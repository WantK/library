package com.example.library.Controller;

import com.example.library.Entity.Admin;
import com.example.library.Entity.Librarian;
import com.example.library.Repository.adminRepository;
import com.example.library.Repository.librarianRepository;
import com.example.library.Service.adminService;
import com.example.library.Service.libService;
import com.example.library.Service.readerService;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class adminController {

    @Autowired
    private adminRepository AdminRepository;

    @Autowired
    private librarianRepository LibrarianRepository;

    @Autowired
    private adminService AdminService;
    @Autowired
    private libService LibrarianService;
    @Autowired
    private readerService ReaderService;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String firstLogin(Model model){
        model.addAttribute("information","Please login");
        return "login";
    }

    @GetMapping(value = "/admin")
    public String adminLoginFirst(Model model){
        model.addAttribute("information","Please login");
        return "admin/login";
    }

    @PostMapping(value = "/tryToLogin")
    public String adminLoginSucceed(@RequestParam(value = "adminName") String UserName,
                                    @RequestParam(value = "Password") String Password,
                                    HttpServletResponse response,
                                    Model model){
        int i = AdminService.adminCheck(UserName,Password);
        if(i == 2){
            CookieUtils.addCookie(response,"adminName",UserName);
            return "redirect:/adminLogin";
        }else{
            model.addAttribute("information","Login failed.Please try again.");
            return "admin/login";
        }
    }



    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public String Login(@RequestParam(value = "UserName") String UserName,
                        @RequestParam(value = "Password") String Password,
                        HttpServletResponse response,
                        Model model){
        int m = LibrarianService.libCheck(UserName,Password);
        int k = ReaderService.readerCheck(UserName,Password);
        if(m == 2){
            CookieUtils.addCookie(response,"librarianName",UserName);
            return "redirect:/librarianLogin";
        }
        else if(k == 2){
            CookieUtils.addCookie(response,"readerName",UserName);
            return "redirect:/readerLogin";
        }else{
            model.addAttribute("information","Login failed.Please try again.");
            return "login";
        }
    }

    @GetMapping(value = "/adminLogin")
    public String adminLogin(HttpServletRequest request,Model model){
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "admin/loginSucceed";
    }
    @GetMapping(value = "/librarianLogin")
    public String librarianLogin(HttpServletRequest request,Model model){
        model.addAttribute("librarianName",CookieUtils.getCookieValue(request,"librarianName"));
        return "librarian/loginSucceed";
    }
    @GetMapping(value = "/readerLogin")
    public String readerLogin(HttpServletRequest request,Model model){
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        return "reader/loginIndex";
    }


    @RequestMapping(value = "/registerLibrarian",method = RequestMethod.GET)
    public String register(HttpServletRequest request,Model model) {
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "admin/registerLib";
    }

    @RequestMapping(value = "/changeAdminPasswordPage", method = RequestMethod.GET)
    public String  changeAdminPasswordPage(HttpServletRequest request,Model model){
        Admin admin  = AdminRepository.findAll().get(0);
        model.addAttribute("adminPassword",admin.getAdminPassword());
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "admin/changeAdminPassword";
    }


    @RequestMapping(value = "/changeAdminPassword", method = RequestMethod.PUT)
    @ResponseBody
    public String changeAdminPassword(@Valid String adminPassword){
        Admin admin = AdminRepository.findAll().get(0);
        admin.setAdminPassword(adminPassword);
        AdminRepository.save(admin);
        return "Update successfully";
    }


    @GetMapping(value = "/setFineIndex")
    public String setFineIndex(HttpServletRequest request,Model model){
        model.addAttribute("fineValue",AdminRepository.findByAdminName("admin").getFineValue());
        model.addAttribute("returnPeriod",AdminRepository.findByAdminName("admin").getReturnPeriod());
        model.addAttribute("deposit",AdminRepository.findByAdminName("admin").getDeposit());
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "admin/setFine";
    }


    @RequestMapping(value = "/setFine",method = RequestMethod.POST)
    @ResponseBody
    public String setFine(@RequestParam(value = "fineValue") Double fineValue){
        Admin admin = AdminRepository.findByAdminName("admin");
        admin.setFineValue(fineValue);
        AdminRepository.save(admin);
        return "Update successfully";
    }

    @GetMapping(value = "/setPeriodIndex")
    public String setPeriodIndex(Model model){
        model.addAttribute("returnPeriod",AdminRepository.findByAdminName("admin").getReturnPeriod());
        return "admin/setPeriodIndex";
    }

    @RequestMapping(value = "/setPeriod",method = RequestMethod.POST)
    @ResponseBody
    public String setFine(@RequestParam(value = "returnPeriod") Integer returnPeriod){
        System.out.println(returnPeriod);
        Admin admin = AdminRepository.findByAdminName("admin");
        admin.setReturnPeriod(returnPeriod);
        AdminRepository.save(admin);
        return "Update successfully";
    }

    @GetMapping(value = "/setDepositIndex")
    public String setDepositIndex(Model model){
        model.addAttribute("deposit",AdminRepository.findByAdminName("admin").getDeposit());
        return "admin/setDepositIndex";
    }

    @RequestMapping(value = "/setDeposit",method = RequestMethod.POST)
    @ResponseBody
    public String setDeposit(@RequestParam(value = "deposit") Double deposit){
        Admin admin = AdminRepository.findByAdminName("admin");
        admin.setDeposit(deposit);
        AdminRepository.save(admin);
        return "Update successfully";
    }


    @GetMapping(value = "/searchLibrarian/keywords={keywords}")
    public String searchLibrarian(@PathVariable("keywords") String keywords,
                                  HttpServletRequest request,
                                  Model model){
        List<Librarian> allLibrarianList = LibrarianRepository.findAll();
        List<Librarian> librarianList = new ArrayList<>();
        Librarian librarian;
        Pattern complie = Pattern.compile(keywords);
        for(int i = 0; i < allLibrarianList.size(); i++ ){
            librarian = allLibrarianList.get(i);
            if(complie.matcher(librarian.getLibrarianId() + " " + librarian.getLibrarianName()).find()){
                librarianList.add(librarian);
            }
        }
        model.addAttribute("librarianList",librarianList);
        model.addAttribute("adminName",CookieUtils.getCookieValue(request,"adminName"));
        return "admin/searchLibrarianResult";
    }



}
