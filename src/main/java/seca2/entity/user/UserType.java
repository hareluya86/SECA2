/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.user;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author KH
 */
@Entity
@Table(name="USERTYPE")
@TableGenerator(name="USERTYPE_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class UserType implements Serializable {
    
    private long USERTYPEID;
    private String USERTYPENAME;
    private String DESCRIPTION;
    
    /**
     * Not in use yet
     */
    private boolean PORTAL_ACCESS;
    private boolean WS_ACCESS;

    @Id @GeneratedValue(generator="USERTYPE_SEQ",strategy=GenerationType.TABLE)
    public long getUSERTYPEID() {
        return USERTYPEID;
    }

    public void setUSERTYPEID(long USERTYPEID) {
        this.USERTYPEID = USERTYPEID;
    }

    public String getUSERTYPENAME() {
        return USERTYPENAME;
    }

    public void setUSERTYPENAME(String USERTYPENAME) {
        this.USERTYPENAME = USERTYPENAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public boolean isPORTAL_ACCESS() {
        return PORTAL_ACCESS;
    }

    public void setPORTAL_ACCESS(boolean PORTAL_ACCESS) {
        this.PORTAL_ACCESS = PORTAL_ACCESS;
    }

    public boolean isWS_ACCESS() {
        return WS_ACCESS;
    }

    public void setWS_ACCESS(boolean WS_ACCESS) {
        this.WS_ACCESS = WS_ACCESS;
    }
    
    
}
