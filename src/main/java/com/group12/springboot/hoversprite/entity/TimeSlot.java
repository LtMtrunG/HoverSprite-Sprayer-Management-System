//package com.group12.springboot.hoversprite.entity;
//
//import java.time.LocalTime;
//
//public class TimeSlot {
//    private LocalTime startTime;
//    private LocalTime endTime;
//    private int maxSessions = 2;
//    private int bookedSessions = 0;
//
//    public TimeSlot(LocalTime startTime, LocalTime endTime) {
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }
//
//    public LocalTime getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(LocalTime startTime) {
//        this.startTime = startTime;
//    }
//
//    public LocalTime getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(LocalTime endTime) {
//        this.endTime = endTime;
//    }
//
//    public int getMaxSessions() {
//        return maxSessions;
//    }
//
//    public void setMaxSessions(int maxSessions) {
//        this.maxSessions = maxSessions;
//    }
//
//    public int getBookedSessions() {
//        return bookedSessions;
//    }
//
//    public void setBookedSessions(int bookedSessions) {
//        this.bookedSessions = bookedSessions;
//    }
//
//    public boolean isAvailable() {
//        return bookedSessions < maxSessions;
//    }
//
//    public void bookSession() {
//        if (isAvailable()) {
//            bookedSessions++;
//        } else {
//            throw new RuntimeException("TimeSlot fully booked");
//        }
//    }
//
//    public void cancelSession() {
//        if (bookedSessions > 0) {
//            bookedSessions--;
//        }
//    }
//}
