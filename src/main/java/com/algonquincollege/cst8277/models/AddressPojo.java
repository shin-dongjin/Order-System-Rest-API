/*****************************************************************c******************o*******v******id********
 * File: AddressPojo.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
*
* Description: model for the Address object
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
  @JsonSubTypes({
    @Type(value = BillingAddressPojo.class, name = "B"),
    @Type(value = ShippingAddressPojo.class, name = "S")
})
@Entity(name="Address")
@Table(name = "CUST_ADDR")
@AttributeOverride(name="id", column=@Column(name="ADDR_ID"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ADDR_TYPE", columnDefinition = "VARCHAR", length= 1)
public abstract class AddressPojo extends PojoBase implements Serializable {

    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * String street for ADDRESS table
     * */
    protected String street;
    /**
     * String city for ADDRESS table
     * */
    protected String city;
    /**
     * String country for ADDRESS table
     * */
    protected String country;
    /**
     * String postal for ADDRESS table
     * */
    protected String postal;
    /**
     * String state for ADDRESS table
     * */
    protected String state;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public AddressPojo() {
        super();
    }
    /**
     * Getter for city
     * @return city
     * */
    @Column(name="CITY")
    public String getCity() {
        return city;
    }
    /**
     * Setter for city
     * @param String
     * */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Getter for country
     * @return country
     * */
    @Column(name="COUNTRY")
    public String getCountry() {
        return country;
    }
    /**
     * Setter for street
     * @param String
     * */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * Getter for postal
     * @return postal
     * */
    @Column(name="POSTAL")
    public String getPostal() {
        return postal;
    }
    /**
     * Setter for postal
     * @param postal
     * */
    public void setPostal(String postal) {
        this.postal = postal;
    }
    /**
     * Getter for state
     * @return state
     * */
    @Column(name="STATE")
    public String getState() {
        return state;
    }
    /**
     * Setter for state
     * @param state
     * */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * Getter for street
     * @return street
     * */
    @Column(name="STREET")
    public String getStreet() {
        return street;
    }
    /**
     * Setter for street
     * @param String
     * */
    public void setStreet(String street) {
        this.street = street;
    }

}