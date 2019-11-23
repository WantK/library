package com.example.library.Entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location {

    @Id
    @GeneratedValue
    private Integer locationId;

    private String locationFloor;
    private String locationClass;
    private String locationShelf;

    public Location() {
    }


    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationFloor() {
        return locationFloor;
    }

    public void setLocationFloor(String locationFloor) {
        this.locationFloor = locationFloor;
    }

    public String getLocationClass() {
        return locationClass;
    }

    public void setLocationClass(String locationClass) {
        this.locationClass = locationClass;
    }

    public String getLocationShelf() {
        return locationShelf;
    }

    public void setLocationShelf(String locationShelf) {
        this.locationShelf = locationShelf;
    }
}
