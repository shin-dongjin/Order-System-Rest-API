/*****************************************************************c******************o*******v******id********
 * File: PojoBase.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Abstract class that is base of (class) hierarchy for all c.a.cst8277.models @Entity classes
 */
@MappedSuperclass
@Access(AccessType.PROPERTY) // NOTE: by using this annotations, any annotation on a field is ignored without warning
@EntityListeners({PojoListener.class})
public abstract class PojoBase implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * int id for common attributes
     * */
    protected int id;
    /**
     * LocalDateTime created for common attributes
     * */
    protected LocalDateTime created;
    /**
     * LocalDateTime updated for common attributes
     * */
    protected LocalDateTime updated;
    /**
     * int version for common attributes
     * */
    protected int version;
    /**
     * Getter for id
     * @return int
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    /**
     * Setter for id
     * @param int
     * */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Getter for created
     * @return LocalDateTime
     * */
    @Column(name = "CREATED")
    public LocalDateTime getCreatedDate() {
        return created;
    }
    /**
     * Setter for created
     * @param LocalDateTime
     * */
    public void setCreatedDate(LocalDateTime created) {
        this.created = created;
    }
    /**
     * Getter for updated
     * @return LocalDateTime
     * */
    @Column(name = "UPDATED")
    public LocalDateTime getUpdatedDate() {
        return updated;
    }
    /**
     * Setter for updated
     * @param LocalDateTime
     * */
    public void setUpdatedDate(LocalDateTime updated) {
        this.updated = updated;
    }
    /**
     * Getter for version
     * @return int
     * */
    @Version
    public int getVersion() {
        return version;
    }
    /**
     * Setter for version
     * @param int
     * */
    public void setVersion(int version) {
        this.version = version;
    }

    // It is a good idea for hashCode() to use the @Id property
    // since it maps to table's PK and that is how Db determine's identity
    // (same for equals()
    /**
     * Create hash-code
     * @return int
     * */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
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
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PojoBase)) {
            return false;
        }
        PojoBase other = (PojoBase)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}