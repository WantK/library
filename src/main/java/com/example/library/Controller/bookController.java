package com.example.library.Controller;

import com.example.library.Entity.*;
import com.example.library.Entity.Reader;
import com.example.library.Repository.*;
import com.example.library.Util.BarcodeUtil;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


@Controller
public class bookController {
    @Autowired
    bookRepository BookRepository;

    @Autowired
    borrowRepository BorrowRepository;

    @Autowired
    private reserveRepository ReserveRepository;

    @Autowired
    private deleteBookRepository DeleteBookRepository;

    @Autowired
    private categoryRepository CategoryRepository;

    @Autowired
    private locationRepository LocationRepository;

    @RequestMapping(value = "/insertBook",method = RequestMethod.GET)
    public String insert(HttpServletRequest request,
                         Model model){
        List<Category> categoryList = CategoryRepository.findAll();
        model.addAttribute("categoryList",categoryList);
        List<Location> locationList = LocationRepository.findAll();
        model.addAttribute("locationList",locationList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/addBook";
    }

    @RequestMapping(value = "/insertMutualBook",method = RequestMethod.GET)
    public String insertMutual(HttpServletRequest request,
                         Model model){
        List<Category> categoryList = CategoryRepository.findAll();
        model.addAttribute("categoryList",categoryList);
        List<Location> locationList = LocationRepository.findAll();
        model.addAttribute("locationList",locationList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "book/mutualAddBook";
    }

    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    @ResponseBody
    public String addBook(@Valid Book book) {
        System.out.println(book);
        int bookCount = book.getCount();//当前要添加的图书个数
        List<Book> bookList = BookRepository.findByIsbn(book.getIsbn());
        int bookCountInDatabase = bookList.size(); //数据库中已有的这种图书的个数
        if(bookCountInDatabase != 0){         //如果数据库中已经有这种图书，那么更新数据库中图书的count值
            book.setCount(bookCount + bookCountInDatabase);
            for(int i = 0; i < bookCountInDatabase; i++ ){
                bookList.get(i).setCount(bookCount + bookCountInDatabase);
                BookRepository.save(bookList.get(i));
            }
        }
        for(int i = 0; i < bookCount; i++){          //将新图书添加到数据库中
            Book bookNew = new Book();
            bookNew.setTitle(book.getTitle());
            bookNew.setAuthor(book.getAuthor());
            bookNew.setImage(book.getImage());
            bookNew.setCount(book.getCount());
            bookNew.setPrice(book.getPrice());
            bookNew.setIsbn(book.getIsbn());
            bookNew.setCategory(book.getCategory());
            bookNew.setFloor(book.getFloor());
            bookNew.setRoom(book.getRoom());
            bookNew.setShelf(book.getShelf());
            BookRepository.save(bookNew);
        }

        return "Add successfully";
    }

    @RequestMapping(value = "/addOwnBook",method = RequestMethod.POST)
    @ResponseBody
    public String addOwnBook(@RequestParam(value = "title") String title,
                             @RequestParam(value = "author") String author,
                             @RequestParam(value = "price") String price,
                             @RequestParam(value = "isbn") String isbn,
                             @RequestParam(value = "count") Integer count,
                             @RequestParam(value = "category") String category,
                             @RequestParam(value = "floor") String floor,
                             @RequestParam(value = "room") String room,
                             @RequestParam(value = "shelf") String shelf,
                             @RequestParam(value = "image") MultipartFile image) {
        System.out.println(title+author);
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(new File(".\\src\\main\\webapp\\bookWithoutISBN\\"+title+".jpg")));//保存图片到目录下
            out.write(image.getBytes());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "上传失败," + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败," + e.getMessage();
        }
        String Image="/bookWithoutISBN/"+title+".jpg";

        int bookCount = count;//当前要添加的图书个数
        List<Book> bookList = BookRepository.findByIsbn(isbn);
        int bookCountInDatabase = bookList.size(); //数据库中已有的这种图书的个数
        if(bookCountInDatabase != 0){         //如果数据库中已经有这种图书，那么更新数据库中图书的count值
            bookCount = bookCount + bookCountInDatabase;
            for(int i = 0; i < bookCountInDatabase; i++ ){
                bookList.get(i).setCount(bookCount);
                BookRepository.save(bookList.get(i));
            }
        }
        for(int i = 0; i < count; i++){          //将新图书添加到数据库中
            Book bookNew = new Book();
            bookNew.setTitle(title);
            bookNew.setAuthor(author);
            bookNew.setImage(Image);
            bookNew.setCount(bookCount);
            bookNew.setPrice(price);
            bookNew.setIsbn(isbn);
            bookNew.setCategory(category);
            bookNew.setFloor(floor);
            bookNew.setRoom(room);
            bookNew.setShelf(shelf);
            BookRepository.save(bookNew);
        }

        return "Add successfully";
    }

