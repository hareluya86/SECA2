/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.file;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

/**
 * An entity class that represents a File
 * <p>
 * 
 * 
 * Note: We will be using EDS, but this would not utilize the full features of 
 * EDS. The following features will not be implemented from EDS:
 * - EnterpriseData 
 * 
 * Update: we will not be using EDS anymore, as there are too many problems in 
 * compiling and dependency 
 * @author KH
 */
@Entity
@Table(name="FILEENTITY")
@DiscriminatorValue("FILEENTITY") //for EDS
@TableGenerator(name="FILEENTITY_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE") //No uploads of multiple files, so allocationSize 1 is fine
public class FileEntity implements Serializable /*extends EnterpriseUnit*/ {

    private long FILE_ID;
    private String FILENAME;
    private long FILE_SIZE_BYTE; //Total original file byte size together with newline and carriage returns
    private long NUM_OF_SEQUENCE; //Total number of sequences
    private int LINE_SIZE; //Length of each line
    private long LAST_SEQUENCE; //The last sequence that was uploaded (LAST_SEQUENCE = NUM_OF_SEQUENCE if FILE_STATUS = COMPLETED)
    private long REMAINING_SEQUENCE; //The number of active sequences remaining
    private FILE_STATUS UPLOAD_STATUS; 
    private String MD5_HASH; 
    
    private java.sql.Date DATE_CREATED;
    private String CREATED_BY;
    private java.sql.Date DATE_CHANGED;
    private String CHANGED_BY;
    
    private List<FileSequence> sequences = new ArrayList<FileSequence>();

    public static enum FILE_STATUS{
        INCOMPLETE,
        COMPLETED
    }

    @Id @GeneratedValue(generator="FILEENTITY_SEQ",strategy=GenerationType.TABLE)
    public long getFILE_ID() {
        return FILE_ID;
    }

    public void setFILE_ID(long FILE_ID) {
        this.FILE_ID = FILE_ID;
    }
    
    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    @Enumerated(EnumType.STRING)
    public FILE_STATUS getUPLOAD_STATUS() {
        return UPLOAD_STATUS;
    }

    public void setUPLOAD_STATUS(FILE_STATUS UPLOAD_STATUS) {
        this.UPLOAD_STATUS = UPLOAD_STATUS;
    }

    public long getFILE_SIZE_BYTE() {
        return FILE_SIZE_BYTE;
    }

    public void setFILE_SIZE_BYTE(long FILE_SIZE_BYTE) {
        this.FILE_SIZE_BYTE = FILE_SIZE_BYTE;
    }

    public long getNUM_OF_SEQUENCE() {
        return NUM_OF_SEQUENCE;
    }

    public void setNUM_OF_SEQUENCE(long NUM_OF_SEQUENCE) {
        this.NUM_OF_SEQUENCE = NUM_OF_SEQUENCE;
    }

    public Date getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setDATE_CREATED(Date DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public Date getDATE_CHANGED() {
        return DATE_CHANGED;
    }

    public void setDATE_CHANGED(Date DATE_CHANGED) {
        this.DATE_CHANGED = DATE_CHANGED;
    }

    public String getCHANGED_BY() {
        return CHANGED_BY;
    }

    public void setCHANGED_BY(String CHANGED_BY) {
        this.CHANGED_BY = CHANGED_BY;
    }

    public String getMD5_HASH() {
        return MD5_HASH;
    }

    public void setMD5_HASH(String MD5_HASH) {
        this.MD5_HASH = MD5_HASH;
    }
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy="FILE")
    public List<FileSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<FileSequence> sequences) {
        this.sequences = sequences;
    }

    public long getLAST_SEQUENCE() {
        return LAST_SEQUENCE;
    }

    public void setLAST_SEQUENCE(long LAST_SEQUENCE) {
        this.LAST_SEQUENCE = LAST_SEQUENCE;
    }

    public int getLINE_SIZE() {
        return LINE_SIZE;
    }

    public void setLINE_SIZE(int LINE_SIZE) {
        this.LINE_SIZE = LINE_SIZE;
    }

    public long getREMAINING_SEQUENCE() {
        return REMAINING_SEQUENCE;
    }

    public void setREMAINING_SEQUENCE(long REMAINING_SEQUENCE) {
        this.REMAINING_SEQUENCE = REMAINING_SEQUENCE;
    }
    
    
    /** ============= Methods inherited from EDS ============================
    @Override
    
    public Object key() {
        return this.getFILENAME();
    }
    
    @Override*/
    public void randInit() {
        DateMidnight dm = new DateMidnight();
        LocalDate ld = new LocalDate();
        java.sql.Date sqlDate = new java.sql.Date(ld.toDate().getTime());
        int user = (int)(Math.random()*12345);
        if(Math.random() >0.5) this.setUPLOAD_STATUS(FILE_STATUS.COMPLETED);
        else this.setUPLOAD_STATUS(FILE_STATUS.INCOMPLETE);
        int filename = (user+6)/7;
        
        this.setFILENAME("File "+filename);
        this.setCREATED_BY("User "+user);
    }
    /*
    @Override
    public String tableName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String className() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> exportAsMap() {
        Map<String, Object> map = super.exportAsMap();
        
        map.put("FILENAME", FILENAME);
        map.put("FILE_SIZE_BYTE",FILE_SIZE_BYTE);
        map.put("NUM_OF_SEQUENCE",NUM_OF_SEQUENCE);
        map.put("FILE_STATUS",STATUS);    
        
        return map;
    }

    @Override
    public String exportAsString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List exportAsList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */
    
}
