package com.space_asians.w1ncent.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
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