    @RequestMapping(value = "/editBooksPage/isbn={isbn}", method = RequestMethod.GET)
    public String editBooksPage(@PathVariable("isbn") String isbn,
                                HttpServletRequest request,
                                Model model){
        List<Category> categoryList = CategoryRepository.findAll();
        model.addAttribute("categoryList",categoryList);
        List<Location> locationList = LocationRepository.findAll();
        model.addAttribute("locationList",locationList);
        Book book = BookRepository.findByIsbn(isbn).get(0);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("book",book);
        return "book/editBooks";
    }

    @RequestMapping(value = "/editBook",method = RequestMethod.POST)
    @ResponseBody
    public String editBook(@RequestParam(value = "isbn") String isbn,
                           @RequestParam(value = "category") String category,
                           @RequestParam(value = "floor") String floor,
                           @RequestParam(value = "room") String room,
                           @RequestParam(value = "shelf") String shelf){
        System.out.println(isbn+category+room+shelf);
        List<Book> bookList = BookRepository.findByIsbn(isbn);
        Book book;
        for(int i = 0; i < bookList.size(); i++){
            book = bookList.get(i);
            book.setCategory(category);
            book.setFloor(floor);
            book.setRoom(room);
            book.setShelf(shelf);
            BookRepository.save(book);
        }
        return "Update successfully";

    }

    @RequestMapping(value = "/searchIndex",method = RequestMethod.GET)
    public String searchIndex(HttpServletRequest request,
                              Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        List<Book> allBookList = BookRepository.findAll();
        List<Book> bookList = new ArrayList<>();
        Set<String> ISBN = new HashSet<>();
        Book book;
        Reserve reserve;
        for(int i = 0; i < allBookList.size(); i++){
            book = allBookList.get(i);
            if(ISBN.add(book.getIsbn())){
                List<Book> tempBookList = BookRepository.findByIsbn(book.getIsbn());
                List<Borrow> tempBorrowList = BorrowRepository.findByIsbn(book.getIsbn());
                List<Reserve> tempReserveList = ReserveRepository.findByIsbn(book.getIsbn());
                int rest = tempBookList.size();
                for(int k = 0;k < tempBorrowList.size();k++){
                    if(tempBorrowList.get(k).getStatus() == 1){
                        rest = rest - 1;
                    }
                }
                for(int k = 0;k < tempReserveList.size();k++){
                    reserve = tempReserveList.get(k);
                    if(reserve.getStatus() == 1){
                        long j = new Date().getTime() - reserve.getDate().getTime();
                        long h = j/(1000*60);
                        if(h > 120){
                            reserve.setStatus(0);
                            ReserveRepository.save(reserve);
                        }else{
                            rest = rest - 1;
                        }
                    }
                }
                book.setRestCount(rest);
                bookList.add(book);
            }
        }
        model.addAttribute("bookList",bookList);
        return "book/searchKeywords";
    }


