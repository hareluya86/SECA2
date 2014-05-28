/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.file;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="FILESEQUENCE")
public class FileSequence implements Serializable /*extends EnterpriseData*/ {

    private FileEntity FILE;
    private long ORIGINAL_LINE_NUM;
    private long CURRENT_LINE_NUM;
    private String SEQUENCE_CONTENT;
    private SEQUENCE_STATUS STATUS;
    
    public static enum SEQUENCE_STATUS{
        ACTIVE,
        REMOVED
    }

    @Id @ManyToOne
    public FileEntity getFILE() {
        return FILE;
    }

    public void setFILE(FileEntity FILE) {
        this.FILE = FILE;
    }

    public long getCURRENT_LINE_NUM() {
        return CURRENT_LINE_NUM;
    }

    public void setCURRENT_LINE_NUM(long CURRENT_LINE_NUM) {
        this.CURRENT_LINE_NUM = CURRENT_LINE_NUM;
    }

    public String getSEQUENCE_CONTENT() {
        return SEQUENCE_CONTENT;
    }

    public void setSEQUENCE_CONTENT(String SEQUENCE_CONTENT) {
        this.SEQUENCE_CONTENT = SEQUENCE_CONTENT;
    }

    @Enumerated(EnumType.STRING)
    public SEQUENCE_STATUS getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(SEQUENCE_STATUS STATUS) {
        this.STATUS = STATUS;
    }
    
    
    /*
    @Override
    public void randInit() {
        LocalDate ld = new LocalDate();
        java.sql.Date sqlDate = new java.sql.Date(ld.toDate().getTime());
        int user = (int)(Math.random()*12345);
        
        this.setCREATED_BY("User "+user);
        this.setCREATED_BY(CREATED_BY);
    }

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
        
        map.put("CURRENT_LINE_NUM", CURRENT_LINE_NUM);
        map.put("SEQUENCE_CONTENT", SEQUENCE_CONTENT);
        map.put("STATUS", STATUS);
        
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

    @Override
    public Object key() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */

    @Id
    public long getORIGINAL_LINE_NUM() {
        return ORIGINAL_LINE_NUM;
    }

    public void setORIGINAL_LINE_NUM(long ORIGINAL_LINE_NUM) {
        this.ORIGINAL_LINE_NUM = ORIGINAL_LINE_NUM;
    }
}
