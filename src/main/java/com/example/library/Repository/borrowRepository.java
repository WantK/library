package com.example.library.Repository;

import com.example.library.Entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface borrowRepository extends JpaRepository<Borrow,Integer> {
    public Borrow findByBookId(Integer bookId);
    public List<Borrow> findByReaderId(Integer readerId);
    public List<Borrow> findByIsbn(String isbn);
    public List<Borrow> findByStatus(Integer status);
    public List<Borrow> findByReaderName(String readerName);
}
