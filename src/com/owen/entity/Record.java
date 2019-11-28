package com.owen.entity;

import java.util.Date;

public class Record {
    private int id;
    private int customerId;
    private int roomId;
    private Date inTime;
    private Date outTime;
    private String breakfast;
    private int price;
    private String type;

    private String name; // 客户名称
    private String roomNumber;
    private String telephone;

    public Record(int id, Date inTime, Date outTime, String breakfast, int price, String type, String name, String roomNumber, String telephone) {
        this.id = id;
        this.inTime = inTime;
        this.outTime = outTime;
        this.breakfast = breakfast;
        this.price = price;
        this.type = type;
        this.name = name;
        this.roomNumber = roomNumber;
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", inTime=" + inTime +
                ", outTime=" + outTime +
                ", breakfast='" + breakfast + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
