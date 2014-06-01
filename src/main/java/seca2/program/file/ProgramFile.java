/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.file.FileEntity;
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
    @Inject private FormFileUploader formFileUploader;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void resetAll(){
        this.formFileFinder.reset();
        this.formFileEditor.reset();
    }
    
    public void loadFileInEditingPanel(long selectedFileId){
        System.out.println("File id selected: "+selectedFileId);//debug

        //find the selected file and set the wrapper to selected
        for(FileMenuWrapper wrappedFile:formFileFinder.getWrappedResults()){
           FileEntity file = wrappedFile.getWrappedFile();
           if(selectedFileId == file.getFILE_ID()){//this is the selected file
               wrappedFile.select();
               wrappedFile.setSelectedValue("1");
               this.formFileEditor.reset(); //everytime you load a new file you'll need to reset, but not sure if we want to put it in seEditingFile().
               this.formFileEditor.setEditingFile(file);//load the selected file into FormFileEditor
               this.formFileEditor.loadSequenceForward(); //load the first batch of sequences
               
           }else{
               wrappedFile.unselect();
               wrappedFile.setSelectedValue("0");
           }
           System.out.println("File "+file.getFILENAME()+" has selected "+wrappedFile.getSelectedValue());
        }
        
        
        
        
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

    public FormFileUploader getFormFileUploader() {
        return formFileUploader;
    }

    public void setFormFileUploader(FormFileUploader formFileUploader) {
        this.formFileUploader = formFileUploader;
    }
    
    
    
}
