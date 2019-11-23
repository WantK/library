package com.example.library.Controller;

import com.example.library.Entity.Book;
import com.example.library.Entity.Borrow;
import com.example.library.Entity.Reserve;
import com.example.library.Repository.bookRepository;
import com.example.library.Repository.borrowRepository;
import com.example.library.Repository.readerRepository;
import com.example.library.Repository.reserveRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class reserveController {
    @Autowired
    private reserveRepository ReserveRepository;
    @Autowired
    private bookRepository BookRepository;

    @Autowired
    private borrowRepository BorrowRepository;

    @Autowired
    private readerRepository ReaderRepository;

    @RequestMapping(value = "/reserve",method = RequestMethod.POST)
    @ResponseBody
    private String reserve(@RequestParam(value = "bookId") Integer bookId,
                           HttpServletRequest request){
        String readerName = CookieUtils.getCookieValue(request,"readerName");
//        Reserve reserve = ReserveRepository.findByBookId(bookId);
//        if(reserve.getReaderName().equals(readerName) && reserve.getStatus()==1){
//            return "Al";
//        }
        Reserve reserve = new Reserve();
        reserve.setBookId(bookId);
        reserve.setStatus(1);
        reserve.setReaderName(readerName);
        Optional<Book> book = BookRepository.findById(bookId);
        reserve.setIsbn(book.get().getIsbn());
        reserve.setTitle(book.get().getTitle());
        reserve.setImage(book.get().getImage());
        reserve.setReaderId(ReaderRepository.findByReaderName(readerName).getReaderId());

        Date date = new Date();
        reserve.setDate(date);

        ReserveRepository.save(reserve);
        return "Reserve successfully";
    }

//    @RequestMapping(value = "/reserveToBorrow",method = RequestMethod.POST)
//    @ResponseBody
//    public String reserveToBorrow(@RequestParam(value = "bookId") Integer bookId){
//        Reserve reserve = ReserveRepository.findByBookId(bookId);
//        if(reserve.getStatus() == 1){
//            Borrow borrow = new Borrow();
//            borrow.setStatus(1);
//            borrow.setBookId(reserve.getBookId());
//            borrow.setFine(0.0);
//            borrow.setIsbn(reserve.getIsbn());
//            borrow.setReaderId(reserve.getReaderId());
//            borrow.setReaderName(reserve.getReaderName());
//            borrow.setTitle(reserve.getTitle());
//            borrow.setImage(reserve.getImage());
//            BorrowRepository.save(borrow);
//            reserve.setStatus(0);
//            ReserveRepository.save(reserve);
//            return "读者"+reserve.getReaderName()+"取书成功";
//        }else{
//            return "已超出取书时间";
//        }
//    }
}
