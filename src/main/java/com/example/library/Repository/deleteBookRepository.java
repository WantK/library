package com.example.library.Repository;

import com.example.library.Entity.DeleteBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface deleteBookRepository extends JpaRepository<DeleteBook,Integer> {
    public List<DeleteBook> findByIsbn(String isbn);
}
