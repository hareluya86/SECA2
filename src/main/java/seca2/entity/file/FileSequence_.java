/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.file;

import javax.persistence.metamodel.SingularAttribute;
import seca2.entity.file.FileSequence.SEQUENCE_STATUS;

/**
 *
 * @author KH
 */
public class FileSequence_ /*extends EnterpriseData_*/ {
    
     public static volatile SingularAttribute<FileEntity,Long> CURRENT_LINE_NUM;
     public static volatile SingularAttribute<FileEntity,String> SEQUENCE_CONTENT;
     public static volatile SingularAttribute<FileEntity,SEQUENCE_STATUS> STATUS;
     
}
