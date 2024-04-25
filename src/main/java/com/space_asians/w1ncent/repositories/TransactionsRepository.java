package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TransactionsRepository extends CrudRepository<Transaction, Integer> {
     // @Query("SELECT * FROM TRANSACTIONS WHERE WHO=\"Firuz\"")
     // public List<String> findAllByMember();

    public Transaction findTopByOrderByIdDesc();
}
