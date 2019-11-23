package com.example.library.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    /*public static void main(String[] args) throws Exception{
        String a = sendGet("https://api.douban.com/v2/book/isbn/9787111563877");
        //System.out.println(a);

        HttpRequest.getTitle(a);

    }*/
    String isbn;
    String url;
    String result;
    public HttpRequest(String isbn) throws Exception {
        this.isbn = isbn;
        this.url = "https://api.douban.com/v2/book/isbn/"+ isbn ;
    }

    /*public static String getResult(String isbn) throws Exception {
        String a = sendGet("https://api.douban.com/v2/book/isbn/"+isbn);
        return HttpRequest.getTitle(a);
    }*/
    public void sendGet() throws Exception {

        String result = "";
        BufferedReader in = null;

        try {
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();

            String request = this.url ;

            //打开和URL之间的连接
            URLConnection connection = new URL(request).openConnection();

            /* begin获取响应码 */
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setConnectTimeout(300000);
            httpUrlConnection.setReadTimeout(300000);
            httpUrlConnection.connect();
            //获取响应码 =200
            int resCode = httpUrlConnection.getResponseCode();
            //获取响应消息 =OK
            String message = httpUrlConnection.getResponseMessage();

            System.out.println("getResponseCode resCode =" + resCode);
            System.out.println("getResponseMessage message =" + message);
            /* end获取响应码 */

            /* begin获取响应headers*/
            System.out.println("响应头：" + result);
            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                System.out.println(header.getKey() + "=" + header.getValue());
            }

            //方式一、定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result += "\n" + inputLine;
            System.out.println("result===" + result);



        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }// 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.result = result;
    }

    public String getTitle() throws Exception {

        String r = this.result.replace("{","").replace("}","")
                .replace("[","").replace("]","");
        System.out.println("result="+ this.result);
        int from = r.lastIndexOf("\"isbn13\"");
        System.out.println(from);
        int end = r.indexOf(",",from);
        //System.out.println(end);
        from = end+1;
        end = r.indexOf(",",from);
        String subString = r.substring(from,end);
        //System.out.println(subString);
        String title = subString.replace("\"","").split(":")[1];
        //System.out.println(title);
        return title;
    }

    public String getImg() throws Exception{
        String r = this.result.replace("{","").replace("}","")
                .replace("[","").replace("]","");
        int from = r.indexOf("\"image\"");
        //System.out.println(from);
        int end = r.indexOf(",",from);
        String substring = r.substring(from,end);
        //System.out.println(substring);
        substring = substring.replace("\\","").replace("\"","");
        from = substring.indexOf(":");
        String imageUrl = substring.substring(from+1);
        //System.out.println(imageUrl);
        ImgGet.downloadImage(imageUrl,this.isbn);
        return "bookImg/"+this.isbn+".jpg";
    }

    public String getAuthor() throws Exception{
        String r = this.result.replace("{","").replace("}","")
                .replace("[","").replace("]","");
        int from = r.indexOf("\"author\"");
        int end = r.indexOf(",",from);
        String subString = r.substring(from,end);
        subString = subString.replace("[","").replace("]","").replace("\\","").replace("\"","");
        //System.out.println(subString);
        String author = subString.split(":")[1];
        //System.out.println(author);
        return author;
    }
    public String getPrice() throws Exception{
        String r = this.result.replace("{","").replace("}","")
                .replace("[","").replace("]","");
        int from = r.lastIndexOf("\"price\"");
        String subString = r.substring(from);
        System.out.println(subString);
        subString = subString.replace("\\","").replace("\"","");
        String price = subString.split(":")[1];
        System.out.println(price);
        return price;
    }

}
