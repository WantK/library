package com.example.library.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Borrow {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer status;
    private Integer bookId;
    private String isbn;
    private String title;
    private Integer readerId;
    private String readerName;
    private Double fine;
    private String image;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fineDate;

    private Integer fineStatus;

    public Borrow() {
    }

    public Date getFineDate() {
        return fineDate;
    }

    public void setFineDate(Date fineDate) {
        this.fineDate = fineDate;
    }

    public Integer getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(Integer fineStatus) {
        this.fineStatus = fineStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getReaderId() {
        return readerId;
    }

    public void setReaderId(Integer readerId) {
        this.readerId = readerId;
    }

    public Double getFine() {
        return fine;
    }

    public void setFine(Double fine) {
        this.fine = fine;
    }
}
