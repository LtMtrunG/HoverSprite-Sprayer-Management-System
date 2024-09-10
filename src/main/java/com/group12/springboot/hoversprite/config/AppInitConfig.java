package com.group12.springboot.hoversprite.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.common.Permission;
import com.group12.springboot.hoversprite.common.PermissionRepository;
import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.common.RoleRepository;

@Configuration
public class AppInitConfig {
    @Bean
    @Transactional
    public CommandLineRunner initDatabase(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        return args -> {
            try {
                if (permissionRepository.count() == 0) {
                    List<String> defaultPermissions = Arrays.asList("APPROVE_BOOKING", "APPROVE_CHANGE_STATUS", "APPROVE_NOTIFICATION", "APPROVE_CREATE_FIELD", "APPROVE_VIEW_FIELD");

                    for (String permissionName : defaultPermissions) {
                        Permission permission = new Permission();
                        permission.setName(permissionName);
                        permissionRepository.save(permission);
                    }
                }

                if (roleRepository.count() == 0) {
                    List<String> defaultRoles = Arrays.asList("FARMER", "RECEPTIONIST", "SPRAYER");

                    for (String roleName : defaultRoles) {
                        Role role = new Role();
                        role.setName(roleName);
                        roleRepository.save(role);
                    }
                }

                Permission approveBooking = permissionRepository.findById("APPROVE_BOOKING").orElse(null);
                Permission approveChangeStatus = permissionRepository.findById("APPROVE_CHANGE_STATUS").orElse(null);
                Permission approveNotification = permissionRepository.findById("APPROVE_NOTIFICATION").orElse(null);
                Permission approveCreateField = permissionRepository.findById("APPROVE_CREATE_FIELD").orElse(null);
                Permission approveViewField = permissionRepository.findById("APPROVE_VIEW_FIELD").orElse(null);

                if (approveBooking != null && approveChangeStatus != null && approveNotification != null) {
                    Role farmer = roleRepository.findById("FARMER").orElse(null);
                    Role receptionist = roleRepository.findById("RECEPTIONIST").orElse(null);
                    Role sprayer = roleRepository.findById("SPRAYER").orElse(null);

                    if (farmer != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveBooking);
                        permissions.add(approveNotification);
                        permissions.add(approveCreateField);
                        permissions.add(approveViewField);
                        farmer.setPermissions(permissions);
                        roleRepository.save(farmer);
                    }

                    if (receptionist != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveBooking);
                        permissions.add(approveChangeStatus);
                        permissions.add(approveNotification);
                        permissions.add(approveCreateField);
                        permissions.add(approveViewField);
                        receptionist.setPermissions(permissions);
                        roleRepository.save(receptionist);
                    }

                    if (sprayer != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveChangeStatus);
                        permissions.add(approveNotification);
                        permissions.add(approveViewField);
                        sprayer.setPermissions(permissions);
                        roleRepository.save(sprayer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}