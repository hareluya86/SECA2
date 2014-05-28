/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author vincent.a.lee
 */
@Entity
@Table(name="PROGRAM")
@TableGenerator(name="PROGRAM_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class Program implements Serializable{
    private Program PARENT_PROGRAM;
    
    private long PROGRAM_ID;
    private String PROGRAM_NAME;
    private String VIEW_DIRECTORY;
    private String VIEW_ROOT;
    private String BEAN_DIRECTORY;

    @Id @GeneratedValue(generator="PROGRAM_SEQ",strategy=GenerationType.TABLE)
    public long getPROGRAM_ID() {
        return PROGRAM_ID;
    }

    public void setPROGRAM_ID(long PROGRAM_ID) {
        this.PROGRAM_ID = PROGRAM_ID;
    }

    public String getPROGRAM_NAME() {
        return PROGRAM_NAME;
    }

    public void setPROGRAM_NAME(String PROGRAM_NAME) {
        this.PROGRAM_NAME = PROGRAM_NAME;
    }

    public String getVIEW_DIRECTORY() {
        return VIEW_DIRECTORY;
    }

    public void setVIEW_DIRECTORY(String VIEW_DIRECTORY) {
        this.VIEW_DIRECTORY = VIEW_DIRECTORY;
    }

    public String getBEAN_DIRECTORY() {
        return BEAN_DIRECTORY;
    }

    public void setBEAN_DIRECTORY(String BEAN_DIRECTORY) {
        this.BEAN_DIRECTORY = BEAN_DIRECTORY;
    }

    public String getVIEW_ROOT() {
        return VIEW_ROOT;
    }

    public void setVIEW_ROOT(String VIEW_ROOT) {
        this.VIEW_ROOT = VIEW_ROOT;
    }

    
}
