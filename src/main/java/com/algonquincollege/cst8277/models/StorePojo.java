/*****************************************************************c******************o*******v******id********
 * File: StorePojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey  040745918
 */
package com.algonquincollege.cst8277.models;

import static com.algonquincollege.cst8277.models.StorePojo.ALL_STORES_QUERY_NAME;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.algonquincollege.cst8277.rest.ProductSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
*
* Description: model for the Store object
*/
@Entity(name = "Store")
@Table(name = "STORES")
@AttributeOverride(name="id", column=@Column(name="STORE_ID"))
@NamedQuery(name = ALL_STORES_QUERY_NAME, query = "select s from Store s")
public class StorePojo extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * String for all stores
     * */
    public static final String ALL_STORES_QUERY_NAME = "allStores";
    /**
     * String storeName for stores table
     * */
    protected String storeName;
    protected Set<ProductPojo> products = new HashSet<>();

    // JPA requires each @Entity class have a default constructor
    /**
     * Default Constructor
     * */
    public StorePojo() {
    }
    /**
     * Getter for storeName
     * @return String storeName
     * */
    public String getStoreName() {
        return storeName;
    }
    /**
     * Setter for storeName
     * @param String storeName
     * */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    /**
     * Getter for products
     * @return Set<ProductPojo> products
     * */
    @JsonSerialize(using = ProductSerializer.class)
      //Discovered what I think is a bug: you should be able to list them in any order,
      //but it turns out, EclipseLink's JPA implementation needs the @JoinColumn StorePojo's PK
      //first, the 'inverse' to ProductPojo's PK second
    @ManyToMany
    @JoinTable(
        name = "STORES_PRODUCTS",
        joinColumns=@JoinColumn(name = "STORE_ID", referencedColumnName="STORE_ID"),
        inverseJoinColumns=@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
        )
    public Set<ProductPojo> getProducts() {
        return products;
    }
    /**
     * Setter for products
     * @param Set<ProductPojo> products
     * */
    public void setProducts(Set<ProductPojo> products) {
        this.products = products;
    }

}