    @RequestMapping(value = "/searchBook/{keywords}",method = RequestMethod.GET)
    public String searchBook(@PathVariable("keywords") String keywords,
                             HttpServletRequest request,
                             Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        List<Book> allBookList = BookRepository.findAll();
        List<Book> bookList = new ArrayList<>();
        Set<String> ISBN = new HashSet<>();
        Book book;
        Reserve reserve;
        Pattern compile = Pattern.compile(keywords);
//        Matcher matcher = compile.matcher(obj.get("author").toString());
//        matcher.find();
        for(int i = 0; i < allBookList.size();i++){
            book = allBookList.get(i);
//            if((book.getTitle()+" "+book.getAuthor()).contains(keywords)){
//                if(ISBN.add(book.getIsbn()))
//                    bookList.add(book);
//            }
            if(compile.matcher(book.getId()+" "+book.getTitle()+" " + book.getCategory() +" " +book.getAuthor()).find()){
                if(ISBN.add(book.getIsbn())){
                    List<Book> tempBookList = BookRepository.findByIsbn(book.getIsbn());
                    List<Borrow> tempBorrowList = BorrowRepository.findByIsbn(book.getIsbn());
                    List<Reserve> tempReserveList = ReserveRepository.findByIsbn(book.getIsbn());
                    int rest = tempBookList.size();
                    for(int k = 0;k < tempBorrowList.size();k++){
                        if(tempBorrowList.get(k).getStatus() == 1){
                            rest = rest - 1;
                        }
                    }
                    for(int k = 0;k < tempReserveList.size();k++){
                        reserve = tempReserveList.get(k);
                        if(reserve.getStatus() == 1){
                            long j = new Date().getTime() - reserve.getDate().getTime();
                            long h = j/(1000*60);
                            if(h > 120){
                                reserve.setStatus(0);
                                ReserveRepository.save(reserve);
                            }else{
                                rest = rest - 1;
                            }
                        }
                    }
                    book.setRestCount(rest);
                    bookList.add(book);
                }

            }
        }
        model.addAttribute("bookList",bookList);
        return "book/search";
    }

    @RequestMapping(value = "/showBook/keywords={keywords}",method = RequestMethod.GET)
    public String showBook(@PathVariable(value = "keywords") String keywords,
                           Model model){
        List<Book> allBookList = BookRepository.findAll();
        List<Book> bookList = new ArrayList<>();
        Set<String> ISBN = new HashSet<>();
        Book book;
        Reserve reserve;
        Pattern compile = Pattern.compile(keywords);
//        Matcher matcher = compile.matcher(obj.get("author").toString());
//        matcher.find();
        for(int i = 0; i < allBookList.size();i++){
            book = allBookList.get(i);
//            if((book.getTitle()+" "+book.getAuthor()).contains(keywords)){
//                if(ISBN.add(book.getIsbn()))
//                    bookList.add(book);
//            }
            if(compile.matcher(book.getId()+" "+book.getTitle()+" " + book.getCategory() +" " + book.getAuthor()).find()){
                if(ISBN.add(book.getIsbn())){
                    List<Book> tempBookList = BookRepository.findByIsbn(book.getIsbn());
                    List<Borrow> tempBorrowList = BorrowRepository.findByIsbn(book.getIsbn());
                    List<Reserve> tempReserveList = ReserveRepository.findByIsbn(book.getIsbn());
                    int rest = tempBookList.size();
                    for(int k = 0;k < tempBorrowList.size();k++){
                        if(tempBorrowList.get(k).getStatus() == 1){
                            rest = rest - 1;
                        }
                    }
                    for(int k = 0;k < tempReserveList.size();k++){
                        reserve = tempReserveList.get(k);
                        if(reserve.getStatus() == 1){
                            long j = new Date().getTime() - reserve.getDate().getTime();
                            long h = j/(1000*60);
                            if(h > 120){
                                reserve.setStatus(0);
                                ReserveRepository.save(reserve);
                            }else{
                                rest = rest - 1;
                            }
                        }
                    }
                    book.setRestCount(rest);
                    bookList.add(book);
                }

            }
        }
        model.addAttribute("bookList",bookList);
        return "book/searchOnlyToShow";
    }

