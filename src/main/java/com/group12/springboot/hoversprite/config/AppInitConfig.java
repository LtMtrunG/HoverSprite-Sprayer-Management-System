package com.group12.springboot.hoversprite.config;

import com.group12.springboot.hoversprite.entity.Permission;
import com.group12.springboot.hoversprite.entity.Role;
import com.group12.springboot.hoversprite.entity.TimeSlot;
import com.group12.springboot.hoversprite.repository.PermissionRepository;
import com.group12.springboot.hoversprite.repository.RoleRepository;
import com.group12.springboot.hoversprite.repository.TimeSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class AppInitConfig {
    @Bean
    @Transactional
    public CommandLineRunner initDatabase(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        return args -> {
            try {
                if (permissionRepository.count() == 0) {
                    List<String> defaultPermissions = Arrays.asList("APPROVE_BOOKING", "APPROVE_CHANGE_STATUS", "APPROVE_NOTIFICATION");

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

                if (approveBooking != null && approveChangeStatus != null && approveNotification != null) {
                    Role farmer = roleRepository.findById("FARMER").orElse(null);
                    Role receptionist = roleRepository.findById("RECEPTIONIST").orElse(null);
                    Role sprayer = roleRepository.findById("SPRAYER").orElse(null);

                    if (farmer != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveBooking);
                        permissions.add(approveNotification);
                        farmer.setPermissions(permissions);
                        roleRepository.save(farmer);
                    }

                    if (receptionist != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveBooking);
                        permissions.add(approveChangeStatus);
                        permissions.add(approveNotification);
                        receptionist.setPermissions(permissions);
                        roleRepository.save(receptionist);
                    }

                    if (sprayer != null) {
                        Set<Permission> permissions = new HashSet<>();
                        permissions.add(approveChangeStatus);
                        permissions.add(approveNotification);
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
