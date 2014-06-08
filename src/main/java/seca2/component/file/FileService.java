/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import seca2.component.data.HibernateUtil;
import seca2.entity.file.FileEntity;
import static seca2.entity.file.FileEntity.FILE_STATUS.*;
import seca2.entity.file.FileSequence;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class FileService {

    private final long MAX_RECORD_FLUSH = 100000;
    private final long MAX_FLUSH_COMMIT = 3;

    @Inject
    private HibernateUtil hibernateUtil;

    private Session session;

    /**
     * Checks if filename already exist
     * <p>
     * In order to keep it simple, we just deemed filename same => file same.
     * <p>
     * @param file
     * @return
     */
    public FileEntity checkFileExists(FileEntity file) {
        if (session == null || !session.isOpen()) {
            session = hibernateUtil.getSession();
        }
        List<FileEntity> results;
        String hashValueQuery = "SELECT file "
                + "FROM FileEntity file "
                + "WHERE "
                /*+ "file.MD5_HASH = '"+file.getMD5_HASH()+"' AND "
                 + "file.FILE_SIZE_BYTE = "+file.getFILE_SIZE_BYTE()+" AND "
                 + "file.NUM_OF_SEQUENCE = "+file.getNUM_OF_SEQUENCE()+" AND "*/
                + "file.FILENAME = '" + file.getFILENAME() + "'";
        Query q = session.createQuery(hashValueQuery);
        results = q.list();
        if (results.size() <= 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public List<FileEntity> searchFileByName(String searchName) {
        if (session == null || !session.isOpen()) {
            session = hibernateUtil.getSession();
        }
        List<FileEntity> results = session.createCriteria(FileEntity.class)
                .add(Restrictions.ilike("FILENAME", "%" + searchName + "%"))
                //.add(Restrictions.ge("DATE_CREATED", searchStartDate))
                //.add(Restrictions.le("DATE_CREATED", searchEndDate))
                .list();

        return results;
    }

    public List<FileSequence> getSequences(long fileId, long start, long end) {
        if (session == null || !session.isOpen()) {
            session = hibernateUtil.getSession();
        }
        List<FileSequence> results = session.createCriteria(FileSequence.class)
                .add(Restrictions.eq("FILE.FILE_ID", fileId))
                .add(Restrictions.ge("ORIGINAL_LINE_NUM", start))
                .add(Restrictions.le("ORIGINAL_LINE_NUM", end))
                .list();

        return results;
    }

    @Asynchronous
    public Future<String> testAsyncInsertion(String jobname,FileEntity holdingFile, InputStream is) throws IOException{
        this.insertFileAndSequences(holdingFile, is);
        
        return new AsyncResult<String>(jobname);
    }
    
    public void insertFileAndSequences(FileEntity holdingFile, InputStream is) throws IOException {
        System.out.println(holdingFile.getFILENAME());
        DateTime startTime = new DateTime();
        
        if (session == null || !session.isOpen()) {
            session = hibernateUtil.getSession();
        }
        FileEntity insertThisFile = holdingFile;

        //resume from the sequence number where the upload has stopped
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        String lineSequence = new String();

        //move bufferedreader to the last line read
        long lineNum = 0;//insertThisFile.getLAST_SEQUENCE();
        //bReader.skip(lineNum*insertThisFile.getLINE_SIZE());

        session.getTransaction().begin();
        session.saveOrUpdate(insertThisFile);
        while ((lineSequence = bReader.readLine()) != null) {

            if (++lineNum <= insertThisFile.getLAST_SEQUENCE()) {
                continue; //skip to the last inserted sequence
            }
            FileSequence nextSequence = this.addSequence(insertThisFile, lineSequence);
            session.save(nextSequence);
            if (insertThisFile.getLAST_SEQUENCE() % MAX_RECORD_FLUSH == 0) {
                System.out.println("Flush at: " + lineSequence);
                //session.update(insertThisFile);//test whether insertThisFile is auto-managed or needs to be updated explicitly
                session.flush();
                session.clear();
                if (insertThisFile.getLAST_SEQUENCE() % (MAX_FLUSH_COMMIT * MAX_RECORD_FLUSH) == 0) {
                    System.out.println("Commit at: " + lineSequence);
                    session.saveOrUpdate(insertThisFile);
                    session.getTransaction().commit();
                    session.clear();
                    session.getTransaction().begin();
                }
            }
        }
        //commit
        session.saveOrUpdate(insertThisFile);
        session.getTransaction().commit();
        session.clear();
        DateTime endTime = new DateTime();
        System.out.println("Start at " + startTime + " and End at " + endTime);
    }

    public FileSequence addSequence(FileEntity file, FileSequence sequence) {
        if (file.getUPLOAD_STATUS() == COMPLETED) {
            throw new RuntimeException("File " + file.getFILENAME() + " has completed uploading and cannot be overwritten. "
                    + "Please delete file and re-upload again.");
        }
        sequence.setFILE(file);
        //file.getSequences().add(sequence);//Because you will end up with a list of millions of objects in memory
        file.setLAST_SEQUENCE(file.getLAST_SEQUENCE() + 1);
        file.setREMAINING_SEQUENCE(file.getLAST_SEQUENCE());
        sequence.setORIGINAL_LINE_NUM(file.getLAST_SEQUENCE());
        sequence.setCURRENT_LINE_NUM(file.getLAST_SEQUENCE());
        if (file.getNUM_OF_SEQUENCE() <= file.getLAST_SEQUENCE()) {
            file.setUPLOAD_STATUS(COMPLETED);
        }
        return sequence;
    }

    ;
    
    public FileSequence addSequence(FileEntity file, String sequenceContent) {
        FileSequence sequence = new FileSequence();
        sequence.setSEQUENCE_CONTENT(sequenceContent);
        sequence.setSTATUS(FileSequence.SEQUENCE_STATUS.ACTIVE);
        return addSequence(file, sequence);
    }

    /**
     * Returns an initialized FileEntity object if it passes both length and
     * checksum validation. Else returns null.
     * <p>
     * This is a condensed method that will:
     * <ul>
     * <li>1) Check if length of every line in the file is the same</li>
     * <li>2) Compute checksum</li>
     * <li>3) Initialize file</li>
     * </ul>
     * In light of best practices, this is not a good implementation, but we did 
     * it for the purpose of performance.
     * <p>
     * @param filename
     * @param is
     * @return
     * @throws seca2.component.file.FileOperationException
     */
    public FileEntity checkLengthAndComputeChecksum(String filename, InputStream is) throws FileOperationException, InvalidFileException {
        FileEntity checkedFile;
        String md5Checksum = "";
        int lineNum = 0;
        //long fileSize = uploadedFile.getSize();
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            //InputStream is = uploadedFile.getInputstream();
            DigestInputStream dis = new DigestInputStream(is, md);

            BufferedReader bReader = new BufferedReader(new InputStreamReader(dis));
            String line = new String();

            int prevLineSize = 0;
            while ((line = bReader.readLine()) != null) {
                lineNum++;
                if (prevLineSize > 0 && prevLineSize != line.length()) { //last line is empty although gvim shows no last line
                    System.out.println(line);
                    throw new InvalidFileException("File does not contain equal length sequences!");
                }
                prevLineSize = line.length();
            }
            byte[] digest = md.digest();
            md5Checksum = String.format("%032x", new BigInteger(digest));
            System.out.println("MD5 hash: " + md5Checksum);
            
            //initialize temp FileEntity set all variables to proceed with the remaining checks
            checkedFile = this.createNewFile(filename);
            //checkedFile.setFILE_SIZE_BYTE(fileSize);
            checkedFile.setNUM_OF_SEQUENCE(lineNum);
            checkedFile.setMD5_HASH(md5Checksum);
            checkedFile.setLINE_SIZE(prevLineSize);
            
        } catch(NoSuchAlgorithmException nsaex){
             throw new FileOperationException("Checksum error!",nsaex);
        } catch (IOException ioex){
            throw new FileOperationException("Input stream I/O error!",ioex);
        } 
        return checkedFile;
    }

    public FileEntity createNewFile(String filename) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFILENAME(filename);
        fileEntity.setNUM_OF_SEQUENCE(0);
        fileEntity.setFILE_SIZE_BYTE(0);
        fileEntity.setLAST_SEQUENCE(0);
        fileEntity.setREMAINING_SEQUENCE(0);
        fileEntity.setUPLOAD_STATUS(INCOMPLETE);

        return fileEntity;
    }
}
