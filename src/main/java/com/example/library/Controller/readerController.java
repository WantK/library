package com.example.library.Controller;

import com.example.library.Entity.*;
import com.example.library.Repository.*;
import com.example.library.Service.readerService;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class readerController {
    @Autowired
    private readerRepository ReaderRepository;

    @Autowired
    private bookRepository BookRepository;

    @Autowired
    private readerService ReaderService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private borrowRepository BorrowRepository;
    @Autowired
    private reserveRepository ReserveRepository;
    @Autowired
    private adminRepository AdminRepository;

    @RequestMapping(value = "/historyIndex",method = RequestMethod.GET)
    public String historyIndex(HttpServletRequest request,
                               Model model){
//        List<Borrow> borrowListTemp = BorrowRepository.findByReaderName(readerName);
//        List<Reserve> reserveListTemp = ReserveRepository.findByReaderName(readerName);
//
//        List<Borrow> borrowList = new ArrayList<>();
//        List<Reserve> reserveList = reserveListTemp;
//        List<Borrow> returnList = new ArrayList<>();
//        List<Reserve> reservedList = new ArrayList<>();
//
//        Borrow borrow;
//        Reserve reserve;
//        for(int i = 0; i < borrowListTemp.size();i++){
//            borrow = borrowListTemp.get(i);
//            if(borrow.getStatus() == 1){
//                borrowList.add(borrow);
//            }else{
//                returnList.add(borrow);
//            }
//        }
//
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
//
//        for(int i = 0; i < reserveListTemp.size();i++){
//            reserve = reserveListTemp.get(i);
//            if(!reserveList.contains(reserve)){
//                reservedList.add(reserve);
//            }
//        }

        String readerName = CookieUtils.getCookieValue(request,"readerName");
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
        model.addAttribute("readerName",readerName);
        model.addAttribute("borrowList",borrowList);
        model.addAttribute("reserveList",reserveList);
        model.addAttribute("returnList",returnList);
        model.addAttribute("reservedList",reservedList);
        return "reader/historyRecord";
    }


//    @RequestMapping(value = "/readerSucceed/{UserName}", method = RequestMethod.GET)
//    public String adminLogin(@PathVariable("UserName") String readerName,
//                             Model model){
//        model.addAttribute("readerName",readerName);
//        return "reader/loginSucceed";
//    }
    @RequestMapping(value = "/readerSucceed", method = RequestMethod.GET)
    public String readerLogin(Model model,
                              HttpServletRequest request){
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        return "reader/loginSucceed";
    }


    @RequestMapping(value = "/addReader", method = RequestMethod.POST)
    @ResponseBody
    public String addReader(@RequestParam(value = "readerName") String readerName,
                            @RequestParam(value = "readerPassword") String readerPassword,
                            @RequestParam(value = "readerEmail") String readerEmail){
        Reader reader = new Reader();
        reader.setReaderName(readerName);
        if(readerPassword.equals(""))
            readerPassword = "12345678";
        reader.setReaderPassword(readerPassword);
        reader.setEmail(readerEmail);
        reader.setDate(new Date());
        reader.setDeposit(AdminRepository.findByAdminName("admin").getDeposit());

        if(ReaderRepository.findByReaderName(readerName) != null){  //判断该账户是否已经被注册
            return "The reader account has been registered.";
        }

        ReaderRepository.save(reader);
        return "Add reader successfully";
    }

    @RequestMapping(value = "/delete/reader", method = RequestMethod.POST)
    @ResponseBody
    public String deleteReader(@RequestParam("readerName") String readerName){
        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        Borrow borrow;
        for(int i = 0;i < borrowList.size();i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1){
                return "Reader " + readerName + " should return books";
            }else{
                if(borrow.getFineStatus() == 1){
                    return "Reader " + readerName + " should pay fine";
                }
            }
        }
        Reader reader = ReaderRepository.findByReaderName(readerName);
        ReaderRepository.deleteById(reader.getReaderId());
        return "Delete successfully";
    }

    @RequestMapping(value = "/editReaderPage/readerName={readerName}", method = RequestMethod.GET)
    public String editReaderPage(@PathVariable("readerName") String readerName,
                                 HttpServletRequest request,
                                 Model model){
        Reader reader = ReaderRepository.findByReaderName(readerName);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("reader",reader);
        return "librarian/editReader";
    }

    @RequestMapping(value = "/editReader",method = RequestMethod.PUT)
    @ResponseBody
    public String editReader(@RequestParam("readerId") Integer readerId,
                             @RequestParam("readerName") String readerName,
                             @RequestParam("readerPassword") String readerPassword,
                             @RequestParam("email") String readerEmail){
        Reader reader = ReaderRepository.findById(readerId).orElse(null);
        reader.setReaderName(readerName);
        reader.setReaderPassword(readerPassword);
        reader.setEmail(readerEmail);
        ReaderRepository.save(reader);
        return "Edit successfully";
    }

    @RequestMapping(value = "/changeReaderInformationPage", method = RequestMethod.GET)
    public String changeReaderInformationPage(HttpServletRequest request, Model model){
        Reader reader = ReaderRepository.findByReaderName(CookieUtils.getCookieValue(request,"readerName"));
        model.addAttribute("reader",reader);
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        return "reader/changeReaderInformation";
    }

    @RequestMapping(value = "/changeReaderInformation", method = RequestMethod.POST)
    @ResponseBody
    public String changeReaderInformation(@Valid Reader reader){
        ReaderRepository.save(reader);
        return "Edit successfully";
    }


    @RequestMapping(value = "/recoveryReaderPassword", method = RequestMethod.GET)
    public String recoveryReaderPassword(){
        return "reader/recoveryReaderPassword";
    }

    @RequestMapping(value = "/getEmail",method = RequestMethod.POST)
    @ResponseBody
    public void getEmail(@RequestParam("readerName") String readerName,@RequestParam("readerEmail") String readerEmail){
        Reader reader = ReaderRepository.findByReaderName(readerName);
        if(reader != null){
            //建立邮件消息
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //发送者
            simpleMailMessage.setFrom("Jinbin_z@163.com");
            //接收者
            simpleMailMessage.setTo(readerEmail);
            //发送的标题
            simpleMailMessage.setSubject("获取Bibliosoft系统登陆密码");
            //发送的内容
            simpleMailMessage.setText("Dear "+readerName+":\n      Your Bibliosoft password is : "+ reader.getReaderPassword());
            //发送
            javaMailSender.send(simpleMailMessage);
        }

    }

    @RequestMapping(value = "/timeDown",method = RequestMethod.GET)
    public String timeDown(){
        return "reader/timeDown";
    }


    @GetMapping(value = "/searchReader/keywords={keywords}")
    public String searchReaderName(@PathVariable("keywords") String keywords,
                                   HttpServletRequest request,
                                   Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        List<Reader> allReaderList = ReaderRepository.findAll();
        List<Reader> readerList = new ArrayList<>();
        Reader reader;
        Pattern compile = Pattern.compile(keywords);
        for(int i = 0; i < allReaderList.size();i++){
            reader = allReaderList.get(i);
            if(compile.matcher(reader.getReaderId() + " " +reader.getReaderName() + " " + reader.getEmail()).find()){
                readerList.add(reader);
            }
        }
        model.addAttribute("readerList",readerList);
        return "librarian/searchReaderResult";
    }

    @RequestMapping(value = "/existReader",method = RequestMethod.POST)
    @ResponseBody
    public Integer existReader(@RequestParam("readerName")String readerName){
        Reader reader = ReaderRepository.findByReaderName(readerName);
        if(reader!=null)
            return 1;
        else
            return 0;
    }
}
