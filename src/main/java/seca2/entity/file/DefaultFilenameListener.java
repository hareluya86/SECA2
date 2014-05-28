/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.file;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author KH
 */
public class DefaultFilenameListener {
    
    private final String DEFAULT_FILENAME = "FILE";
    
    @PrePersist
    @PreUpdate
    public void defaultFilename(FileEntity file){
        //If filename is not set, empty or contains only whitespaces, give it the 
        //default filename
        if(file.getFILENAME() == null ||
                file.getFILENAME().isEmpty() || 
                file.getFILENAME().trim().length() > 0) {
            DateTime dt = new DateTime();
            file.setFILENAME(DEFAULT_FILENAME.concat(dt.toString()));
        }
            
    }
}
