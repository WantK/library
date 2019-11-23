package com.example.library.Controller;

import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class indexController {
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String mainPage() {
        return "index";
    }

    @RequestMapping(value = "/loginIndex", method = RequestMethod.GET)
    public String loginIndex(HttpServletRequest request, Model model) {
        model.addAttribute("readerName",CookieUtils.getCookieValue(request,"readerName"));
        return "reader/loginIndex";
    }

    @RequestMapping(value = "/try",method = RequestMethod.GET)
    public String tryfirst(){
        return "admin/try";
    }


}


