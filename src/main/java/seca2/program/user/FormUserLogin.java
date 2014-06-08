/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.user;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.exception.JDBCConnectionException;
import org.joda.time.DateTime;
import seca2.bootstrap.module.UserModule;
import seca2.component.user.UserAccountLockedException;
import seca2.component.user.UserService;
import seca2.entity.user.UserEntity;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormUserLogin")
@RequestScoped
public class FormUserLogin {
    
    @EJB private UserService userService;
    @Inject private UserModule userModule; //to check if there was a previous URL to be redirected and set the sessionID
    
    private String username;
    private String password;
    
    private final String messageBoxId = "form-user-login";
    
    public void login(String sessionId) throws IOException {

        //Check if username and password are present
        if (username == null || username.isEmpty()) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Please enter username", null);
            return;
        }
        if (password == null || password.isEmpty()) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Please enter password", null);
            return;
        }
        UserEntity user = null;
        try {
            //use UserService to login
            user = userService.login(username, password);
        } catch (UserAccountLockedException ex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Oops...Your account has been locked. Please contact administrator to unlock it.", null);
            return;
        } catch (EJBException ejbex) {
            String message = ejbex.getCause().getMessage();
            if (ejbex.getCause() instanceof JDBCConnectionException) {
                message = "There was a problem connecting to the database. Please try again later.";
            }

            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, message, null);
            return;
        }

        if (user == null) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Wrong credentials. Are you sure you entered the correct credentials?",
                    "Alternatively, would you like to created a new account? ");
            return;
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        //HttpServletRequest req = (HttpServletRequest) ec.getRequest();
        //HttpServletResponse resp = (HttpServletResponse) ec.getResponse();

        //HttpSession session = req.getSession(true);
        //session.setAttribute("user", 1);
        userModule.setsSessionId(sessionId);
        //sSessionId = session.getId();
        DateTime sessionStarttime = new DateTime();
        System.out.println("Session " + userModule.getsSessionId() + " started at " + sessionStarttime);
        password = "";
        username = "";

        //do a redirect to refresh the view
        String previousURI = userModule.getPreviousURI();
        if (previousURI != null && !previousURI.isEmpty()) {
            ec.redirect(previousURI);
        } else {
            ec.redirect(ec.getRequestContextPath());//go to home
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
