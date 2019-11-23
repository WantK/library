package com.example.library.Controller;

import com.example.library.Entity.Borrow;
import com.example.library.Entity.Reader;
import com.example.library.Repository.adminRepository;
import com.example.library.Repository.borrowRepository;
import com.example.library.Service.fineService;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class fineController {
    @Autowired
    private borrowRepository BorrowRepository;
    @Autowired
    private adminRepository AdminRepository;
    @Autowired
    private fineService FineService;

    @PersistenceContext
    private EntityManager em;

    @GetMapping(value = "/fineIndex")
    public String fineIndex(HttpServletRequest request,Model model){
        String readerName = CookieUtils.getCookieValue(request,"readerName");
        double fineHavePaid = 0.0;
        double fineCurrently = 0.0;
        double fine = 0.0;

        int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
        double finePerDay = AdminRepository.findByAdminName("admin").getFineValue();

        List<Borrow> borrowListTemp = BorrowRepository.findByReaderName(readerName);
        Borrow borrow;
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

        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        List<Borrow> borrowFine = new ArrayList<>();
        List<Borrow> returnFine = new ArrayList<>();
        List<Borrow> fineHistory = new ArrayList<>();
        for(int i = 0; i < borrowList.size();i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1){
                if(borrow.getFineStatus() == 1){
                    fineCurrently = fineCurrently + borrow.getFine();
                    borrowFine.add(borrow);
                }
            }
            if(borrow.getStatus() == 0){
                if(borrow.getFineStatus() == 1){
                    fineCurrently = fineCurrently + borrow.getFine();
                    returnFine.add(borrow);
                }
            }
            if(borrow.getFineStatus() == 0){
                if(borrow.getFine() > 0){
                    fineHavePaid = fineHavePaid + borrow.getFine();
                    fineHistory.add(borrow);
                }

            }
        }

        model.addAttribute("readerName",readerName);
        model.addAttribute("fineCurrently",fineCurrently);
        model.addAttribute("fineHavePaid",fineHavePaid);
        model.addAttribute("borrowFineList",borrowFine);
        model.addAttribute("returnFineList",returnFine);
        model.addAttribute("fineHistory",fineHistory);
        return "reader/fineView";
    }

    @GetMapping(value = "/showIncome")
    public String showIncome(Model model,
                             HttpServletRequest request){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/showIncome";
    }

    @GetMapping(value = "/queryMonthIncome/{year}/{month}")
    public String queryMonth(@PathVariable("year") int year,
                           @PathVariable("month") int month,
                           HttpServletRequest request,
                           Model model){
        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        List<Reader> readerList = new ArrayList<>();
        Reader reader;
//                = dateFormat.parse("2010-09-13 22:36:01");

        String start = year+ "-"+ month +"-01 00:00:00";
        String end;
        if(month == 12){
            end = (year + 1)+ "-01-01 00:00:00";
        }else{
            end = year+ "-"+ (month+1) +"-01 00:00:00";
        }
        try{
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
        }catch (ParseException e){
            e.getMessage();
        }
        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        readerList = query.getResultList();
        double totalCount = 0.0;
        for(int j = 0; j < readerList.size() ; j++){
            reader = readerList.get(j);
            totalCount = totalCount + reader.getDeposit();
        }
        model.addAttribute("totalDeposit",totalCount);
        model.addAttribute("readerList",readerList);


        query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
        List<Borrow> borrowList = new ArrayList<>();
        Borrow borrow;
//                = dateFormat.parse("2010-09-13 22:36:01");
//        start = year+ "-"+ month +"-01 00:00:00";
////        if(month == 12){
////            end = (year + 1)+ "-01-01 00:00:00";
////        }else{
////            end = year+ "-"+ (month+1) +"-01 00:00:00";
////        }
////        try{
////            startDate = dateFormat.parse(start);
////            endDate = dateFormat.parse(end);
////        }catch (ParseException e){
////            e.getMessage();
////        }
        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        borrowList = query.getResultList();
        totalCount = 0.0;
        for(int j = 0; j< borrowList.size();j++){
            borrow = borrowList.get(j);
            totalCount = totalCount + borrow.getFine();
        }
        model.addAttribute("totalFine",totalCount);
        model.addAttribute("borrowList",borrowList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("borrowDate",year+"-"+month);
        return "librarian/showIncome";
    }

    @GetMapping(value = "/queryDayIncome/{year}/{month}/{day}")
    public String queryDay(@PathVariable("year") int year,
                           @PathVariable("month") int month,
                           @PathVariable("day") int day,
                           HttpServletRequest request,
                           Model model){
        List<Integer> bigMonth = new ArrayList<>(Arrays.asList(1,3,5,7,8,10));
        List<Integer> smallMonth = new ArrayList<>(Arrays.asList(4,6,9,11));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        List<Reader> readerList = new ArrayList<>();
        Reader reader;
//                = dateFormat.parse("2010-09-13 22:36:01");

        String start = year+ "-"+month+"-"+ day + " 00:00:00";
        String end = year+ "-"+ month +"-" + (day + 1) +" 00:00:00";
        if(bigMonth.contains(month)){
            if(day == 31){
                end = year+ "-"+ (month+1) +"-01 00:00:00";
            }
        }else if(smallMonth.contains(month)) {
            if(day == 30){
                end = year+ "-"+ (month+1) +"-01 00:00:00";
            }
        }else if(month == 12){
            if(day == 31){
                end = (year+1)+ "-01-01 00:00:00";
            }
        }else{
            if((year%4==0 && year%100!=0) || (year%400==0)){
                if(day == 28){
                    end = year+ "-"+ month +"-" + (day + 1) +" 00:00:00";
                }
                if(day == 29){
                    end = year+ "-"+ (month+1) +"-01 00:00:00";
                }
            }else
                {
                    if(day == 28){
                        end = year+ "-"+ (month+1) +"-01 00:00:00";
                    }
                }
        }

        try{
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
        }catch (ParseException e){
            e.getMessage();
        }

        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        readerList = query.getResultList();
        double totalCount = 0.0;
        for(int j = 0; j < readerList.size() ; j++){
            reader = readerList.get(j);
            totalCount = totalCount + reader.getDeposit();
        }
        model.addAttribute("totalDeposit",totalCount);
        model.addAttribute("readerList",readerList);




        query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
        List<Borrow> borrowList = new ArrayList<>();
        Borrow borrow;
//                = dateFormat.parse("2010-09-13 22:36:01");

        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        borrowList = query.getResultList();
        totalCount = 0.0;
        for(int j = 0; j< borrowList.size();j++){
            borrow = borrowList.get(j);
            totalCount = totalCount + borrow.getFine();
        }
        model.addAttribute("totalFine",totalCount);
        model.addAttribute("borrowList",borrowList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("borrowDate",year+"-"+month+"-"+day);
        return "librarian/showIncome";


    }

    @GetMapping(value = "/queryWeekIncome/{year}/{month}/{day}")
    public String queryWeek(@PathVariable("year") int year,
                            @PathVariable("month") int month,
                            @PathVariable("day") int day,
                            HttpServletRequest request,
                            Model model){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = year+ "-"+month+"-"+ day + " 00:00:00";
        Date currentDate = null;
        Date startDate = null;
        Date endDate = null;
        try{
            currentDate = dateFormat.parse(current);
        }catch (ParseException e){
            e.getMessage();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        String start = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 6);
        String end = dateFormat.format(calendar.getTime());

        try{
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
        }catch (ParseException e){
            e.getMessage();
        }


        List<Reader> readerList = new ArrayList<>();
        Reader reader;
        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        readerList = query.getResultList();
        double totalCount = 0.0;
        for(int j = 0; j < readerList.size() ; j++){
            reader = readerList.get(j);
            totalCount = totalCount + reader.getDeposit();
        }
        model.addAttribute("totalDeposit",totalCount);
        model.addAttribute("readerList",readerList);


        query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
        List<Borrow> borrowList = new ArrayList<>();
        Borrow borrow;
//                = dateFormat.parse("2010-09-13 22:36:01");

        query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
        query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
        borrowList = query.getResultList();
        totalCount = 0.0;
        for(int j = 0; j< borrowList.size();j++){
            borrow = borrowList.get(j);
            totalCount = totalCount + borrow.getFine();
        }
        model.addAttribute("totalFine",totalCount);
        model.addAttribute("borrowList",borrowList);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("borrowDate",year+"-"+month+"-"+day);
        return "librarian/showIncome";
    }


    @GetMapping(value = "/returnFineIndex")
    public String returnFineIndex(Model model,
                                  HttpServletRequest request){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/payFineIndex";
    }

    @GetMapping(value = "/payFine/{readerName}")
    public String payFine(@PathVariable("readerName") String readerName,
                          HttpServletRequest request,
                          Model model){
        System.out.println(readerName);
        double fineCurrently = 0.0;
        double fine = 0.0;

        int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
        double finePerDay = AdminRepository.findByAdminName("admin").getFineValue();

        List<Borrow> borrowListTemp = BorrowRepository.findByReaderName(readerName);
        Borrow borrow;
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

        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        List<Borrow> borrowFine = new ArrayList<>();
        List<Borrow> returnFine = new ArrayList<>();
        for(int i = 0; i < borrowList.size();i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1){
                if(borrow.getFineStatus() == 1){
                    borrowFine.add(borrow);
                }
            }
            if(borrow.getStatus() == 0){
                if(borrow.getFineStatus() == 1){
                    fineCurrently = fineCurrently + borrow.getFine();
                    returnFine.add(borrow);
                }
            }
        }
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("readerName",readerName);
        model.addAttribute("fineCurrently",fineCurrently);
        model.addAttribute("borrowFineList",borrowFine);
        model.addAttribute("returnFineList",returnFine);
        return "librarian/payFine";
    }

    @PostMapping(value = "/pay")
    @ResponseBody
    public String pay(@RequestParam(value = "readerName")String readerName){
        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        Borrow borrow;
        for(int i = 0;i< borrowList.size(); i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 0){
                if(borrow.getFineStatus() == 1){
                    borrow.setFineStatus(0);
                    borrow.setFineDate(new Date());
                    BorrowRepository.save(borrow);
                }
            }
        }
        return "Pay successfully";
    }

    @GetMapping(value = "/showFine/{readerName}")
    public String showFine(@PathVariable("readerName") String readerName,
                           HttpServletRequest request,
                           Model model){
        double fineHavePaid = 0.0;
        double fineCurrently = 0.0;
        double fine = 0.0;

        int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
        double finePerDay = AdminRepository.findByAdminName("admin").getFineValue();

        List<Borrow> borrowListTemp = BorrowRepository.findByReaderName(readerName);
        Borrow borrow;
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

        List<Borrow> borrowList = BorrowRepository.findByReaderName(readerName);
        List<Borrow> borrowFine = new ArrayList<>();
        List<Borrow> returnFine = new ArrayList<>();
        List<Borrow> fineHistory = new ArrayList<>();
        for(int i = 0; i < borrowList.size();i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1){
                if(borrow.getFineStatus() == 1){
                    fineCurrently = fineCurrently + borrow.getFine();
                    borrowFine.add(borrow);
                }
            }
            if(borrow.getStatus() == 0){
                if(borrow.getFineStatus() == 1){
                    fineCurrently = fineCurrently + borrow.getFine();
                    returnFine.add(borrow);
                }
            }
            if(borrow.getFineStatus() == 0){
                if(borrow.getFine() > 0){
                    fineHavePaid = fineHavePaid + borrow.getFine();
                    fineHistory.add(borrow);
                }

            }
        }
        System.out.println(borrowFine);
        System.out.println(returnFine);
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        model.addAttribute("fineCurrently",fineCurrently);
        model.addAttribute("fineHavePaid",fineHavePaid);
        model.addAttribute("borrowFineList",borrowFine);
        model.addAttribute("returnFineList",returnFine);
        model.addAttribute("fineHistory",fineHistory);
        return "librarian/showReaderFine";
    }
}
