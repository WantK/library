package com.example.library.Repository;

import com.example.library.Entity.Remind;
import org.springframework.data.jpa.repository.JpaRepository;

public interface remindRepository extends JpaRepository<Remind, Integer> {
}
