/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.io.Serializable;

/**
 *
 * @author KH
 */
public class FormFileEditor implements Serializable {
    
    private String testName;
    
    public void reset(){
        testName = "";
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    
}
