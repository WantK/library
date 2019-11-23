package com.example.library.Repository;

import com.example.library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface bookRepository extends JpaRepository<Book, Integer> {
//  public List<Book> findById(Integer id);
    public List<Book> findByIsbn(String isbn);
    public List<Book> findByImage(String image);

}
