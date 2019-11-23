package com.example.library.Repository;

import com.example.library.Entity.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface librarianRepository extends JpaRepository<Librarian, Integer> {
    public List<Librarian> findByLibrarianName(String librarianName);

}