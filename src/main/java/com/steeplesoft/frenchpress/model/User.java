package com.steeplesoft.frenchpress.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author jdlee
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private int version;
    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    @Pattern(regexp = ".+@.+\\.[a-z]+")
    private String emailAddress;
//    @NotNull
    private String password;
    private String userRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!"".equals(password)) {
            this.password = password;
        }
    }

    public String getRole() {
        return userRole;
    }

    public void setRole(String role) {
        this.userRole = role;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.lastName);
        hash = 17 * hash + Objects.hashCode(this.firstName);
        hash = 17 * hash + Objects.hashCode(this.emailAddress);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.emailAddress, other.emailAddress)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.userRole, other.userRole)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "User{" + "id=" + id + ", lastName=" + lastName + ", firstName=" + firstName + ", emailAddress=" + emailAddress + "}";
    }
}
