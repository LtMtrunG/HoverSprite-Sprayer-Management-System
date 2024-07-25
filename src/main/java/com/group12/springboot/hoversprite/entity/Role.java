package com.group12.springboot.hoversprite.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="TBL_ROLES")
public class Role {
    @Id
    private String name;

    @ManyToMany
    @JoinTable(
            name = "TBL_ROLES_PERMISSIONS",
            joinColumns = @JoinColumn(name = "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name")
    )
    Set<Permission> permissions;

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
}
