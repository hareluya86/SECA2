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
    private boolean selected;
    private FileMenuItemWrapper selectedItem;
    private String selectedValue;
    private List<FileMenuItemWrapper> items;
    
    public void select(){
        this.selected = true;
        this.createDropdownMenu();
        for(FileMenuItemWrapper item:items){
            if(selectedValue.equals(item.getItemValue())){
                item.setItemLabel(item.getItemLabel().concat("ing "+wrappedFile.getFILENAME()));
            }
        }
    }
    
    public void unselect(){
        this.selected = false;
        this.createDropdownMenu();
    }
    
    public void createDropdownMenu(){
        List<FileMenuItemWrapper> dropdownMenu = new ArrayList<FileMenuItemWrapper>();
        
        //create a FileMenuItemWrapper object
        //1. Create menu items
        FileMenuItemWrapper menuItemTitle = new FileMenuItemWrapper();
        menuItemTitle.setItemLabel(wrappedFile.getFILENAME());
        menuItemTitle.setItemValue("0");
        dropdownMenu.add(menuItemTitle);
        
        FileMenuItemWrapper menuItemEdit = new FileMenuItemWrapper();
        menuItemEdit.setItemLabel("Edit");
        menuItemEdit.setItemValue("1");
        dropdownMenu.add(menuItemEdit);
        
        items = dropdownMenu;
    }

    public FileEntity getWrappedFile() {
        return wrappedFile;
    }

    public void setWrappedFile(FileEntity wrappedFile) {
        this.wrappedFile = wrappedFile;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public FileMenuItemWrapper getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(FileMenuItemWrapper selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<FileMenuItemWrapper> getItems() {
        return items;
    }

    public void setItems(List<FileMenuItemWrapper> items) {
        this.items = items;
    }
    
    
}
