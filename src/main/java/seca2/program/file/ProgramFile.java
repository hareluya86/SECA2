/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

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

    public FormFileFinder getFormFileFinder() {
        return formFileFinder;
    }

    public void setFormFileFinder(FormFileFinder formFileFinder) {
        this.formFileFinder = formFileFinder;
    }
}
