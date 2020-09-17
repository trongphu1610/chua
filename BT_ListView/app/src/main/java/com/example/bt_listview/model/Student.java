package com.example.bt_listview.model;

public class Student {
    private int ID ,score;
    private String name , monhoc;

    public Student(int ID, int score, String name, String monhoc) {
        this.ID = ID;
        this.score = score;
        this.name = name;
        this.monhoc = monhoc;
    }

    public Student() {
    }

    public Student(int id, String name, String subject, int parseInt) {

    }

    @Override
    public String toString() {
        return "Student{" +
                "ID=" + ID +
                ", score=" + score +
                ", name='" + name + '\'' +
                ", monhoc='" + monhoc + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonhoc() {
        return monhoc;
    }

    public void setMonhoc(String monhoc) {
        this.monhoc = monhoc;
    }
}
