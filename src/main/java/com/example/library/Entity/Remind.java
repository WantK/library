package com.example.library.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Remind {
    @Id
    @GeneratedValue
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Remind() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
