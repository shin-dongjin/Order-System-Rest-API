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
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
*
* Description: model for the Order object
*/
@Entity(name = "Order")
@Table(name = "ORDER_TBL")
@AttributeOverride(name="id", column=@Column(name="ORDER_ID"))
public class OrderPojo extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * String description for ORDER_TBL table
     * */
    protected String description;
    /**
     * List<OrderLinePojo> orderlines for ORDER_TBL table
     * */
    protected List<OrderLinePojo> orderlines;
    /**
     * CustomerPojo owningCustomer for ORDER_TBL table
     * */
    protected CustomerPojo owningCustomer;
    
    // JPA requires each @Entity class have a default constructor
    /**
     * Default Constructor
     * */
	public OrderPojo() {
	}
    /**
     * Getter for description
     * @return String
     * */
    public String getDescription() {
        return description;
    }
    /**
     * Setter for description
     * @param String
     * */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Getter for orderlines
     * @return List<OrderLinePojo>
     * */
    @JsonManagedReference
    @OneToMany(mappedBy = "owningOrder")
	public List<OrderLinePojo> getOrderlines() {
		return this.orderlines;
	}
    /**
     * Setter for orderlines
     * @param List<OrderLinePojo>
     * */
	public void setOrderlines(List<OrderLinePojo> orderlines) {
		this.orderlines = orderlines;
	}
	   /**
     * Add new orderline
     * @param OrderLinePojo
     * @return OrderLinePojo
     * */
	public OrderLinePojo addOrderline(OrderLinePojo orderline) {
		getOrderlines().add(orderline);
		orderline.setOwningOrder(this);
		return orderline;
	}
    /**
   * Remove new orderline
   * @param OrderLinePojo
   * @return OrderLinePojo
   * */
	public OrderLinePojo removeOrderline(OrderLinePojo orderline) {
		getOrderlines().remove(orderline);
        orderline.setOwningOrder(null);
		return orderline;
	}
    /**
     * Getter for owningCustomer
     * @return CustomerPojo
     * */
	@JsonBackReference
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "OWNING_CUST_ID")
	public CustomerPojo getOwningCustomer() {
		return this.owningCustomer;
	}
    /**
     * Setter for owner
     * @param CustomerPojo
     * */
	public void setOwningCustomer(CustomerPojo owner) {
		this.owningCustomer = owner;
	}

}