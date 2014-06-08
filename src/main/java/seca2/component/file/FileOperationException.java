/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.file;

import seca2.program.file.*;

/**
 *
 * @author vincent.a.lee
 */
public class FileOperationException extends Exception{

    public FileOperationException(String string) {
        super(string);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