    @RequestMapping(value = "/readerToShowBook/keywords={wd}",method = RequestMethod.GET)
    public String readerToShowBook(@PathVariable("wd")String keywords,
                                   HttpServletRequest request,
                                   Model model){
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        List<Book> allBookList = BookRepository.findAll();
        List<Book> bookList = new ArrayList<>();
        Set<String> ISBN = new HashSet<>();
        Book book;
        Reserve reserve;
        Pattern compile = Pattern.compile(keywords);
//        Matcher matcher = compile.matcher(obj.get("author").toString());
//        matcher.find();
        for(int i = 0; i < allBookList.size();i++){
            book = allBookList.get(i);
//            if((book.getTitle()+" "+book.getAuthor()).contains(keywords)){
//                if(ISBN.add(book.getIsbn()))
//                    bookList.add(book);
//            }
            if(compile.matcher(book.getId()+" "+book.getTitle()+" " + book.getCategory() +" "+book.getAuthor()).find()){
                if(ISBN.add(book.getIsbn())){
                    List<Book> tempBookList = BookRepository.findByIsbn(book.getIsbn());
                    List<Borrow> tempBorrowList = BorrowRepository.findByIsbn(book.getIsbn());
                    List<Reserve> tempReserveList = ReserveRepository.findByIsbn(book.getIsbn());
                    int rest = tempBookList.size();
                    for(int k = 0;k < tempBorrowList.size();k++){
                        if(tempBorrowList.get(k).getStatus() == 1){
                            rest = rest - 1;
                        }
                    }
                    for(int k = 0;k < tempReserveList.size();k++){
                        reserve = tempReserveList.get(k);
                        if(reserve.getStatus() == 1){
                            long j = new Date().getTime() - reserve.getDate().getTime();
                            long h = j/(1000*60);
                            if(h > 120){
                                reserve.setStatus(0);
                                ReserveRepository.save(reserve);
                            }else{
                                rest = rest - 1;
                            }
                        }
                    }
                    book.setRestCount(rest);
                    bookList.add(book);
                }

            }
        }

        model.addAttribute("bookList",bookList);
        return "book/searchReaderBook";
    }

    @RequestMapping(value = "/manageBook",method = RequestMethod.GET)
    public String manageBook(){
        return "book/manage";
    }



//    @RequestMapping(value = "/book/{ISBN}",method = RequestMethod.GET)
//    public String bookISBN(@PathVariable("ISBN") String ISBN,
//                           Model model){
//        List<Book> bookList = BookRepository.findByIsbn(ISBN);
//        List<Borrow> borrowList = BorrowRepository.findByIsbn(ISBN);//borrow表中同一ISBN号的书
//
//        List<Reserve> reserveList = ReserveRepository.findByIsbn(ISBN);
//        Reserve reserve;
//        for(int i = reserveList.size() - 1;i >= 0; i--){
//            reserve = reserveList.get(i);
//            if(reserve.getStatus() == 1){
//                long j = new Date().getTime() - reserve.getDate().getTime();
//                long k = j/(1000*60);
//                if(k > 120){
//                    reserve.setStatus(0);
//                    ReserveRepository.save(reserve);
//                    reserveList.remove(reserve);
//                }
//            }else{//status = 0
//                reserveList.remove(reserve);
//            }
//        }//reserveList中是有效的被预约的书
//
//        List<Integer> borrowBookID = new ArrayList<>();//用来找到没有被借的图书
//
//
//        List<Book> showBorrowList = new ArrayList<>();//show the borrowed books(List<Book>)
//        Borrow borrow;
//        Optional<Book> bookOptional;
//        for(int i = 0;i < borrowList.size(); i++){
//            borrow = borrowList.get(i);
//            if(borrow.getStatus() == 1) {
//                borrowBookID.add(borrow.getBookId());
//                bookOptional = BookRepository.findById(borrow.getBookId());
//                showBorrowList.add(bookOptional.get());
//            }
//        }
//
//
//        Book book;
//        Integer k;
//        for(int i = 0; i < borrowBookID.size(); i++){
//            k = borrowBookID.get(i);
//            for(int j = bookList.size() - 1;j >= 0 ;j--){
//                book = bookList.get(j);
//                if(book.getId() == k)
//                    bookList.remove(book);
//            }
//        }
//
//        for(int i = 0; i < reserveList.size(); i++){
//            reserve = reserveList.get(i);
//            for(int j = bookList.size() - 1;j >= 0 ;j--){
//                book = bookList.get(j);
//                if(book.getId() == reserve.getBookId())
//                    bookList.remove(book);
//            }
//        }
//
//        //将reserveList变成内置类型为bookList
//        List<Book> reserveBook = new ArrayList<>();
//        for(int i = 0; i < reserveList.size();i++){
//            reserve = reserveList.get(i);
//            Optional<Book> bookTemp = BookRepository.findById(reserve.getBookId());
//            reserveBook.add(bookTemp.get());
//        }
//
//
////        Iterator<Book> it = bookList.iterator();
////        while(it.hasNext()){
////            Book x = it.next();
////            if(x.equals("del")){
////                it.remove();
////            }
////        }
//        model.addAttribute("bookList",bookList);
//        model.addAttribute("reserveList",reserveBook);
//        model.addAttribute("showBorrowList",showBorrowList);
//        return "book/showBook";
//    }

