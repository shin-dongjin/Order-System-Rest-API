/*****************************************************************c******************o*******v******id********
 * File: BillingAddressPojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
/**
*
* Description: model for the BillingAddress object
*/
@Entity
@DiscriminatorValue("B")
public class BillingAddressPojo extends AddressPojo implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * boolean isAlsoShipping for BillingAddress table
     * */
    protected boolean isAlsoShipping;
    /**
     * Default Constructor
     * */
    // JPA requires each @Entity class have a default constructor
    public BillingAddressPojo() {
    }
    /**
     * Getter for isAlsoShipping
     * @return isAlsoShipping
     * */
    public boolean isAlsoShipping() {
        return isAlsoShipping;
    }
    /**
     * Setter for isAlsoShipping
     * @param boolean isAlsoShipping
     * */
    public void setAlsoShipping(boolean isAlsoShipping) {
        this.isAlsoShipping = isAlsoShipping;
    }
    
}