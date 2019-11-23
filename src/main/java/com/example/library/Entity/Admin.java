package com.example.library.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Admin {

    @Id
    @GeneratedValue
    private Integer id;

    private String adminName;
    private String adminPassword;
    private Double fineValue=1.0;
    private Integer returnPeriod=30;
    private Double deposit=300.0;

    public Admin() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Double getFineValue() {
        return fineValue;
    }

    public void setFineValue(Double fineValue) {
        this.fineValue = fineValue;
    }

    public Integer getReturnPeriod() {
        return returnPeriod;
    }

    public void setReturnPeriod(Integer returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