    @RequestMapping(value = "/bookLibrarian/{ISBN}",method = RequestMethod.GET)
    public String bookLibrarian(HttpServletRequest request,
                                @PathVariable("ISBN") String ISBN,
                                Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        List<Book> bookList = BookRepository.findByIsbn(ISBN);

        model.addAttribute("bookList",bookList);
        return "book/showBookLibrarian";
    }
    @RequestMapping(value = "/bookOnlyToShow/{ISBN}",method = RequestMethod.GET)
    public String bookOnlyToShow(@PathVariable("ISBN") String ISBN,
                                Model model){
        List<Book> bookList = BookRepository.findByIsbn(ISBN);

        model.addAttribute("bookList",bookList);
        return "book/onlyShowBook";
    }

    @RequestMapping(value = "/bookShowToReader/{ISBN}",method = RequestMethod.GET)
    public String bookShowToReader(@PathVariable("ISBN") String ISBN,
                                   HttpServletRequest request,
                                   Model model){
        List<Book> bookList = BookRepository.findByIsbn(ISBN);//ISBN
        List<Borrow> borrowList = BorrowRepository.findByIsbn(ISBN);//borrow表中同一ISBN号的书

        List<Reserve> reserveList = ReserveRepository.findByIsbn(ISBN);
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

        List<Integer> borrowBookID = new ArrayList<>();//用来找到没有被借的图书


        List<Book> showBorrowList = new ArrayList<>();//show the borrowed books(List<Book>)
        Borrow borrow;
        Optional<Book> bookOptional;
        for(int i = 0;i < borrowList.size(); i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1) {
                borrowBookID.add(borrow.getBookId());
                bookOptional = BookRepository.findById(borrow.getBookId());
                showBorrowList.add(bookOptional.get());
            }
        }


        Book book;
        Integer k;
        for(int i = 0; i < borrowBookID.size(); i++){
            k = borrowBookID.get(i);
            for(int j = bookList.size() - 1;j >= 0 ;j--){
                book = bookList.get(j);
                if(book.getId().equals(k))
                    bookList.remove(book);
            }
        }

        for(int i = 0; i < reserveList.size(); i++){
            reserve = reserveList.get(i);
            for(int j = bookList.size() - 1;j >= 0 ;j--){
                book = bookList.get(j);
                if(book.getId().equals(reserve.getBookId()))
                    bookList.remove(book);
            }
        }

