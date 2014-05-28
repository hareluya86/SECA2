/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Part;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import seca2.component.data.HibernateUtil;
import seca2.entity.file.FileEntity;
import static seca2.entity.file.FileEntity.FILE_STATUS.COMPLETED;
import static seca2.entity.file.FileEntity.FILE_STATUS.INCOMPLETE;
import seca2.entity.file.FileSequence;
import seca2.program.messenger.FacesMessenger;


/**
 * Subprogram that processes file and sequence objects
 * <p>
 * 
 * @author vincent.a.lee
 */
//@Named("FileUploader")
//@SessionScoped //if it can only be sessionscoped, then it's best to embed it in another session scope object
public class FormFileUploader implements Serializable {
    
    private final long MAX_RECORD_FLUSH = 100000;
    private final long MAX_FLUSH_COMMIT = 3;
    
    @Inject
    private HibernateUtil hibernateUtil;
    private UploadedFile uploadedFile;
    private FileEntity holdingFile;
    
    private String startUpload;
    
    private String insertButtonValue;
    private boolean showInsertButton;
    private boolean disableInsertButton;
    private boolean showRetryButton;
    private String insertFileMessage;
    
    private String messageBoxId = "upload-form";
    
    @PostConstruct
    public void init(){
        insertButtonValue = "";
        showInsertButton = false;
        disableInsertButton = true;
        insertFileMessage = "";
    }

    
    /**
     * 1) Validate file:
     *      - Make sure each line is of the same length
     *      - Compute checksum
     *      - Compute size (both number of bytes and sequences)
     *      - Compare with existing file and determine if insert button should be a "Insert new file"/"Resume existing file"/"File already uploaded"
     * 2) Create/retrieve file reference from database to be used later for insertion of sequences
     * 3) 
     * @param event 
     */
    public void uploadFile(FileUploadEvent event) throws Exception{
        try {
            FileEntity checkedLengthAndChecksum = checkLengthAndComputeChecksum(event.getFile());
            if(checkedLengthAndChecksum == null)
                throw new InvalidFileException("File "+event.getFile().getFileName()+" does not contain equal length sequences!");
            //Compare with existing file and determine if insert button should be a "Insert new file"/"Resume existing file"/"File already uploaded"
            Session session = hibernateUtil.getSession();
            FileEntity existingFile = this.checkFileExists(session, checkedLengthAndChecksum);
            //session.close();
            if(existingFile != null){
                System.out.println("Existing file "+existingFile.getFILENAME()+" found");//debug
                
                if(!existingFile.getMD5_HASH().equals(checkedLengthAndChecksum.getMD5_HASH())){ //corrupted file
                    System.out.println("Existing file "+existingFile.getFILENAME()+" found but corrupted. "
                            + "You may want to delete the file and re-upload it again.");//debug
                    throw new InvalidFileException("Existing file "+existingFile.getFILENAME()+" found but corrupted. "
                            + "You may want to delete the file and re-upload it again.");
                }
                    
                switch(existingFile.getUPLOAD_STATUS()){
                    case INCOMPLETE :   FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_INFO,
                                            "File \""+existingFile.getFILENAME()+"\" has been uploaded before."
                                            ,"");
                                        this.insertButtonValue = "Resume upload for file \""+existingFile.getFILENAME()+"\"";
                                        this.showInsertButton = true;
                                        this.disableInsertButton = false;
                                        break;
                    case COMPLETED  :   FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_INFO,
                                            "Uploading has already completed for this file \""+existingFile.getFILENAME()+"\""
                                            ,"");
                                        this.showInsertButton = false;
                                        this.disableInsertButton = true;
                                        this.holdingFile = null;
                                        this.uploadedFile = null;
                                        
                                        break;
                    default         :   throw new RuntimeException("Unknown UPLOAD_STATUS for file "+existingFile.getFILENAME());
                }
            } else {
                System.out.println("No existing file found");//debug
                this.insertButtonValue = "Insert new file \""+event.getFile().getFileName()+"\"";
                this.showInsertButton = true;
                this.disableInsertButton = false;
                existingFile = checkedLengthAndChecksum;
            }
            //Hold temp files reference in memory *very impt!*
            this.holdingFile = existingFile;
            this.uploadedFile = event.getFile();
            
        }  catch (InvalidFileException ifex){
            FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_ERROR,ifex.getMessage(),"");
            this.showInsertButton = false;
        } catch(JDBCConnectionException jdbcex){
            FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_ERROR,"Database connection error!",jdbcex.getMessage());
            this.showInsertButton = false;
        } catch (Exception ex) {//all unknown Exceptions
            //setFacesMessage(FacesMessage.SEVERITY_ERROR,ex.getClass().getName(),"");
            //this.showInsertButton = false;
            //throw unknown exceptions out to the front
            this.cancel();
            throw ex;
        } finally {
            
        }
    }
    /**
     * Returns an initialized FileEntity object if it passes both length and checksum validation.
     * Else returns null.
     * <p>
     * @param uploadedFile
     * @return
     * @throws Exception 
     */
    public FileEntity checkLengthAndComputeChecksum(UploadedFile uploadedFile) throws Exception{
        FileEntity checkedFile;
        String md5Checksum = "";
        int lineNum = 0;
        long fileSize = uploadedFile.getSize();
        MessageDigest md = MessageDigest.getInstance("MD5");
        InputStream is = uploadedFile.getInputstream();
        DigestInputStream dis = new DigestInputStream(is, md);

        BufferedReader bReader = new BufferedReader(new InputStreamReader(dis));
        String line = new String();

        int prevLineSize = 0;
        while((line=bReader.readLine())!=null){
            lineNum++;
            if(prevLineSize > 0 && prevLineSize != line.length()){ //last line is empty although gvim shows no last line
                System.out.println(line);
                return null;
            }
            prevLineSize = line.length();
        }
        byte[] digest = md.digest();// dis.getMessageDigest().digest();
        /*for(int i=0; i<digest.length; i++){//debug
            System.out.print(digest[i]);
        }debug*/
        md5Checksum = String.format("%032x", new BigInteger(digest));
        System.out.println("MD5 hash: "+md5Checksum);
            
        //initialize temp FileEntity set all variables to proceed with the remaining checks
        checkedFile = this.createNewFile(uploadedFile.getFileName());
        checkedFile.setFILE_SIZE_BYTE(fileSize);
        checkedFile.setNUM_OF_SEQUENCE(lineNum);
        checkedFile.setMD5_HASH(md5Checksum);
        checkedFile.setLINE_SIZE(prevLineSize);
        
        return checkedFile;
    }
    
    /**
     * Helper to set Faces message
     * @param clientId
     * @param level
     * @param headline
     * @param description 
     *
    public void setFacesMessage(String clientId, FacesMessage.Severity level, String headline, String description){
        FacesMessage msg = new FacesMessage(level,headline,description);
        FacesContext.getCurrentInstance().addMessage(clientId, msg);
    }*/
    
    /**
     * 
     */
    public void insertFileAndSequences() {
        System.out.println(holdingFile.getFILENAME());
        DateTime startTime = new DateTime();
        Session session = hibernateUtil.getSession();
        FileEntity insertThisFile = this.holdingFile;
        UploadedFile fileContents = this.uploadedFile;

        //resume from the sequence number where the upload has stopped
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(fileContents.getInputstream()));
            String lineSequence = new String();
            
            //move bufferedreader to the last line read
            long lineNum = 0;//insertThisFile.getLAST_SEQUENCE();
            //bReader.skip(lineNum*insertThisFile.getLINE_SIZE());
            
            session.getTransaction().begin();
            session.saveOrUpdate(insertThisFile);
            while((lineSequence=bReader.readLine())!=null){
                
                if(++lineNum <= insertThisFile.getLAST_SEQUENCE())
                    continue; //skip to the last inserted sequence
                FileSequence nextSequence = this.addSequence(insertThisFile, lineSequence);
                session.save(nextSequence);
                if(insertThisFile.getLAST_SEQUENCE()%MAX_RECORD_FLUSH == 0){
                    System.out.println("Flush at: "+lineSequence);
                    //session.update(insertThisFile);//test whether insertThisFile is auto-managed or needs to be updated explicitly
                    session.flush();
                    session.clear();
                    if(insertThisFile.getLAST_SEQUENCE()%(MAX_FLUSH_COMMIT*MAX_RECORD_FLUSH) == 0){
                        System.out.println("Commit at: "+lineSequence);
                        session.saveOrUpdate(insertThisFile);
                        session.getTransaction().commit();
                        session.clear();
                        session.getTransaction().begin();
                        
                        //Update view with progress
                        this.insertFileMessage = insertThisFile.getLAST_SEQUENCE() + " out of "
                                + insertThisFile.getNUM_OF_SEQUENCE() + " uploaded successfully!";
                    }
                }
            }
            //commit
            session.saveOrUpdate(insertThisFile);
            session.getTransaction().commit();
            session.clear();
            DateTime endTime = new DateTime();
            System.out.println("Start at "+startTime+" and End at "+endTime);
            FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_INFO, insertThisFile.getFILENAME()+" has been successfully uploaded", "");
        } catch (IOException ex) {
            //Logger.getLogger(ProgramFile.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_ERROR, ex.getClass().getName(), ex.getMessage());
        } catch(java.lang.OutOfMemoryError e){
            FacesMessenger.setFacesMessage(messageBoxId,FacesMessage.SEVERITY_ERROR, e.getClass().getName(), e.getMessage());
        } catch (Exception e) {
            throw e;
        } finally { //clear all temp files and variables
            this.cancel();
        }
    }
    
    public FileEntity createNewFile(String filename){
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFILENAME(filename);
        fileEntity.setNUM_OF_SEQUENCE(0);
        fileEntity.setFILE_SIZE_BYTE(0);
        fileEntity.setLAST_SEQUENCE(0);
        fileEntity.setUPLOAD_STATUS(INCOMPLETE);
        
        return fileEntity;
    };
    
    public FileSequence addSequence(FileEntity file, FileSequence sequence){
        if(file.getUPLOAD_STATUS()==COMPLETED)
            throw new RuntimeException("File "+file.getFILENAME()+" has completed uploading and cannot be overwritten. "
                    + "Please delete file and re-upload again.");
        sequence.setFILE(file);
        //file.getSequences().add(sequence);//Because you will end up with a list of millions of objects in memory
        file.setLAST_SEQUENCE(file.getLAST_SEQUENCE()+1);
        sequence.setORIGINAL_LINE_NUM(file.getLAST_SEQUENCE());
        sequence.setCURRENT_LINE_NUM(file.getLAST_SEQUENCE());
        if(file.getNUM_OF_SEQUENCE() <= file.getLAST_SEQUENCE())
            file.setUPLOAD_STATUS(COMPLETED);
        return sequence;
    };
    
    public FileSequence addSequence(FileEntity file, String sequenceContent){
        FileSequence sequence = new FileSequence();
        sequence.setSEQUENCE_CONTENT(sequenceContent);
        sequence.setSTATUS(FileSequence.SEQUENCE_STATUS.ACTIVE);
        return addSequence(file,sequence);
    }
    
    /**
     * Based on the following criteria, return an existing FileEntity or null if it doesn't fulfill ALL criteria:
     * <p>
     * <strong>1) Hash value</strong>
     * <ul>
     * <li>If no existing file with given hash value, return null</li>
     * <li>If existing files with given hash value, proceed to the next check</li>
     * </ul>
     * <strong>2) File size</strong> [REDUNDANT?]
     * <ul>
     * <li>If no existing files with given file size, return null</li>
     * <li>If existing files with given file size, proceed to the next check</li>
     * </ul>
     * <strong>3) Number of sequences</strong> [REDUNDANT?]
     * <ul>
     * <li>If no existing files with given number of sequences, return null</li>
     * <li>If existing files with given file number of sequences, proceed to the next check</li>
     * </ul>
     * <strong>4) Filename</strong>
     * <ul>
     * <li>If no existing files with given filename, return null</li>
     * <li>If existing files with given filename, proceed to the next check</li>
     * </ul>
     * <p>
     * WARNING: This is not a 100% method, there is a chance that a file with different content 
     * but with the same hash, size, number of sequences and name is returned. If multiple files 
     * are found that fulfills all the above conditions as the given file, the first result will
     * be returned.
     * 
     * @param session
     * @param file
     * @return 
     */
    public FileEntity checkFileExists(Session session, FileEntity file){
        List<FileEntity> results;
        String hashValueQuery = "SELECT file "
                                + "FROM FileEntity file "
                                + "WHERE "
                                    /*+ "file.MD5_HASH = '"+file.getMD5_HASH()+"' AND "
                                    + "file.FILE_SIZE_BYTE = "+file.getFILE_SIZE_BYTE()+" AND "
                                    + "file.NUM_OF_SEQUENCE = "+file.getNUM_OF_SEQUENCE()+" AND "*/
                                    + "file.FILENAME = '"+file.getFILENAME()+ "'";
        Query q = session.createQuery(hashValueQuery);
        results = q.list();
        if(results.size() <= 0 ){
            return null;
        }
        else{
            return results.get(0);
        }
    }
    
    /**
     * Cancels the entire process and clears all temp variables/storage.
     * 
     * @throws IOException 
     */
    public void cancel() {
        this.uploadedFile = null;
        this.holdingFile = null;
        this.disableInsertButton = true;
        this.showInsertButton = false;
        
        System.gc();
    }
    
    public static String bytesToStringUTFCustom(byte[] bytes) {
        char[] buffer = new char[bytes.length >> 1];
        for (int i = 0; i < buffer.length; i++) {
            int bpos = i << 1;
            char c = (char) (((bytes[bpos] & 0x00FF) << 8) + (bytes[bpos + 1] & 0x00FF));
            buffer[i] = c;
        }
        return new String(buffer);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Getters and Setters...">
    
    public HibernateUtil getHibernateUtil() {
        return hibernateUtil;
    }

    public void setHibernateUtil(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getStartUpload() {
        return startUpload;
    }

    public void setStartUpload(String startUpload) {
        this.startUpload = startUpload;
    }

    public FileEntity getHoldingFile() {
        return holdingFile;
    }

    public void setHoldingFile(FileEntity holdingFile) {
        this.holdingFile = holdingFile;
    }

    public String getInsertButtonValue() {
        return insertButtonValue;
    }

    public void setInsertButtonValue(String insertButtonValue) {
        this.insertButtonValue = insertButtonValue;
    }

    public boolean isDisableInsertButton() {
        return disableInsertButton;
    }

    public void setDisableInsertButton(boolean disableInsertButton) {
        this.disableInsertButton = disableInsertButton;
    }

    public boolean isShowInsertButton() {
        return showInsertButton;
    }

    public void setShowInsertButton(boolean showInsertButton) {
        this.showInsertButton = showInsertButton;
    }

    public boolean isShowRetryButton() {
        return showRetryButton;
    }

    public void setShowRetryButton(boolean showRetryButton) {
        this.showRetryButton = showRetryButton;
    }

    public String getInsertFileMessage() {
        return insertFileMessage;
    }

    public void setInsertFileMessage(String insertFileMessage) {
        this.insertFileMessage = insertFileMessage;
    }
    // </editor-fold>
    public void startFileUpload(){
        this.startUpload = "File upload has started";
        System.out.println(this.startUpload);
    }
}
