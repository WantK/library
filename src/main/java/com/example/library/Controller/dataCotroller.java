package com.example.library.Controller;

import com.example.library.Entity.Borrow;
import com.example.library.Entity.Librarian;
import com.example.library.Entity.Reader;
import com.example.library.Repository.borrowRepository;
import com.example.library.Repository.librarianRepository;
import com.example.library.Repository.readerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class dataCotroller {
    @Autowired
    private librarianRepository LibrarianRepository;


    @Autowired
    private borrowRepository BorrowRepository;

    @Autowired
    private readerRepository ReaderRepository;


    @RequestMapping(value = "/loginFailed",method = RequestMethod.GET)
    public String loginFailed(){
        return "loginFailed";
    }


//    @RequestMapping(value = "/showReader/{bookId}",method = RequestMethod.POST)
//    public Integer showReader(@PathVariable("bookId") Integer bookId){
//        Borrow borrow = BorrowRepository.findByBookId(bookId);
//        return borrow.getReaderId();
//    }
    @RequestMapping(value = "/showReader/{bookId}",method = RequestMethod.POST)
    public String showReader(@PathVariable("bookId") Integer bookId){
        Borrow borrow = BorrowRepository.findByBookId(bookId);
        return borrow.getReaderName();
    }


}
