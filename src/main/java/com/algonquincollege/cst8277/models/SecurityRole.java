/*****************************************************************c******************o*******v******id********
 * File: SecurityRole.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import static com.algonquincollege.cst8277.models.SecurityRole.ROLE_BY_NAME_QUERY;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Role class used for (JSR-375) Java EE Security authorization/authentication
 */
@Entity(name = "SecurityRole")
@Table(name = "SECURITY_ROLE")
@NamedQuery(name = ROLE_BY_NAME_QUERY, query = "select sr from SecurityRole sr where sr.roleName = :param1")
public class SecurityRole implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * static final String roleByName
     * */
    public static final String ROLE_BY_NAME_QUERY = "roleByName";
    /**
     * int id
     * */
    protected int id;
    /**
     * String roleName
     * */
    protected String roleName;
    /**
     * Set<SecurityUser> users
     * */
    protected Set<SecurityUser> users;
    
    /**
     * Default Constructor
     * */
    public SecurityRole() {
        super();
    }
    /**
     * @return int id
     * */
    @Id
    @Column(name = "ROLE_ID")
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
     * @return string rolename
     * */
    @Column(name = "ROLENAME")
    public String getRoleName() {
        return roleName;
    }
    /**
     * @param string rolename
     * */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    /**
     * @return set<SecurityUser>
     * */
    @JsonInclude(Include.NON_NULL)
    //TODO
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.PERSIST)
    public Set<SecurityUser> getUsers() {
        return users;
    }
    /**
     * @param Set<SecurityUser> users
     * */
    public void setUsers(Set<SecurityUser> users) {
        this.users = users;
    }
    /**
     * @param SecurityUser user
     * */
    public void addUserToRole(SecurityUser user) {
        getUsers().add(user);
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
        SecurityRole other = (SecurityRole)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}