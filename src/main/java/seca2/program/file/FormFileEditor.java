/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import seca2.component.file.FileService;
import seca2.entity.file.FileEntity;
import seca2.entity.file.FileSequence;

/**
 *
 * @author KH
 */
public class FormFileEditor implements Serializable {
    
    private FileEntity editingFile;
    private List<FileSequence> cachedSequences; //Application managed cached list
    
    private int sequenceCachedStart; //Which sequence # to load from DB  (DB Object-specific) [dynamic]
    private int sequenceCachedRows; //How many sequence from start to be loaded from DB [static]
    private int sequenceCacheBlockSize; //Maximum number of DB loads I store at any one time in my application [static]
    
    @EJB private FileService fileService;
    
    @PostConstruct
    public void init(){
        this.reset();
    }
    
    /**
     * Stateless method to pull a new cache block.
     * <p>
     * @param fileService
     * @param fileId
     * @param start
     * @param end
     * @return 
     */
    public List<FileSequence> loadSequencesFromDB(FileService fileService, long fileId, long start, long end){
        return fileService.getSequences(fileId, start, end);
    }
    
    public void loadSequenceForward(){
        
        long fileId = this.editingFile.getFILE_ID();
        long fileSequenceSize = this.editingFile.getNUM_OF_SEQUENCE();
        
        long currentCacheEndIndex = 0; //Last sequence position in the cache
        if(this.cachedSequences.size() > 0){
            currentCacheEndIndex = this.cachedSequences.get(cachedSequences.size()-1).getORIGINAL_LINE_NUM();
        }
        
        //end of file has been reached
        if(currentCacheEndIndex >= fileSequenceSize)
            return;
        
        //Determine the next sequence index to get to
        long nextCacheStartIndex = currentCacheEndIndex + 1;
        long nextCacheEndIndex = nextCacheStartIndex + this.sequenceCachedRows-1;
        if(nextCacheEndIndex > fileSequenceSize){
            nextCacheEndIndex = fileSequenceSize;
        }
        System.out.println("Loading seqeunces from "+nextCacheStartIndex+" to "+nextCacheEndIndex);
        this.cachedSequences.addAll(this.loadSequencesFromDB(fileService, fileId, nextCacheStartIndex, nextCacheEndIndex));
        
        //check if cache size exceeds the cache block size
        if(this.cachedSequences.size() > this.sequenceCacheBlockSize*this.sequenceCachedRows){
            this.cachedSequences = this.cachedSequences.subList(this.sequenceCachedRows, this.cachedSequences.size());
        }
    }
    
    public void loadSequenceBackward(){
        long fileId = this.editingFile.getFILE_ID();
        long fileSequenceSize = this.editingFile.getNUM_OF_SEQUENCE();
        
        long currentCacheStartIndex = 0; //Last sequence position in the cache
        if(this.cachedSequences.size() > 0){
            currentCacheStartIndex = this.cachedSequences.get(0).getORIGINAL_LINE_NUM();
        }
        
        //start of file has been reached
        if(currentCacheStartIndex == fileSequenceSize)
            return;
        
        //Determine the next sequence index to get to
        long nextCacheStartIndex = currentCacheStartIndex - this.sequenceCachedRows; //move 1 block size up
        long nextCacheEndIndex = nextCacheStartIndex + this.sequenceCachedRows-1;
        if(nextCacheStartIndex <= 0){
            nextCacheEndIndex = 0;
        }
        System.out.println("Loading seqeunces from "+nextCacheStartIndex+" to "+nextCacheEndIndex);
        List<FileSequence> temp = loadSequencesFromDB(fileService, fileId, nextCacheStartIndex, nextCacheEndIndex);
        temp.addAll(this.cachedSequences);
        this.cachedSequences = temp;
        
        //check if cache size exceeds the cache block size
        if(this.cachedSequences.size() > this.sequenceCacheBlockSize*this.sequenceCachedRows){
            this.cachedSequences = this.cachedSequences.subList(0, this.sequenceCacheBlockSize*this.sequenceCachedRows);
        }
    }
    
    public void reset(){
        sequenceCachedStart = 0;
        sequenceCachedRows = 500;
        sequenceCacheBlockSize = 3;
        cachedSequences = new ArrayList<>();
        editingFile = null;
    }

    public FileEntity getEditingFile() {
        return editingFile;
    }

    public void setEditingFile(FileEntity editingFile) {
        this.editingFile = editingFile;
    }
    
    public List<FileSequence> getSampleSequence(){
        
        if(editingFile != null){
            return this.editingFile.getSequences();
        }
        return new ArrayList<>();
    }
    
    public List<FileSequence> getCachedSequences() {
        return cachedSequences;
    }

    public void setCachedSequences(List<FileSequence> cachedSequences) {
        this.cachedSequences = cachedSequences;
    }

    public int getSequenceCachedStart() {
        return sequenceCachedStart;
    }

    public void setSequenceCachedStart(int sequenceCachedStart) {
        this.sequenceCachedStart = sequenceCachedStart;
    }

    public int getSequenceCachedRows() {
        return sequenceCachedRows;
    }

    public void setSequenceCachedRows(int sequenceCachedRows) {
        this.sequenceCachedRows = sequenceCachedRows;
    }

    public int getSequenceCacheBlockSize() {
        return sequenceCacheBlockSize;
    }

    public void setSequenceCacheBlockSize(int sequenceCacheBlockSize) {
        this.sequenceCacheBlockSize = sequenceCacheBlockSize;
    }
    
    
}
