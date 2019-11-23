package com.example.library.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CookieUtils {


    public static void addCookie(HttpServletResponse response, String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");// 这个要设置
        cookie.setMaxAge(60*60*24*30);//保留一个月 以秒为单位
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String key){
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(key)) {
                    Cookie cookie = new Cookie(key,null);
                    cookie.setPath("/");//设置成跟写入cookies一样的
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }


    public static String getCookieValue(HttpServletRequest request, String key){
        try{
            for(Cookie cookie : request.getCookies()){
                if (cookie.getName().equals(key)) {
                    return URLDecoder.decode(cookie.getValue(), "UTF-8");
                }
            }
            return null;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static void showCookieValue(HttpServletRequest request){
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                System.out.print(cookies[i].getName());
                System.out.println(cookies[i].getValue());
            }
        }
    }
}