/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author vincent.a.lee
 * @param <T>
 */
public interface DripDatasource<T extends Object> extends Serializable {
    
    public void pour(List<T> stuff);
    public List<T> drip();
    public List<T> drip(int size);
}
