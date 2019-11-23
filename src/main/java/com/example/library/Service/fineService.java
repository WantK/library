package com.example.library.Service;

import com.example.library.Entity.Reader;
import com.example.library.Repository.adminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.library.Entity.Borrow;

@Service
public class fineService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private adminRepository AdminRepository;

//    public Map<Integer,Double> totalFineMonth() throws ParseException {
//        Calendar cal = Calendar.getInstance();
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
//        Map<Integer,Double> map = new HashMap<>();
//
//        Query query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startDate = null;
//        Date endDate = null;
//        List<Borrow> borrowList = new ArrayList<>();
//        Borrow borrow;
////                = dateFormat.parse("2010-09-13 22:36:01");
//        for(int i = month;i > 0; i--){
//            String start = year+ "-"+i+"-00 00:00:00";
//            String end = year+ "-"+ (i+1) +"-00 00:00:00";
//            startDate = dateFormat.parse(start);
//            endDate = dateFormat.parse(end);
//            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
//            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
//            borrowList = query.getResultList();
//            double totalCount = 0.0;
//            for(int j = 0; j< borrowList.size();j++){
//                borrow = borrowList.get(j);
//                totalCount = totalCount + borrow.getFine();
//            }
//            map.put(i,totalCount);
//        }
//        return map;
//    }
//
//
//    public Map<Integer,Double> totalDepositMonth(int year,int month) throws ParseException {
////        Calendar cal = Calendar.getInstance();
////        int month = cal.get(Calendar.MONTH) + 1;
////        int year = cal.get(Calendar.YEAR);
//        Map<Integer,Double> map = new HashMap<>();
//        double deposit = AdminRepository.findByAdminName("admin").getDeposit();
//        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startDate = null;
//        Date endDate = null;
//        List<Reader> readerList = new ArrayList<>();
//        Reader reader;
////                = dateFormat.parse("2010-09-13 22:36:01");
//        for(int i = month;i > 0; i--){
//            String start = year+ "-"+i+"-00 00:00:00";
//            String end = year+ "-"+ (i+1) +"-00 00:00:00";
//            startDate = dateFormat.parse(start);
//            endDate = dateFormat.parse(end);
//            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
//            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
//            readerList = query.getResultList();
//            double totalCount = 0.0;
//            totalCount = deposit*(readerList.size());
//            map.put(i,totalCount);
//        }
//        return map;
//    }

    public Map<Integer,Double> totalFineDay() throws ParseException {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        Map<Integer,Double> map = new HashMap<>();

        Query query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        List<Borrow> borrowList = new ArrayList<>();
        Borrow borrow;
//                = dateFormat.parse("2010-09-13 22:36:01");
        for(int i = day;i > 0; i--){
            String start = year+ "-"+month+"-"+ i + " 00:00:00";
            String end = year+ "-"+ month +"-" + (i + 1) +" 00:00:00";
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
            borrowList = query.getResultList();
            double totalCount = 0.0;
            for(int j = 0; j< borrowList.size();j++){
                borrow = borrowList.get(j);
                totalCount = totalCount + borrow.getFine();
            }
            map.put(i,totalCount);
        }
        return map;
    }


    public Map<Integer,Double> totalDepositDay(int year,int month,int day) throws ParseException {
//        Calendar cal = Calendar.getInstance();
//        int day = cal.get(Calendar.DATE);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
        List<Integer> bigMonth = new ArrayList<>(Arrays.asList(1,3,5,7,8,10));
        List<Integer> smallMonth = new ArrayList<>(Arrays.asList(4,6,9,11));
//        int[] bigMonth = {1,3,5,7,8,10};
//        int[] smallMonth = {4,6,9,11};
        Map<Integer,Double> map = new HashMap<>();
        double deposit = AdminRepository.findByAdminName("admin").getDeposit();
        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;
        List<Reader> readerList = new ArrayList<>();
        Reader reader;
//                = dateFormat.parse("2010-09-13 22:36:01");
        for(int i = day;i > 0; i--){
            String start = year+ "-"+month+"-"+ i + " 00:00:00";
            String end = year+ "-"+ month +"-" + (i + 1) +" 00:00:00";
            if(bigMonth.contains(month)){
                if(i == 31){
                    end = year+ "-"+ (month+1) +"-01 00:00:00";
                }
            }else if(smallMonth.contains(month)) {
                if(i == 30){
                    end = year+ "-"+ (month+1) +"-01 00:00:00";
                }
            }else if(month == 12){
                if(i == 31){
                    end = (year+1)+ "-01-01 00:00:00";
                }
            }else{
                if((year%4==0 && year%100!=0) || (year%400==0)){
                    if(i == 28){
                        end = year+ "-"+ month +"-" + (i + 1) +" 00:00:00";
                    }
                    if(i == 29){
                        end = year+ "-"+ (month+1) +"-01 00:00:00";
                    }
                }else
                {
                    if(i == 28){
                        end = year+ "-"+ (month+1) +"-01 00:00:00";
                    }
                }
            }

            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
            readerList = query.getResultList();
            double totalCount = 0.0;
            totalCount = deposit*(readerList.size());
            map.put(i,totalCount);
        }
        return map;
    }
