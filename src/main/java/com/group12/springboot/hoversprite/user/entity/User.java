package com.group12.springboot.hoversprite.user.entity;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.user.enums.Expertise;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="TBL_USERS")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="password")
    private String password;

    @Column(name="full_name")
    private String fullName;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="email")
    private String email;

    @Column(name="address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name="expertise")
    private Expertise expertise;

    @Column(name="fields_id")
    private List<Long> fieldsId;

    @ManyToOne
    private Role role;
}