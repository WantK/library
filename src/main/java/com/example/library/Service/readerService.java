package com.example.library.Service;

import com.example.library.Entity.Reader;
import com.example.library.Repository.readerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class readerService {
    @Autowired
    private readerRepository ReaderRepository;

    //temp==2，则登陆成功
    public Integer readerCheck(String userName,String userPassword){
        int temp = 0;
        List<Reader> allReader = ReaderRepository.findAll();
        Reader reader = null;
        for (int i = 0; i < allReader.size(); i++){
            reader = allReader.get(i);
            if(userName.equals(reader.getReaderName()) && userPassword.equals(reader.getReaderPassword())){
                temp = 2;
                break;
            }
        }
        return temp;
    }

    public Integer readerCheck(String userName){
        int temp = 0;
        List<Reader> allReader = ReaderRepository.findAll();
        Reader reader;
        for (int i = 0; i < allReader.size(); i++){
            reader = allReader.get(i);
            if(userName.equals(reader.getReaderName())){
                temp = 1;
                break;
            }
        }
        return temp;
    }
}
