package com.example.library.Repository;

import com.example.library.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface locationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByLocationFloor(String locationFloor);
}
