package com.space_asians.w1ncent.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "`when`")
    private LocalDate when;
    @Column
    private String who;
    @Column
    private String whom;
    @Column
    private float how_much;
    @Column
    private String for_what;

    public Transaction() {
    }

    public int getId() {
        return this.id;
    }

    public LocalDate getWhen() {
        return this.when;
    }

    public String getWho() {
        return this.who;
    }

    public String getWhom() {
        return this.whom;
    }

    public float getHow_much() {
        return this.how_much;
    }

    public String getFor_what() {
        return this.for_what;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWhen(LocalDate when) {
        this.when = when;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setWhom(String whom) {
        this.whom = whom;
    }

    public void setHow_much(float how_much) {
        this.how_much = how_much;
    }

    public void setFor_what(String for_what) {
        this.for_what = for_what;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Transaction)) return false;
        final Transaction other = (Transaction) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$when = this.getWhen();
        final Object other$when = other.getWhen();
        if (this$when == null ? other$when != null : !this$when.equals(other$when)) return false;
        final Object this$who = this.getWho();
        final Object other$who = other.getWho();
        if (this$who == null ? other$who != null : !this$who.equals(other$who)) return false;
        final Object this$whom = this.getWhom();
        final Object other$whom = other.getWhom();
        if (this$whom == null ? other$whom != null : !this$whom.equals(other$whom)) return false;
        if (Float.compare(this.getHow_much(), other.getHow_much()) != 0) return false;
        final Object this$for_what = this.getFor_what();
        final Object other$for_what = other.getFor_what();
        if (this$for_what == null ? other$for_what != null : !this$for_what.equals(other$for_what)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Transaction;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $when = this.getWhen();
        result = result * PRIME + ($when == null ? 43 : $when.hashCode());
        final Object $who = this.getWho();
        result = result * PRIME + ($who == null ? 43 : $who.hashCode());
        final Object $whom = this.getWhom();
        result = result * PRIME + ($whom == null ? 43 : $whom.hashCode());
        result = result * PRIME + Float.floatToIntBits(this.getHow_much());
        final Object $for_what = this.getFor_what();
        result = result * PRIME + ($for_what == null ? 43 : $for_what.hashCode());
        return result;
    }

    public String toString() {
        return "Transaction(id=" + this.getId() + ", when=" + this.getWhen() + ", who=" + this.getWho() + ", whom=" + this.getWhom() + ", how_much=" + this.getHow_much() + ", for_what=" + this.getFor_what() + ")";
    }
}
