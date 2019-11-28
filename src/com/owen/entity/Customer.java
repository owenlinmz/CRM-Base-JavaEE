package com.owen.entity;

public class Customer {

    private int id;
    private String name;
    private String identity;
    private String telephone;
    private int roomId;
    private String roomNumber;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":" + '\"' + name + '\"' +
                ", \"identity\":" + '\"' + identity + '\"' +
                ", \"telephone\":" + '\"' + telephone + '\"' +
                ", \"roomId\":" + roomId +
                '}';
    }

    public Customer(int id, String name, String identity, String telephone, int roomId, String roomNumber) {
        this.id = id;
        this.name = name;
        this.identity = identity;
        this.telephone = telephone;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
    }

    public Customer(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
