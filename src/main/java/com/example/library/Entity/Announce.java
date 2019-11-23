package com.example.library.Entity;


import javax.persistence.*;
import java.util.Date;

@Entity
public class Announce {
    @Id
    @GeneratedValue
    private Integer announceId;

    private String announceTitle;

    private String announceText;

    @Temporal(TemporalType.TIMESTAMP)
    private Date announceDate;

    private String announceImage;

    private Integer announceStatus;
    public Announce() {

    }

    public Integer getAnnounceId() {
        return announceId;
    }

    public String getAnnounceText() {
        return announceText;
    }

    public String getAnnounceTitle() {
        return announceTitle;
    }

    public void setAnnounceTitle(String announceTitle) {
        this.announceTitle = announceTitle;
    }

    public void setAnnounceText(String announceText) {
        this.announceText = announceText;
    }

    public Date getAnnounceDate() {
        return announceDate;
    }

    public void setAnnounceDate(Date announceDate) {
        this.announceDate = announceDate;
    }

    public String getAnnounceImage() {
        return announceImage;
    }

    public void setAnnounceImage(String announceImage) {
        this.announceImage = announceImage;
    }

    public Integer getAnnounceStatus() {
        return announceStatus;
    }

    public void setAnnounceStatus(Integer announceStatus) {
        this.announceStatus = announceStatus;
    }
}
