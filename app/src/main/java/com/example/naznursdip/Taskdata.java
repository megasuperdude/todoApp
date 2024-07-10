package com.example.naznursdip;

public class Taskdata {
    public String taskText, startDate, endDate, taskPry, taskStatus;//status: ar - архив / cr - текущи

    public Taskdata(String taskText, String startDate, String endDate, String taskPry, String taskStatus) {
        this.taskText = taskText;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskPry = taskPry;
        this.taskStatus = taskStatus;
    }

    public Taskdata(){

    }
}
