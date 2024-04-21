package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersRepository extends CrudRepository<Member, Integer> {
}
