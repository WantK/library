package com.example.library.Controller;

import com.example.library.Entity.DeleteBook;
import com.example.library.Repository.deleteBookRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class deleteBookController {

    @Autowired
    private deleteBookRepository DeleteBookRepository;

    @GetMapping(value = "/deleteHistoryIndex")
    public String deleteHistoryIndex(HttpServletRequest request,
                                     Model model){
        List<DeleteBook> deleteBookList = DeleteBookRepository.findAll();
        List<DeleteBook> showDelete = new ArrayList<>();
        DeleteBook deleteBook;
        Set<String> ISBN = new HashSet<>();
        for(int i = 0 ; i < deleteBookList.size(); i++ ){
            deleteBook = deleteBookList.get(i);
            if(ISBN.add(deleteBook.getIsbn())){
                showDelete.add(deleteBook);
            }
        }
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("showDelete",showDelete);
        return "librarian/showDelete";
    }

    @GetMapping(value = "/showDeletedDetail/{ISBN}")
    public String showDeleteDetail(HttpServletRequest request,
                                   @PathVariable("ISBN")String ISBN,
                                   Model model){
        List<DeleteBook> deleteBookList = DeleteBookRepository.findByIsbn(ISBN);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("deleteBookList",deleteBookList);
        return "librarian/showDeleteBook";
    }
}