        //将reserveList变成内置类型为bookList
        List<Book> reserveBook = new ArrayList<>();
        for(int i = 0; i < reserveList.size();i++){
            reserve = reserveList.get(i);
            Optional<Book> bookTemp = BookRepository.findById(reserve.getBookId());
            reserveBook.add(bookTemp.get());
        }


//        Iterator<Book> it = bookList.iterator();
//        while(it.hasNext()){
//            Book x = it.next();
//            if(x.equals("del")){
//                it.remove();
//            }
//        }
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        model.addAttribute("bookList",bookList);
        model.addAttribute("reserveList",reserveBook);
        model.addAttribute("showBorrowList",showBorrowList);
        return "book/showBook";
    }


    @RequestMapping(value = "/delete/isbn={isbn}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteBooks(@PathVariable("isbn") String isbn,
                              HttpServletRequest request){
        List<Book> books = BookRepository.findByIsbn(isbn);
        DeleteBook deleteBook = new DeleteBook();
        for(Book book : books){
            deleteBook.setId(book.getId());
            deleteBook.setTitle(book.getTitle());
            deleteBook.setAuthor(book.getAuthor());
            deleteBook.setPrice(book.getPrice());
            deleteBook.setImage(book.getImage());
            deleteBook.setIsbn(book.getIsbn());
            deleteBook.setCount(book.getCount());
            deleteBook.setBarcode(book.getBarcode());
            deleteBook.setCategory(book.getCategory());
            deleteBook.setFloor(book.getFloor());
            deleteBook.setRoom(book.getRoom());
            deleteBook.setShelf(book.getShelf());
            deleteBook.setLibrarianName(CookieUtils.getCookieValue(request,"librarianName"));
            DeleteBookRepository.save(deleteBook);
            deleteBook = new DeleteBook();
            BookRepository.deleteById(book.getId());
        }
        return "Delete books which have the ISBN" + isbn;
    }


    @RequestMapping(value = "deleteBook/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String deleteBook(@PathVariable("id") Integer id,
                             HttpServletRequest request){
        Optional<Book> bookOptional = BookRepository.findById(id);
        Book book = bookOptional.get();
        List<DeleteBook> deleteBookList = DeleteBookRepository.findByIsbn(book.getIsbn());
        DeleteBook deleteBook = new DeleteBook();
        if(deleteBookList.isEmpty()){
            deleteBook.setId(id);
            deleteBook.setTitle(book.getTitle());
            deleteBook.setAuthor(book.getAuthor());
            deleteBook.setPrice(book.getPrice());
            deleteBook.setImage(book.getImage());
            deleteBook.setIsbn(book.getIsbn());
            deleteBook.setCount(book.getCount());
            deleteBook.setBarcode(book.getBarcode());
            deleteBook.setCategory(book.getCategory());
            deleteBook.setFloor(book.getFloor());
            deleteBook.setRoom(book.getRoom());
            deleteBook.setShelf(book.getShelf());
            deleteBook.setLibrarianName(CookieUtils.getCookieValue(request,"librarianName"));
            DeleteBookRepository.save(deleteBook);
            BookRepository.deleteById(id);
        }else{
            int count = 0;
            for(int i = 0;i < deleteBookList.size();i++){
                deleteBook = deleteBookList.get(i);
                deleteBook.setCount(deleteBook.getCount() + 1);
                count = deleteBook.getCount() + 1;
                DeleteBookRepository.save(deleteBook);
            }
            deleteBook = new DeleteBook();
            deleteBook.setId(id);
            deleteBook.setTitle(book.getTitle());
            deleteBook.setAuthor(book.getAuthor());
            deleteBook.setPrice(book.getPrice());
            deleteBook.setImage(book.getImage());
            deleteBook.setIsbn(book.getIsbn());
            deleteBook.setCount(count);
            deleteBook.setBarcode(book.getBarcode());
            deleteBook.setCategory(book.getCategory());
            deleteBook.setFloor(book.getFloor());
            deleteBook.setRoom(book.getRoom());
            deleteBook.setShelf(book.getShelf());
            deleteBook.setLibrarianName(CookieUtils.getCookieValue(request,"librarianName"));
            DeleteBookRepository.save(deleteBook);
            BookRepository.deleteById(id);
        }
        return "Delete successfully";
    }

    @RequestMapping(value = "/showNewBooks/{isbn}", method = RequestMethod.GET)
    public String showNewBooks(@PathVariable("isbn") String isbn,
                               HttpServletRequest request,
                               Model model) throws IOException {
        List<Book> bookList = BookRepository.findByIsbn(isbn);
        for(int i =bookList.size() - 1;i >= 0;i--){
            Book book = bookList.get(i);
            if(book.getBarcode() == null){
                File file =  BarcodeUtil.generateFile(book.getId().toString(),book.getId().toString());
                if(file.exists()){
                    book.setBarcode("/barcode/" + book.getId() + ".png");
                    BookRepository.save(book);
                }
            }
            else{
                bookList.remove(i);
            }
        }
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("bookList",bookList);
        return "book/showNewBooks";
    }

    @RequestMapping(value = "/existBook",method = RequestMethod.POST)
    @ResponseBody
    public Integer existReader(@RequestParam("bookId")Integer bookId){
        List<Book> bookList = BookRepository.findAll();
        for(Book book : bookList){
            if(book.getId().equals(bookId))
                return 1;
        }
        return 0;
    }
}
