/*****************************************************************c******************o*******v******id********
 * File: PojoListener.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class PojoListener {
    /**
     * Setter for created on date
     * @param PojoBase
     * */
    @PrePersist
    public void setCreatedOnDate(PojoBase base) {
        LocalDateTime now = LocalDateTime.now();
        base.setCreatedDate(now);
        //might as well call setUpdatedDate as well
        base.setUpdatedDate(now);
    }
    /**
     * Setter for update on date
     * @param PojoBase
     * */
    @PreUpdate
    public void setUpdatedDate(PojoBase base) {
        base.setUpdatedDate(LocalDateTime.now());
    }
}