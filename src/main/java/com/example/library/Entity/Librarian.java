package com.example.library.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Librarian {
    @Id
    @GeneratedValue
    private Integer librarianId;
    @Column(unique = true,nullable = false)
    private String librarianName;
    private String librarianPassword;

    public Librarian() {
    }

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    public String getLibrarianPassword() {
        return librarianPassword;
    }

    public void setLibrarianPassword(String librarianPassword) {
        this.librarianPassword = librarianPassword;
    }
}
