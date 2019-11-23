package com.example.library.Controller;

import com.example.library.Entity.Borrow;
import com.example.library.Entity.Reader;
import com.example.library.Repository.adminRepository;
import com.example.library.Repository.borrowRepository;
import com.example.library.Repository.readerRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class returnController {
    @Autowired
    private readerRepository ReaderRepository;
    @Autowired
    private borrowRepository BorrowRepository;
    @Autowired
    private adminRepository AdminRepository;

    @RequestMapping(value = "/returnIndex",method = RequestMethod.GET)
    private String returnIndex(HttpServletRequest request,
                               Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/return";
    }


    @RequestMapping(value = "/returnBook", method = RequestMethod.POST)
    @ResponseBody
    private String returnBook(@RequestParam(value = "bookId") Integer bookId,
                              @RequestParam(value = "readerName") String readerName){
        Reader reader = ReaderRepository.findByReaderName(readerName);
        if(reader == null){
            return "Please check the readerName, and enter the right readerName";
        }

        Borrow borrow = BorrowRepository.findByBookId(bookId);
        System.out.println(readerName);
        if(borrow == null){
            return "No borrow record";
        }else if(!borrow.getReaderName().equals(readerName)){
            return "Not borrowed by"+readerName;
        }else if(borrow.getStatus() == 0){
            return "Already returned";
        }else{
            int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
            double finePerDay = AdminRepository.findByAdminName("admin").getFineValue();
            double fine = 0.0;
            long j = new Date().getTime() - borrow.getDate().getTime();
            long k = j/(1000*60*60);
            if(k > (period*24)){
                fine = ((k - period*24)/24)*finePerDay;
            }

            if(fine > 0){
                borrow.setStatus(0);
                borrow.setFine(fine);
                borrow.setFineStatus(1);
            }else{
                borrow.setStatus(0);
            }

            BorrowRepository.save(borrow);
            return "Returned Successfully";
    }
}
}
