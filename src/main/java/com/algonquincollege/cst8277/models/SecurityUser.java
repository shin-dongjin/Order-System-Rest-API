/*****************************************************************c******************o*******v******id********
 * File: SecurityUser.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import static com.algonquincollege.cst8277.models.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import static com.algonquincollege.cst8277.models.SecurityUser.USER_FOR_OWNING_CUST_QUERY;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.algonquincollege.cst8277.rest.SecurityRoleSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * User class used for (JSR-375) Java EE Security authorization/authentication
 */
@Entity(name = "SecurityUser")
@Table(name = "SECURITY_USER")
@NamedQuery(name = SECURITY_USER_BY_NAME_QUERY, query = "select s from SecurityUser s where s.username = :username")
//@NamedQuery(name = USER_FOR_OWNING_CUST_QUERY, query = "select s from SecurityUser s where s.cust = :cust")
public class SecurityUser implements Serializable, Principal {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * static final String USER_FOR_OWNING_CUST_QUERY
     * */
    public static final String USER_FOR_OWNING_CUST_QUERY =
        "userForOwningCust";
    /**
     * static final String SECURITY_USER_BY_NAME_QUERY
     * */
    public static final String SECURITY_USER_BY_NAME_QUERY =
        "userByName";
    /**
     * int id
     * */
    protected int id;
    /**
     * String username
     * */
    protected String username;
    /**
     * String pwHash
     * */
    protected String pwHash;
    /**
     * Set<SecurityRole> roles
     * */
    protected Set<SecurityRole> roles = new HashSet<>();
    /**
     * CustomerPojo cust
     * */
    protected CustomerPojo cust;
    /**
     * Default Constructor
     * */
    public SecurityUser() {
        super();
    }
    /**
     * @return int id
     * */
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    /**
     * @param int id
     * */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return string username
     * */
    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }
    /**
     * @param string username
     * */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return String
     * */
    @JsonIgnore
    public String getPwHash() {
        return pwHash;
    }
    /**
     * @param String pwHash
     * */
    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }
    /**
     * @return Set<SecurityRole>
     * */
    @JsonInclude(Include.NON_NULL)
    @JsonSerialize(using = SecurityRoleSerializer.class)
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "SECURITY_USER_SECURITY_ROLE",
        joinColumns=@JoinColumn(name = "USER_ID", referencedColumnName="USER_ID"),
        inverseJoinColumns=@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")
        )
    public Set<SecurityRole> getRoles() {
        return roles;
    }
    /**
     * @param Set<SecurityRole> roles
     * */
    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    @OneToOne
    @JoinColumn(name="CUST_ID")
    public CustomerPojo getCustomer() {
        return cust;
    }
    public void setCustomer(CustomerPojo cust) {
        this.cust = cust;
    }
    /**
     * @return string getUsername
     * */
    //Principal
    @JsonIgnore
    @Override
    public String getName() {
        return getUsername();
    }
    /**
     * @return int
     * create hashcode
     * */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    /**
     * @return boolean
     * @param Object
     * */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SecurityUser other = (SecurityUser)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}