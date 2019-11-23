package com.example.library.Controller;

import com.example.library.Entity.Borrow;
import com.example.library.Entity.Reader;
import com.example.library.Entity.Remind;
import com.example.library.Repository.adminRepository;
import com.example.library.Repository.borrowRepository;
import com.example.library.Repository.readerRepository;
import com.example.library.Repository.remindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class remindController {
    @Autowired
    private borrowRepository BorrowRepository;

    @Autowired
    private adminRepository AdminRepository;

    @Autowired
    private readerRepository ReaderRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private remindRepository RemindRepository;

    @RequestMapping(value = "/Remind", method = RequestMethod.POST)
    @ResponseBody
    public void Remind(){

        Remind remind = RemindRepository.findAll().get(0);
        double time = (new Date().getTime() - remind.getDate().getTime())/(1000*60*60);
        if(time<23)
            return;

        List<Borrow> borrowList = BorrowRepository.findAll();
        //用set存使得一个name只需记住一次
        Set<Reader> readerList = new HashSet<>();

        int period = AdminRepository.findByAdminName("admin").getReturnPeriod();
        Borrow borrow;
        long j;
        double k;
        for(int i = 0;i < borrowList.size();i++){
            borrow = borrowList.get(i);
            if(borrow.getStatus() == 1){
                j = new Date().getTime() - borrow.getDate().getTime();
                k = period - j/(1000*60*60*24);

                //找出离到期时间还有两天的readerName
                if(k < 0 && k > 2){
                    continue;
                }else{
                    readerList.add(ReaderRepository.findByReaderName(borrow.getReaderName()));
                }
            }
        }

        for(Reader reader : readerList){
            if(reader != null){
                //建立邮件消息
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                //发送者
                simpleMailMessage.setFrom("Jinbin_z@163.com");
                //接收者
                simpleMailMessage.setTo(reader.getEmail());
                //发送的标题
                simpleMailMessage.setSubject("Bibliosoft remind.");
                //发送的内容
                simpleMailMessage.setText("Dear "+reader.getReaderName()+":\n      Some books you borrowed in Bibliosoft are about to expire.");
                //发送
                javaMailSender.send(simpleMailMessage);
            }
        }
        remind.setDate(new Date());
        RemindRepository.save(remind);
    }
}
