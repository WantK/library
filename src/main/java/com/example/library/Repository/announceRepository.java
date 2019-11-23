package com.example.library.Repository;

import com.example.library.Entity.Announce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface announceRepository extends JpaRepository<Announce, Integer> {
    List<Announce> findByAnnounceStatus(Integer announceStatus);
}
