/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.program.Program;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramFile")
@SessionScoped
public class ProgramFile extends Program {
    
    @Inject private FormFileFinder formFileFinder;
    @Inject private FormFileEditor formFileEditor;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void resetAll(){
        this.formFileFinder.reset();
        this.formFileFinder.reset();
    }
    
    public void loadFileInEditingPanel(){
        this.formFileEditor.setTestName("File loaded");
    }

    public FormFileFinder getFormFileFinder() {
        return formFileFinder;
    }

    public void setFormFileFinder(FormFileFinder formFileFinder) {
        this.formFileFinder = formFileFinder;
    }

    public FormFileEditor getFormFileEditor() {
        return formFileEditor;
    }

    public void setFormFileEditor(FormFileEditor formFileEditor) {
        this.formFileEditor = formFileEditor;
    }
    
    
}
