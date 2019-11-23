package com.example.library.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ImgGet {
    /*public static void main(String[] args) throws IOException {
        ImgGet.downloadImage("https://img3.doubanio.com/view/subject/m/public/s29476275.jpg");
    }*/

    public static void downloadImage(String Imageurl,String isbn) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        System.getProperties().setProperty("http.proxyHost", "IP");//设置代理  
        System.getProperties().setProperty("http.proxyPort", "Port");
        URL url = new URL(Imageurl);//打开网络输入流
        DataInputStream dis = new DataInputStream(url.openStream());
        String newImageName = ".\\bookImg\\"+isbn+"'.jpg";
        //建立一个新的文件  
        FileOutputStream fos = new FileOutputStream(new File(newImageName));
        byte[] buffer = new byte[1024];
        int length;
        //开始填充数据  
        while ((length = dis.read(buffer))>0){
            fos.write(buffer, 0, length);
        }
        dis.close();
        fos.close();
        resizeImage(".\\bookImg\\"+isbn+"'.jpg",
                ".\\bookImg\\"+isbn+".jpg",
                150,200);
    }

    public static void resizeImage(String srcImgPath, String distImgPath,
                                   int width, int height) throws IOException {

        File srcFile = new File(srcImgPath);
        Image srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg = null;
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
                0, null);

        ImageIO.write(buffImg, "JPEG", new File(distImgPath));
    }
}

