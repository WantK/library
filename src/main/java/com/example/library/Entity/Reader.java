package com.example.library.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Reader {

    @Id
    @GeneratedValue
    private Integer readerId;
    @Column(unique = true,nullable = false)
    private String readerName;

    private String readerPassword;

    private String email;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Double deposit;

    public Reader() {
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getReaderId() {
        return readerId;
    }

    public void setReaderId(Integer readerId) {
        this.readerId = readerId;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getReaderPassword() {
        return readerPassword;
    }

    public void setReaderPassword(String readerPassword) {
        this.readerPassword = readerPassword;
    }
}
