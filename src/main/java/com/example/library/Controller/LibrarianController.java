package com.example.library.Controller;

import com.example.library.Entity.Book;
import com.example.library.Entity.Librarian;
import com.example.library.Entity.Reader;
import com.example.library.Repository.librarianRepository;
import com.example.library.Repository.readerRepository;
import com.example.library.Service.HttpClient;
import com.example.library.Util.BarcodeUtil;
import com.example.library.Util.CookieUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.example.library.Repository.bookRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class LibrarianController {
    @Autowired
    librarianRepository LibrarianRepository;
    @Autowired
    readerRepository ReaderRepository;

    @Autowired
    bookRepository BookRepository;

    @Autowired
    HttpClient httpClient;

    @RequestMapping(value = "/manageLibrarian",method = RequestMethod.GET)
    public String manageLibrarian(HttpServletRequest request,Model model){
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        List<Librarian> librarianList = LibrarianRepository.findAll();
        model.addAttribute("librarianList",librarianList);
        return "librarian/manage";
    }

    @RequestMapping(value = "/manageReader",method = RequestMethod.GET)
    public String manageReader(HttpServletRequest request,
                               Model model){
        List<Reader> readerList = ReaderRepository.findAll();
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("readerList",readerList);
        return "reader/manage";
    }

    @RequestMapping(value = "/registerReader",method = RequestMethod.GET)
    public String register(HttpServletRequest request,
                           Model model) {
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/registerReader";
    }

//    @RequestMapping(value = "/librarian", method = RequestMethod.POST)
//    public void librarianAdd(@Valid Librarian librarian){
//        librarian.setLibrarianName(librarian.getLibrarianName());
//        librarian.setLibrarianPassword(librarian.getLibrarianPassword());
//        LibrarianRepository.save(librarian);
//    }

//    @RequestMapping(value = "librarian_id={id}", method = RequestMethod.DELETE)
//    public void librarianDelete(@PathVariable("id") Integer id){  //删除
//        LibrarianRepository.deleteById(id);
//    }


    @RequestMapping(value = "/librarianSucceed", method = RequestMethod.GET)
    public String loginSucceed(){
//        List<Reader> readerList = ReaderRepository.findAll();
//        model.addAttribute("readerList",readerList);
//        List<Book> bookList = BookRepository.findAll();
//        Map<String,Book> map = new HashMap();
//        for(Book book : bookList){
//            map.put(book.getIsbn(),book);
//        }
//        bookList.clear();
//        for (String key : map.keySet()) {
//            bookList.add(map.get(key));
//        }
//        model.addAttribute("bookList",bookList);
        return "librarian/loginSucceed";

    }

    @RequestMapping(value = "/addLibrarian",method = RequestMethod.POST)
    @ResponseBody
    public String addLibrarian(@RequestParam(value = "librarianName") String librarianName,
                               @RequestParam(value = "librarianPassword") String librarianPassword){
        Librarian librarian = new Librarian();
        librarian.setLibrarianName(librarianName);
        if(librarianPassword.equals(""))
            librarianPassword = "00010001";
        librarian.setLibrarianPassword(librarianPassword);

        if(LibrarianRepository.findByLibrarianName(librarianName) != null){  //判断该账户是否已经被注册
            return "The librarian account has been registered.";
        }
        LibrarianRepository.save(librarian);
        return "Add successfully";
    }

    @RequestMapping(value = "/delete/librarianName", method = RequestMethod.POST)
    @ResponseBody
    public String deleteLibrarian(@RequestParam("librarianName") String LibrarianName){
        List<Librarian> librarianList = LibrarianRepository.findByLibrarianName(LibrarianName);
        Librarian librarian;
        for(int k = 0;k < librarianList.size();k++){
            librarian = librarianList.get(k);
            LibrarianRepository.deleteById(librarian.getLibrarianId());
        }
        return "Delete successfully";
    }
    @RequestMapping(value = "/editLibrarianPage/librarianName={librarianName}", method = RequestMethod.GET)
    public String editLibrarianPage(@PathVariable("librarianName") String librarianName,
                                    HttpServletRequest request,
                                    Model model){
        Librarian librarian = LibrarianRepository.findByLibrarianName(librarianName).get(0);
        model.addAttribute("librarian",librarian);
        /*List<Librarian> librarianList = LibrarianRepository.findByLibrarianName(librarianName);
        model.addAttribute("librarianList",librarianList);*/
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "librarian/editLibrarian";
    }

    @RequestMapping(value = "/editLibrarian",method = RequestMethod.POST)
    @ResponseBody
    public void editLibrarian(@Valid Librarian librarian){
            LibrarianRepository.save(librarian);
    }

    @RequestMapping(value = "/recoveryLibrarianPassword",method = RequestMethod.POST)
    @ResponseBody
    public String recoveryLibrarianPassword(@RequestParam("librarianName") String librarianName){
        Librarian librarian = LibrarianRepository.findByLibrarianName(librarianName).get(0);
        librarian.setLibrarianPassword("00010001");
        LibrarianRepository.save(librarian);
        return "Recovery successfully";
    }


    @RequestMapping(value = "/getBookInformation/isbn={isbn}", method = RequestMethod.POST)
    @ResponseBody
    public Book getBookInformation(@PathVariable("isbn") String isbn) throws Exception {
        List<Book> books = BookRepository.findByIsbn(isbn);
        if(books.size() != 0){
            return books.get(0);
        }
        else{
            Book book = new Book();
//            HttpRequest httpRequest = new HttpRequest(isbn);
//            httpRequest.sendGet();
//            book.setTitle(httpRequest.getTitle());
//            book.setImage(httpRequest.getImg());
//            book.setAuthor(httpRequest.getAuthor());
//            book.setPrice(httpRequest.getPrice());
            String url = "https://api.douban.com/v2/book/isbn/"+isbn;
            HttpMethod method = HttpMethod.GET;
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            JSONObject obj = JSONObject.fromObject(httpClient.client(url,method,params));

            book.setTitle(obj.get("title").toString());

            Pattern compile = Pattern.compile("\"(.*)\"");
            Matcher matcher = compile.matcher(obj.get("author").toString());
            matcher.find();
            String[] temp = matcher.group(1).split("\"");
            String author = "";
            for(int i = 0; i < temp.length; i++){
                author = author + temp[i];
            }
            book.setAuthor(author);


            compile = Pattern.compile("(\\d+\\.\\d+)|(\\d+)");
            matcher = compile.matcher(obj.get("price").toString());
            matcher.find();
            String price = matcher.group();
            book.setPrice(price);

            Map m = (Map) JSONObject.toBean((JSONObject)obj.get("images"),Map.class);
            book.setImage(m.get("small").toString());
            return book;
        }
    }



}
