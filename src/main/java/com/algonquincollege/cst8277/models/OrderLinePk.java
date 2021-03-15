/*****************************************************************c******************o*******v******id********
 * File: OrderLinePk.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OrderLinePk implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * int owningOrderId for OrderLinePK table
     * */
    protected int owningOrderId;
    /**
     * String street for OrderLinePK table
     * */
    protected int orderLineNo;
    /**
     * Getter for owningOrderId
     * @return owningOrderId
     * */
    @Column(name = "OWNING_ORDER_ID")
    public int getOwningOrderId() {
        return owningOrderId;
    }
    /**
     * Setter for owningOrderId
     * @param owningOrderId
     * */
    public void setOwningOrderId(int owningOrderId) {
        this.owningOrderId = owningOrderId;
    }
    /**
     * Getter for orderLineNo
     * @return orderLineNo
     * */
    @Column(name = "ORDERLINE_NO")
    public int getOrderLineNo() {
        return orderLineNo;
    }
    /**
     * Setter for orderLineNo
     * @param orderLineNo
     * */
    public void setOrderLineNo(int orderLineNo) {
        this.orderLineNo = orderLineNo;
    }
    /**
     * Create Hashcode
     * @return int
     * */
    @Override
    public int hashCode() {
        return Objects.hash(orderLineNo, owningOrderId);
    }
    /**
     * Compare objects
     * @return boolean
     * @param Object
     * */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrderLinePk)) {
            return false;
        }
        OrderLinePk other = (OrderLinePk) obj;
        return orderLineNo == other.orderLineNo && owningOrderId == other.owningOrderId;
    }

}