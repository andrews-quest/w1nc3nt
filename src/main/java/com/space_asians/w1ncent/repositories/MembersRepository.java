package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface MembersRepository extends CrudRepository<Member, Integer> {

    // @Query("SELECT * ")
    // public List<String> findBalance;

    @Query(value = "SELECT balance FROM members WHERE `name` = ?1", nativeQuery = true)
    public int findBalanceByName(String name);

    @Modifying
    @Query(value = "UPDATE Member m set m.balance=?2 where m.name=?1")
    public void updateBalance(String name, int balance);

}