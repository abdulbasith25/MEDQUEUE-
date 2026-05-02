package com.appointment.entity;

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Doctor doctor;
    private User user;
    private Date createdDate;
    
}
