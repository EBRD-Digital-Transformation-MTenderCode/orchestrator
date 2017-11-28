package com.procurement.orchestrator.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "message")
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Message() {
    }

    public Message(String text, Date date) {
        this.text = text;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
