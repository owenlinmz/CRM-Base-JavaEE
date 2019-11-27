package com.owen.entity;

public class Room {

    private int id;

    private String roomNumber;

    private String type;

    private String status;

    private int floor;

    private int bed;

    private int price;

    public Room(int id, String roomNumber, String type, String status, int floor, int bed, int price) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = status;
        this.floor = floor;
        this.bed = bed;
        this.price = price;
    }

    public Room() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"roomNumber\":" + "\"" + roomNumber + "\"" +
                ", \"type\":" + "\"" + type + '\"' +
                ", \"status\":" + "\"" + status + '\"' +
                ", \"floor\":" + floor +
                ", \"bed\":" + bed +
                ", \"price\":" + price +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
