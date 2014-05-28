/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.user;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * We're not persisting this at the moment
 * @author vincent.a.lee
 */
@Entity
@Table(name="USERSESSION")
public class UserSession implements Serializable{
    @Id private UserEntity USERID;
    @Id private String SESSIONID;

    public UserEntity getUSERID() {
        return USERID;
    }

    public void setUSERID(UserEntity USERID) {
        this.USERID = USERID;
    }

    public String getSESSIONID() {
        return SESSIONID;
    }

    public void setSESSIONID(String SESSIONID) {
        this.SESSIONID = SESSIONID;
    }
    
    
}
