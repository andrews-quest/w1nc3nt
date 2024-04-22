package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembersRepository extends CrudRepository<Member, Integer> {

    // @Query("SELECT * ")
    // public List<String> findBalance;

    @Query(value = "SELECT balance FROM members WHERE name = ?1", nativeQuery = true)
    public String findBalanceByName(String name);

    // @Modifying
    // @Query()
    // public updateBalance(String name);
}
