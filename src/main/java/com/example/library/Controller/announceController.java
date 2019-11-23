package com.example.library.Controller;

import com.example.library.Entity.Announce;
import com.example.library.Repository.announceRepository;
import com.example.library.Util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class announceController {

    @Autowired
    announceRepository AnnounceRepository;


    @RequestMapping(value = "/addAnnounce", method = RequestMethod.POST)
    public String addAnnounce(@RequestParam("announceTitle")String announceTitle,
                              @RequestParam("announceText")String announceText,
                              @RequestParam("announceImageFile") MultipartFile announceImageFile){
        Announce announce = new Announce();
        System.out.println(announceTitle);
        announce.setAnnounceTitle(announceTitle);
        announce.setAnnounceText(announceText);
        Date date = new Date();
        announce.setAnnounceDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String dateNowStr = sdf.format(date);
        System.out.println("格式化后的日期：" + dateNowStr);
        announce.setAnnounceStatus(1);
        if (!announceImageFile.isEmpty()) {
            try {

                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(".\\src\\main\\webapp\\announce\\"+announceTitle+ '_'+dateNowStr+".jpg")));//保存图片到目录下
                out.write(announceImageFile.getBytes());
                out.flush();
                out.close();
                String announceImage="/announce/"+announceTitle+ '_' + dateNowStr +".jpg";
                announce.setAnnounceImage(announceImage);
                AnnounceRepository.save(announce);//增加公告
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
            return "redirect:/announcePage";
        } else {
            return "上传失败，因为文件是空的.";
        }

    }

    @RequestMapping(value = "/announcePage", method = RequestMethod.GET)
    public String announcePage(Model model,
                               HttpServletRequest request){
        List<Announce> allAnnounce = AnnounceRepository.findByAnnounceStatus(1);

        int n = allAnnounce.size();
        Integer[] idList = new Integer[n];
        for(int i = 0; i < n; i++)
            idList[i] = allAnnounce.get(i).getAnnounceId();
        for(int i = 0; i < n-1; i++) {
            for(int j = i + 1; j < n ; j++) {
                if(idList[j]>idList[i]) {
                    int temp = idList[i];
                    idList[i] = idList[j];
                    idList[j] = temp;
                }
            }
        }
        List<Announce> announceList = new ArrayList<>();
        for(int i = 0; i < n; i++)
            announceList.add(AnnounceRepository.findById(idList[i]).orElse(null));
        model.addAttribute("announceList",announceList);

        model.addAttribute("librarianName", CookieUtils.getCookieValue(request,"librarianName"));
        return "announce/announcePage";
    }

    @RequestMapping(value = "/setTop",method = RequestMethod.PUT)
    @ResponseBody
    public void setTop(@RequestParam("announceId")Integer announceId){
        Announce announce = AnnounceRepository.findById(announceId).orElse(null);

        Announce newAnnounce = new Announce();
        newAnnounce.setAnnounceTitle(announce.getAnnounceTitle());
        newAnnounce.setAnnounceText(announce.getAnnounceText());
        newAnnounce.setAnnounceDate(announce.getAnnounceDate());
        newAnnounce.setAnnounceImage(announce.getAnnounceImage());
        newAnnounce.setAnnounceStatus(announce.getAnnounceStatus());

        AnnounceRepository.deleteById(announceId);
        AnnounceRepository.save(newAnnounce);

    }

    @RequestMapping(value = "/updataAnnounce",method = RequestMethod.PUT)
    @ResponseBody
    public void updataAnnounce(@RequestParam("announceId")Integer announceId,
                               @RequestParam("announceTitle")String announceTitle,
                               @RequestParam("announceText")String announceText){
        Announce announce = AnnounceRepository.findById(announceId).orElse(null);
        announce.setAnnounceTitle(announceTitle);
        announce.setAnnounceText(announceText);
        AnnounceRepository.save(announce);
    }

    @RequestMapping(value = "/deleteAnnounce",method = RequestMethod.PUT)
    @ResponseBody
    public void deleteAnnounce(@RequestParam("announceId")Integer announceId){
        Announce announce = AnnounceRepository.findById(announceId).orElse(null);
        announce.setAnnounceStatus(0);
        AnnounceRepository.save(announce);
    }

    @RequestMapping(value = "/indexGetAnnounce3",method = RequestMethod.GET)
    @ResponseBody
    public List<Announce> indexGetAnnounce3(){
        List<Announce> AnnounceList = AnnounceRepository.findByAnnounceStatus(1);
        int n = AnnounceList.size();
        Integer[] idList = new Integer[n];
        for(int i = 0; i < n; i++)
            idList[i] = AnnounceList.get(i).getAnnounceId();
        for(int i = 0; i < n-1; i++){
            for(int j = i + 1; j < n ; j++){
                if(idList[j]>idList[i]){
                    int temp = idList[i];
                    idList[i] = idList[j];
                    idList[j] = temp;
                }
            }
        }
        List<Announce> indexGetAnnounce3 = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            indexGetAnnounce3.add(AnnounceRepository.findById(idList[i]).orElse(null));
        return indexGetAnnounce3;
    }
}
