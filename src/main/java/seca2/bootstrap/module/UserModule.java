/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author vincent.a.lee
 */
@SessionScoped
public class UserModule implements Serializable{
    
    private String sSessionId;
    private String previousURI;
    private final String loginContainerName = "login-form:loginbox-container"; // should not be here!
    
    public boolean checkSessionActive() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();
        
        fc.getPartialViewContext().getRenderIds().add(loginContainerName);
        
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        } else {
            if (sSessionId != null && sSessionId.equals(session.getId())) {
                //hide login block
                //session.setAttribute("user", 1);
                return true;
            } else {
                //pop up login block
                //session.setAttribute("user", 0);
                //store this current requestURI for redirection after login
                String originalURI = (String) req.getAttribute("javax.servlet.forward.request_uri");
                if (originalURI != null || !originalURI.isEmpty()) {
                    this.previousURI = originalURI;
                }
                //Check if application was installed by calling CheeckInstaller.
                /*if (checkInstaller.getStatus() != CheckInstaller.INSTALL_STATUS.INSTALLED) {
                    FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_INFO,
                            "Application is not installed yet, "
                            + "click here to <a href='/install/'>install</a> now", null);
                }*/
                return false;
            }
        }
    }

    public String getsSessionId() {
        return sSessionId;
    }

    public void setsSessionId(String sSessionId) {
        this.sSessionId = sSessionId;
    }

    public String getPreviousURI() {
        return previousURI;
    }

    public void setPreviousURI(String previousURI) {
        this.previousURI = previousURI;
    }
    
    

}
