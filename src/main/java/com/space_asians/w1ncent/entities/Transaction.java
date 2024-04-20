package com.space_asians.w1ncent.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "`when`")
    private String when;
    @Column
    private String who;
    @Column
    private String whom;
    @Column
    private float how_much;
    @Column
    private String for_what;
}
