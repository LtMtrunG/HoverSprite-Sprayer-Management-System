package com.group12.springboot.hoversprite.common;

import java.util.Set;

import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="TBL_ROLES")
public class Role implements GrantedAuthority {
    @Id
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "TBL_ROLES_PERMISSIONS",
            joinColumns = @JoinColumn(name = "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name")
    )

    Set<Permission> permissions;

    @Override
    public String getAuthority() {
        return name; // Return the role name as the authority with ROLE_ prefix
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return name; // Return the role name when the object is printed
    }
}