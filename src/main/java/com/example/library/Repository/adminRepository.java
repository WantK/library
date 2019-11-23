package com.example.library.Repository;

import com.example.library.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface adminRepository extends JpaRepository<Admin, Integer> {
        public Admin findByAdminName(String adminName);
}
