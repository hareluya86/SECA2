/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.JDBCConnectionException;
import seca2.component.data.HibernateUtil;
import seca2.component.file.FileService;
import seca2.entity.file.FileEntity;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
public class FormFileFinder implements Serializable {
    
    //UI elements
    private String searchName;
    private Date searchStartDate;
    private Date searchEndDate;
    private List<FileEntity> results;
    private String FORM_ID = "form-file-finder";
    
    @EJB private FileService fileService;
    
    //Dependencies
    //@Inject private EntitySearchDemo entitySearch;
    @Inject private HibernateUtil hibernateUtil;
    
    @PostConstruct
    public void init(){
        results = new ArrayList<FileEntity>();
        //Initialize required criteria
        
    }
    
    public void search(){
        try{
            results = fileService.searchFileByName(searchName);
        } catch(EJBException ejbex){
            String message = ejbex.getCause().getMessage();
            if (ejbex.getCause() instanceof JDBCConnectionException) {
                FacesMessenger.setFacesMessage(FORM_ID,
                    FacesMessage.SEVERITY_ERROR,"Database connection error!",message);
            } else {
                FacesMessenger.setFacesMessage(FORM_ID, FacesMessage.SEVERITY_ERROR, message, null);
            }
        }
    }

    public List<FileEntity> getResults() {
        return results;
    }

    public void setResults(List<FileEntity> results) {
        this.results = results;
    }

    public void reset(){
        this.searchName = "";
        this.searchStartDate = null;
        this.searchEndDate = null;
        this.results = new ArrayList<FileEntity>();
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Date getSearchStartDate() {
        return searchStartDate;
    }

    public void setSearchStartDate(Date searchStartDate) {
        this.searchStartDate = searchStartDate;
    }

    public Date getSearchEndDate() {
        return searchEndDate;
    }

    public void setSearchEndDate(Date searchEndDate) {
        this.searchEndDate = searchEndDate;
    }

    public String getFORM_ID() {
        return FORM_ID;
    }

    public void setFORM_ID(String FORM_ID) {
        this.FORM_ID = FORM_ID;
    }
    
    
}
