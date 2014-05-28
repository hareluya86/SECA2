/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.user;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author KH
 */
@Entity
@Table(name="USERENTITY")
@TableGenerator(name="USERENTITY_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class UserEntity implements Serializable {
    
    private long USERID;
    private String USERNAME;
    private String PASSWORD;
    private boolean USER_LOCKED;
    private int UNSUCCESSFUL_ATTEMPTS;
    private java.util.Date LAST_UNSUCCESS_ATTEMPT; //Timestamp
    
    private java.sql.Date DATE_CREATED;
    private String CREATED_BY;
    private java.sql.Date DATE_CHANGED;
    private String CHANGED_BY;
    
    private UserType USERTYPE;

    @Id @GeneratedValue(generator="USERENTITY_SEQ",strategy=GenerationType.TABLE)
    public long getUSERID() {
        return USERID;
    }

    public void setUSERID(long USERID) {
        this.USERID = USERID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public boolean isUSER_LOCKED() {
        return USER_LOCKED;
    }

    public void setUSER_LOCKED(boolean USER_LOCKED) {
        this.USER_LOCKED = USER_LOCKED;
    }

    public int getUNSUCCESSFUL_ATTEMPTS() {
        return UNSUCCESSFUL_ATTEMPTS;
    }

    public void setUNSUCCESSFUL_ATTEMPTS(int UNSUCCESSFUL_ATTEMPTS) {
        this.UNSUCCESSFUL_ATTEMPTS = UNSUCCESSFUL_ATTEMPTS;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public java.util.Date getLAST_UNSUCCESS_ATTEMPT() {
        return LAST_UNSUCCESS_ATTEMPT;
    }

    public void setLAST_UNSUCCESS_ATTEMPT(java.util.Date LAST_UNSUCCESS_ATTEMPT) {
        this.LAST_UNSUCCESS_ATTEMPT = LAST_UNSUCCESS_ATTEMPT;
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

    @ManyToOne
    public UserType getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(UserType USERTYPE) {
        this.USERTYPE = USERTYPE;
    }
    
    
}
