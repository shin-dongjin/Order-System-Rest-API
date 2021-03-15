/*****************************************************************c******************o*******v******id********
 * File: OrderPojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
*
* Description: model for the Product object
*/
@Entity(name = "Product")
@Table(name = "PRODUCT")
@AttributeOverride(name="id", column=@Column(name="PRODUCT_ID"))
public class ProductPojo extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * String description for product table
     * */
    protected String description;
    /**
     * String serialNo for product table
     * */
    protected String serialNo;
    /**
     * Set<StorePojo> stores for product table
     * */
    protected Set<StorePojo> stores = new HashSet<>();
    /**
     * Default Constructor
     * */
    // JPA requires each @Entity class have a default constructor
    public ProductPojo() {
    }
    
    /**
     * @return the value for firstName
     */
    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }
    /**
     * @param description new value for description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the value for serialNo
     */
    @Column(name = "SERIALNUMBER")
    public String getSerialNo() {
        return serialNo;
    }
    /**
     * @param description new value for serialNo
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    /**
     * @return the value for stores
     */
    @JsonInclude(Include.NON_NULL)
    @ManyToMany(mappedBy = "products")
    public Set<StorePojo> getStores() {
        return stores;
    }
    /**
     * @param description new value for stores
     */
    public void setStores(Set<StorePojo> stores) {
        this.stores = stores;
    }

}