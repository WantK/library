package com.example.library.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity

public class DeleteBook {
    @Id
    @GeneratedValue
    private Integer defaultId;

    private Integer id;

    private String title;
    private String author;
    private String price;
    private String image;
    private String isbn;
    private Integer count;
    private String barcode;

    private String category;
    private String floor;
    private String room;
    private String shelf;
    private String librarianName;


    public DeleteBook() {
    }

//    public DeleteBook(Integer id, String title, String author, String price, String image, String isbn, Integer count, String barcode, String category, String floor, String room, String shelf, String librarianName) {
//        this.id = id;
//        this.title = title;
//        this.author = author;
//        this.price = price;
//        this.image = image;
//        this.isbn = isbn;
//        this.count = count;
//        this.barcode = barcode;
//        this.category = category;
//        this.floor = floor;
//        this.room = room;
//        this.shelf = shelf;
//        this.librarianName = librarianName;
//    }


    public Integer getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(Integer defaultId) {
        this.defaultId = defaultId;
    }

    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String  getPrice() {
        return price;
    }

    public void setPrice(String  price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }
}
