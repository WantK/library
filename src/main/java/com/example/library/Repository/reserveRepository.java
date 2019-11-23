package com.example.library.Repository;


import com.example.library.Entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface reserveRepository extends JpaRepository<Reserve,Integer> {
    public List<Reserve> findByIsbn(String isbn);
    public Reserve findByBookId(Integer bookId);
    public List<Reserve> findByReaderName(String readerName);
}
