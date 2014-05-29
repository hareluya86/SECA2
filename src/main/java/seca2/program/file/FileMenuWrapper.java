/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.util.ArrayList;
import java.util.List;
import seca2.entity.file.FileEntity;

/**
 *
 * @author KH
 */
public class FileMenuWrapper {
    
    private FileEntity wrappedFile;
    
    public List<String> getDropdownMenu(){
        List<String> dropdownMenu = new ArrayList<String>();
        
        dropdownMenu.add()
    }

    public FileEntity getWrappedFile() {
        return wrappedFile;
    }

    public void setWrappedFile(FileEntity wrappedFile) {
        this.wrappedFile = wrappedFile;
    }
    
    
}
