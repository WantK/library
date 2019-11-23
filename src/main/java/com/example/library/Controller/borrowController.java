package com.example.library.Controller;

import com.example.library.Entity.Book;
import com.example.library.Entity.Borrow;
import com.example.library.Entity.Reader;
import com.example.library.Entity.Reserve;
import com.example.library.Repository.*;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class borrowController {
    @Autowired
    private bookRepository BookRepository;
    @Autowired
    private borrowRepository BorrowRepository;

    @Autowired
    private readerRepository ReaderRepository;
    @Autowired
    private reserveRepository ReserveRepository;
    @Autowired
    private adminRepository AdminRepository;

    @RequestMapping(value = "/borrowIndex",method = RequestMethod.GET)
    public String borrowIndex(HttpServletRequest request,
                              Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/borrowFirst";
    }

    @RequestMapping(value = "/borrowResult",method = RequestMethod.POST)
    @ResponseBody
    public String borrow(@RequestParam(value = "bookId") Integer bookId,
                         @RequestParam(value = "isbn")String isbn,
                         @RequestParam(value = "readerName") String readerName){
        Reader reader = ReaderRepository.findByReaderName(readerName);
        if(reader == null){
            return "Please check the readerName, and enter the right readerName";
        }

        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        Borrow borrowTemp;
        for(int i = borrowList.size() - 1; i >=0;i--){
            borrowTemp = borrowList.get(i);
            if(borrowTemp.getStatus() == 0){
                borrowList.remove(borrowTemp);
            }
        }
        if(borrowList.size() >= 3){
            return "The max limit is three books";
        }

       borrowTemp = BorrowRepository.findByBookId(bookId);
        if(borrowTemp != null){
            if(borrowTemp.getStatus() == 1){
                if(borrowTemp.getReaderName().equals(readerName)){
                    return "Already borrowed by you";
                }
                return "Already borrowed by other reader";
            }

        }

        List<Reserve> reserveList = ReserveRepository.findByIsbn(isbn);
        Reserve reserve;
        for(int i = reserveList.size() - 1;i >= 0; i--){
            reserve = reserveList.get(i);
            if(reserve.getStatus() == 1){
                long j = new Date().getTime() - reserve.getDate().getTime();
                long k = j/(1000*60);
                if(k > 120){
                    reserve.setStatus(0);
                    ReserveRepository.save(reserve);
                    reserveList.remove(reserve);
                }
            }else{//status = 0
                reserveList.remove(reserve);
            }
        }//reserveList中是有效的被预约的书

        reserve = ReserveRepository.findByBookId(bookId);
        if(reserve != null && reserve.getStatus()==1){
            if(reserveList.contains(reserve)){
                Borrow borrow = new Borrow();
                borrow.setStatus(1);
                borrow.setBookId(reserve.getBookId());
                borrow.setFine(0.0);
                borrow.setIsbn(reserve.getIsbn());
                borrow.setReaderId(reserve.getReaderId());
                borrow.setReaderName(reserve.getReaderName());
                borrow.setTitle(reserve.getTitle());
                borrow.setImage(reserve.getImage());
                BorrowRepository.save(borrow);
                reserve.setStatus(0);
                Date date = new Date();
                borrow.setDate(date);

                borrow.setFineStatus(0);
                ReserveRepository.save(reserve);
                return "Reader "+reserve.getReaderName()+" takes the reserving book successfully";
            }else{
                return "Already reserved by other reader";
            }
        }

        Borrow borrow = new Borrow();
        borrow.setBookId(bookId);
        borrow.setIsbn(isbn);
        borrow.setReaderId(ReaderRepository.findByReaderName(readerName).getReaderId());
        borrow.setReaderName(readerName);
        Integer status = 1;
        Double fine = 0.0;
        borrow.setStatus(status);
        borrow.setFine(fine);
        borrow.setTitle(BookRepository.findById(bookId).get().getTitle());
        borrow.setImage(BookRepository.findById(bookId).get().getImage());

        Date date = new Date();
        borrow.setDate(date);

        borrow.setFineStatus(0);

        BorrowRepository.save(borrow);
        return "Borrow successfully";
    }

//    @RequestMapping(value = "/showReader/{bookId}",method = RequestMethod.POST)
//    public Integer showReader(@PathVariable("bookId") Integer bookId){
//        Borrow borrow = BorrowRepository.findByBookId(bookId);
//        return borrow.getReaderId();
//    }

//    @RequestMapping(value = "/borrow/{bookId}",method = RequestMethod.GET)
//    public String borrow(@PathVariable("bookId") Integer bookId,
//                         Model model){
//        Optional<Book> bookOptional = BookRepository.findById(bookId);
//        Book book = bookOptional.get();
//        model.addAttribute("image",book.getImage());
//        model.addAttribute("id",book.getId());
//        model.addAttribute("title",book.getTitle());
//        model.addAttribute("author",book.getAuthor());
//        model.addAttribute("isbn",book.getIsbn());
//        List<Book> bookList = BookRepository.findByIsbn(book.getIsbn());
//        List<Borrow> borrowList = BorrowRepository.findByIsbn(book.getIsbn());
//        Integer restCount = bookList.size() - borrowList.size();
//        model.addAttribute("restCount",restCount);
//        return "book/borrow";
//    }

    @RequestMapping(value = "/borrow/{bookId}",method = RequestMethod.GET)
    public String borrow(@PathVariable("bookId") Integer bookId,
                HttpServletRequest request,
                Model model){
            Optional<Book> bookOptional = BookRepository.findById(bookId);
            Book book = bookOptional.get();
        model.addAttribute("book",book);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/borrowLibrarian";
    }

    @RequestMapping(value = "/recordIndex",method = RequestMethod.GET)
    public String RecordIndex(HttpServletRequest request,
                             Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/recordIndex";
    }

    @RequestMapping(value = "/showRecord/{readerName}",method = RequestMethod.GET)
    public String showRecord(HttpServletRequest request,
                            @PathVariable("readerName") String readerName,
                             Model model){
        List<Borrow> borrowListTemp = BorrowRepository.findByReaderName(readerName);
        List<Reserve> reserveListTemp = ReserveRepository.findByReaderName(readerName);

        int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
        double finePerDay = AdminRepository.findByAdminName("admin").getFineValue();
        double fine = 0.0;
        List<Borrow> borrowList = new ArrayList<>();
        List<Borrow> returnList = new ArrayList<>();

        Borrow borrow;
        Reserve reserve;
        for(int i = 0; i < borrowListTemp.size();i++ ){
            borrow = borrowListTemp.get(i);
            if(borrow.getStatus() == 1){
                long j = new Date().getTime() - borrow.getDate().getTime();
                long k = j/(1000*60*60);
                if(k > (period*24)){
                    fine = ((k - period*24)/24)*finePerDay;
                }

                if(fine > 0){
                    borrow.setFine(fine);
                    borrow.setFineStatus(1);
                }
                BorrowRepository.save(borrow);
            }
        }

        borrowListTemp = BorrowRepository.findByReaderName(readerName);


        for(int i = 0; i < borrowListTemp.size();i++){
            borrow = borrowListTemp.get(i);
            if(borrow.getStatus() == 1){
                borrowList.add(borrow);
            }else{
                returnList.add(borrow);
            }
        }

        for(int i = reserveListTemp.size() - 1;i >= 0; i--){
            reserve = reserveListTemp.get(i);
            if(reserve.getStatus() == 1){
                long j = new Date().getTime() - reserve.getDate().getTime();
                long k = j/(1000*60);
                if(k > 120){
                    reserve.setStatus(0);
                    ReserveRepository.save(reserve);
                    reserveListTemp.remove(reserve);
                }
            }else{//status = 0
                reserveListTemp.remove(reserve);
            }
        }//reserveList中是有效的被预约的书

        List<Reserve> reserveList = reserveListTemp;
        reserveListTemp = ReserveRepository.findByReaderName(readerName);

        for(int j = 0;j < reserveList.size();j++){
            reserve = reserveList.get(j);
            if(reserveListTemp.contains(reserve)){
                reserveListTemp.remove(reserve);
            }
        }
        List<Reserve> reservedList = reserveListTemp;


        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("borrowList",borrowList);
        model.addAttribute("reserveList",reserveList);
        model.addAttribute("returnList",returnList);
        model.addAttribute("reservedList",reservedList);
        return "book/showReaderRecord";
    }
}
