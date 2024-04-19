package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends CrudRepository<Transaction, Integer> {
}
