package com.example.library.Service;

import com.example.library.Entity.Book;
import com.example.library.Repository.bookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class bookService {
    @Autowired
    bookRepository BookRepository;

}