//
//    public Map<Integer,Double> totalFineWeek() throws ParseException {
//        Calendar cal = Calendar.getInstance();
//        int day = cal.get(Calendar.DATE);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
//        Map<Integer,Double> map = new HashMap<>();
//
//        Query query = em.createQuery("select e from Borrow e WHERE e.fineDate BETWEEN :startDate AND :endDate");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startDate = null;
//        Date endDate = null;
//        List<Borrow> borrowList = new ArrayList<>();
//        Borrow borrow;
//        int week = 1;
////                = dateFormat.parse("2010-09-13 22:36:01");
//        for(int i = 0;i < day; i = i + 7){
//            String start = year+ "-"+month+"-"+ i + " 00:00:00";
//            String end = year+ "-"+ month +"-" + (i + 7) +" 00:00:00";
//            startDate = dateFormat.parse(start);
//            endDate = dateFormat.parse(end);
//            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
//            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
//            borrowList = query.getResultList();
//            double totalCount = 0.0;
//            for(int j = 0; j< borrowList.size();j++){
//                borrow = borrowList.get(j);
//                totalCount = totalCount + borrow.getFine();
//            }
//            map.put(week,totalCount);
//            week = week + 1;
//        }
//        return map;
//    }
//
//    public Map<Integer,Double> totalDepositWeek() throws ParseException {
//        Calendar cal = Calendar.getInstance();
//        int day = cal.get(Calendar.DATE);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
//        Map<Integer,Double> map = new HashMap<>();
//        double deposit = AdminRepository.findByAdminName("admin").getDeposit();
//        Query query = em.createQuery("select e from Reader e WHERE e.date BETWEEN :startDate AND :endDate");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startDate = null;
//        Date endDate = null;
//        List<Reader> readerList = new ArrayList<>();
//        Reader reader;
//        int week = 1;
////                = dateFormat.parse("2010-09-13 22:36:01");
//        for(int i = 0;i < day; i = i + 7){
//            String start = year+ "-"+month+"-"+ i + " 00:00:00";
//            String end = year+ "-"+ month +"-" + (i + 7) +" 00:00:00";
//            startDate = dateFormat.parse(start);
//            endDate = dateFormat.parse(end);
//            query.setParameter("startDate",startDate,TemporalType.TIMESTAMP);
//            query.setParameter("endDate",endDate,TemporalType.TIMESTAMP);
//            readerList = query.getResultList();
//            double totalCount = 0.0;
//            totalCount = deposit*(readerList.size());
//            map.put(week,totalCount);
//            week = week + 1;
//        }
//        return map;
//    }

}
