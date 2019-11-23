package com.example.library.Service;

import com.example.library.Entity.Librarian;
import com.example.library.Repository.librarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class libService {
    @Autowired
    private librarianRepository LibrarianRepository;

    public Integer libCheck(String userName,String userPassword){
        int temp = 0;   //temp==0,则userName不存在；temp==1，则密码不匹配；temp==2，则登陆成功
        List<Librarian> allLib = LibrarianRepository.findAll();
        Librarian lib = null;
        for (int i = 0; i < allLib.size(); i++){
            lib = allLib.get(i);
            if(userName.equals(lib.getLibrarianName()) && userPassword.equals(lib.getLibrarianPassword())){
                temp = 2;
                break;
            }
        }
        return temp;
    }

    public Integer libCheck(String userName){
        int temp = 0;
        List<Librarian> allReader = LibrarianRepository.findAll();
        Librarian librarian;
        for (int i = 0; i < allReader.size(); i++){
            librarian = allReader.get(i);
            if(userName.equals(librarian.getLibrarianName())){
                temp = 1;
                break;
            }
        }
        return temp;
    }
}
