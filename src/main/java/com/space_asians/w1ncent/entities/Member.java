package com.space_asians.w1ncent.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private float balance;

    @Column
    private String password;

    @Column
    private String state;

    @Column
    private String state_manager;

    @Column
    private Long chat_id;

    public Member() {
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public float getBalance() {
        return this.balance;
    }

    public String getPassword() {
        return this.password;
    }

    public String getState() {
        return this.state;
    }

    public String getState_manager() {
        return this.state_manager;
    }

    public Long getChat_id() {
        return this.chat_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setState_manager(String state_manager) {
        this.state_manager = state_manager;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Member)) return false;
        final Member other = (Member) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        if (Float.compare(this.getBalance(), other.getBalance()) != 0) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$state = this.getState();
        final Object other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) return false;
        final Object this$state_manager = this.getState_manager();
        final Object other$state_manager = other.getState_manager();
        if (this$state_manager == null ? other$state_manager != null : !this$state_manager.equals(other$state_manager))
            return false;
        final Object this$chat_id = this.getChat_id();
        final Object other$chat_id = other.getChat_id();
        if (this$chat_id == null ? other$chat_id != null : !this$chat_id.equals(other$chat_id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Member;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        result = result * PRIME + Float.floatToIntBits(this.getBalance());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $state = this.getState();
        result = result * PRIME + ($state == null ? 43 : $state.hashCode());
        final Object $state_manager = this.getState_manager();
        result = result * PRIME + ($state_manager == null ? 43 : $state_manager.hashCode());
        final Object $chat_id = this.getChat_id();
        result = result * PRIME + ($chat_id == null ? 43 : $chat_id.hashCode());
        return result;
    }

    public String toString() {
        return "Member(id=" + this.getId() + ", name=" + this.getName() + ", balance=" + this.getBalance() + ", password=" + this.getPassword() + ", state=" + this.getState() + ", state_manager=" + this.getState_manager() + ", chat_id=" + this.getChat_id() + ")";
    }
}
