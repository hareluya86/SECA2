/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.messenger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author KH
 */
public class FacesMessenger {
    
    /**
     * Helper to set Faces message
     * @param clientId
     * @param level
     * @param headline
     * @param description 
     */
    public static void setFacesMessage(String clientId, FacesMessage.Severity level, String headline, String description){
        FacesMessage msg = new FacesMessage(level,headline,description);
        FacesContext.getCurrentInstance().addMessage(clientId, msg);
    }
}
