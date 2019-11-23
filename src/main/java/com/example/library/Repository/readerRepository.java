package com.example.library.Repository;

import com.example.library.Entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface readerRepository extends JpaRepository<Reader, Integer> {
    public Reader findByReaderName(String readerName);
}
