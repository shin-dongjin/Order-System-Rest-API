/*****************************************************************c******************o*******v******id********
 * File: CustomerPojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import java.util.List;

import static com.algonquincollege.cst8277.models.CustomerPojo.ALL_CUSTOMERS_QUERY_NAME;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
*
* Description: model for the Customer object
*/
@Entity(name = "Customer")
@Table(name = "CUSTOMER")
@AttributeOverride(name="id", column=@Column(name="CUST_ID"))
@NamedQuery(name = ALL_CUSTOMERS_QUERY_NAME, query = "select c from Customer c")
public class CustomerPojo extends PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ALL_CUSTOMERS_QUERY_NAME = "allCustomers";
    /**
     * String firstName for CUSTOMER table
     * */
    protected String firstName;
    /**
     * String lastName for CUSTOMER table
     * */
    protected String lastName;
    /**
     * String email for CUSTOMER table
     * */
    protected String email;
    /**
     * String phoneNumber for CUSTOMER table
     * */
    protected String phoneNumber;
    /**
     * AddressPojo shippingAddress for CUSTOMER table
     * */
    protected AddressPojo shippingAddress;
    /**
     * AddressPojo billingAddress for CUSTOMER table
     * */
    protected AddressPojo billingAddress;
    /**
     * List<OrderPojo> orders for CUSTOMER table
     * */
    protected List<OrderPojo> orders;
	
    // JPA requires each @Entity class have a default constructor
	public CustomerPojo() {
	}
	
    /**
     * @return the value for firstName
     */
    @Column(name = "FNAME")
    public String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName new value for firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the value for lastName
     */
    @Column(name = "LNAME")
    public String getLastName() {
        return lastName;
    }
    /**
     * @param lastName new value for lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Getter for email
     * @return email
     * */
    @Column(name="EMAIL")
    public String getEmail() {
        return email;
    }
    /**
     * Setter for email
     * @return email
     * */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Getter for phoneNumber
     * @return phoneNumber
     * */
    @Column(name="PHONENUMBER")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * Setter for phoneNumber
     * @return phoneNumber
     * */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Getter for shippingAddress
     * @return shippingAddress
     * */
    //dont use CascadeType.All (skipping CascadeType.REMOVE): what if two customers
    //live at the same address and 1 leaves the house but the other does not?
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="SHIPPING_ADDR")
    public AddressPojo getShippingAddress() {
        return shippingAddress;
    }
    /**
     * Setter for shippingAddress
     * @return shippingAddress
     * */
    public void setShippingAddress(AddressPojo shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    /**
     * Getter for billingAddress
     * @return billingAddress
     * */
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="BILLING_ADDR")
    public AddressPojo getBillingAddress() {
        return billingAddress;
    }
    /**
     * Setter for billingAddress
     * @return billingAddress
     * */
    public void setBillingAddress(AddressPojo billingAddress) {
        this.billingAddress = billingAddress;
    }
    /**
     * Getter for order
     * @return order
     * */
    @JsonManagedReference
    @OneToMany(mappedBy = "OwningCustomer")
    public List<OrderPojo> getOrders() {
        return orders;
    }
    /**
     * Setter for orders
     * @return orders
     * */
    public void setOrders(List<OrderPojo> orders) {
        this.orders = orders;
    }
    /**
     * Create string for the object
     * @return String
     * */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("Customer [id=")
            .append(id)
            .append(", ");
        if (firstName != null) {
            builder
                .append("firstName=")
                .append(firstName)
                .append(", ");
        }
        if (lastName != null) {
            builder
                .append("lastName=")
                .append(lastName)
                .append(", ");
        }
        if (phoneNumber != null) {
            builder
                .append("phoneNumber=")
                .append(phoneNumber)
                .append(", ");
        }
        if (email != null) {
            builder
                .append("email=")
                .append(email)
                .append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

}