package com.lightboard.board.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Board {
    @Id // primary key를 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY) // strategy를 GenerationType.IDENTITY로 해준다(MySQL, MariaDB / SEQUENCE는 오라클 용 / AUTO는 자동지정)
    private Integer id;

    private String title;

    private String content;

    private String filename;

    private String filepath;
}