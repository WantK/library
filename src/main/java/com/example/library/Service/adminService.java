package com.example.library.Service;

import com.example.library.Entity.Admin;
import com.example.library.Repository.adminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class adminService {

    @Autowired
    private adminRepository AdminRepository;

    public Integer adminCheck(String userName,String userPassword){
        int temp = 0;   //temp==2，则登陆成功
        List<Admin> allAdmin = AdminRepository.findAll();
        Admin admin = null;
        for (int i = 0; i < allAdmin.size(); i++){
            admin = allAdmin.get(i);
            if(userName.equals(admin.getAdminName()) && userPassword.equals(admin.getAdminPassword())){
                temp = 2;
                break;
            }
        }
        return temp;
    }
}
