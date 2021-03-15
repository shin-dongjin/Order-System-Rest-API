/*****************************************************************c******************o*******v******id********
 * File: OrderLinePojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
*
* Description: model for the OrderLine object
*/
@Entity(name = "OrderLine")
@Table(name = "ORDERLINE")
@Access(AccessType.PROPERTY) // NOTE: by using this annotations, any annotation on a field is ignored without warning
public class OrderLinePojo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * OrderLinePk primaryKey for orderline table
     * */
    protected OrderLinePk primaryKey;
    /**
     * OrderPojo owningOrder for orderline table
     * */
    protected OrderPojo owningOrder;
    /**
     * Double amount for orderline table
     * */
    protected Double amount;
    /**
     * ProductPojo product for orderline table
     * */
    protected ProductPojo product;

    // JPA requires each @Entity class have a default constructor
    /**
     * Default Constructor
     * */
    public OrderLinePojo() {
    }
    /**
     * Getter for primaryKey
     * @return OrderLinePk
     * */
    @EmbeddedId
    public OrderLinePk getPk() {
        return primaryKey;
    }
    /**
     * Setter for primaryKey
     * @param OrderLinePk
     * */
    public void setPk(OrderLinePk primaryKey) {
        this.primaryKey = primaryKey;
    }
    /**
     * Getter for owningOrder
     * @return OrderPojo
     * */
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "OWNING_ORDER_ID")
    @MapsId("owningOrderId")
    public OrderPojo getOwningOrder() {
        return owningOrder;
    }
    /**
     * Setter for OrderPojo
     * @param OrderPojo
     * */
    public void setOwningOrder(OrderPojo owningOrder) {
        this.owningOrder = owningOrder;
    }
    /**
     * Getter for amount
     * @return Double
     * */
    @Column(name="AMOUNT")
    public Double getAmount() {
        return amount;
    }
    /**
     * Setter for amount
     * @param Double
     * */
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    /**
     * Getter for product
     * @return ProductPojo
     * */
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PRODUCT_ID")
    public ProductPojo getProduct() {
        return product;
    }
    /**
     * Setter for product
     * @param ProductPojo
     * */
    public void setProduct(ProductPojo product) {
        this.product = product;
    }

}