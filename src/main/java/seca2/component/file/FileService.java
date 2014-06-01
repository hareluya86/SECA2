/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.file;

import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import seca2.component.data.HibernateUtil;
import seca2.entity.file.FileEntity;
import seca2.entity.file.FileSequence;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class FileService {
    
    @Inject private HibernateUtil hibernateUtil;
    
    private Session session;
    
    /**
     * Checks if filename already exist
     * <p>
     * In order to keep it simple, we just deemed filename same => file same.
     * <p>
     * @param file
     * @return 
     */
    public FileEntity checkFileExists(FileEntity file){
        if(session == null || !session.isOpen()) 
            session = hibernateUtil.getSession();
        List<FileEntity> results;
        String hashValueQuery = "SELECT file "
                                + "FROM FileEntity file "
                                + "WHERE "
                                    /*+ "file.MD5_HASH = '"+file.getMD5_HASH()+"' AND "
                                    + "file.FILE_SIZE_BYTE = "+file.getFILE_SIZE_BYTE()+" AND "
                                    + "file.NUM_OF_SEQUENCE = "+file.getNUM_OF_SEQUENCE()+" AND "*/
                                    + "file.FILENAME = '"+file.getFILENAME()+ "'";
        Query q = session.createQuery(hashValueQuery);
        results = q.list();
        if(results.size() <= 0 ){
            return null;
        }
        else{
            return results.get(0);
        }
    }
    
    public List<FileEntity> searchFileByName(String searchName){
        if(session == null || !session.isOpen()) 
            session = hibernateUtil.getSession();
        List<FileEntity> results = session.createCriteria(FileEntity.class)
                .add(Restrictions.ilike("FILENAME", "%"+searchName+"%"))
                //.add(Restrictions.ge("DATE_CREATED", searchStartDate))
                //.add(Restrictions.le("DATE_CREATED", searchEndDate))
                .list();
        
        return results;
    }
    
    public List<FileSequence> getSequences(long fileId, long start, long end){
        if(session == null || !session.isOpen()) 
            session = hibernateUtil.getSession();
        List<FileSequence> results = session.createCriteria(FileSequence.class)
                .add(Restrictions.eq("FILE.FILE_ID", fileId))
                .add(Restrictions.ge("ORIGINAL_LINE_NUM", start))
                .add(Restrictions.le("ORIGINAL_LINE_NUM", end))
                .list();
        
        return results;
    }
}
