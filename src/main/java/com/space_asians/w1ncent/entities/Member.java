package com.space_asians.w1ncent.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @Column
    int balance;
}