package com.space_asians.w1ncent.repositories;

import com.space_asians.w1ncent.entities.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface MembersRepository extends CrudRepository<Member, Integer> {

    // @Query("SELECT * ")
    // public List<String> findBalance;

    @Query(value = "SELECT balance FROM members WHERE `name` = ?1", nativeQuery = true)
    public float findBalanceByName(String name);


    @Query(value = "SELECT * FROM members WHERE chat_id = ?1", nativeQuery = true)
    public Optional<Member> findByChatId(Long chat_id);

    public Member findByPassword(String password);

    @Query(value = "SELECT name FROM members WHERE chat_id = ?1", nativeQuery = true)
    public String findNameByChatId(Long chat_id);

    @Query(value = "SELECT previous FROM members WHERE chat_id = ?1", nativeQuery = true)
    public String findPreviousByChatId(Long chat_id);

    @Query(value = "SELECT chat_id FROM members WHERE  chat_id IS NOT null", nativeQuery = true)
    public Long[] getChatIds();

    @Modifying
    @Query(value = "UPDATE Member m SET m.balance = (?2 + m.balance) WHERE m.name = ?1")
    public void updateBalance(String name, float balance);

    @Modifying
    @Query(value = "UPDATE Member m SET m.chat_id = ?1 WHERE m.password = ?2")
    public void updateChatId(Long chat_id, String password);

    @Modifying
    @Query(value = "UPDATE Member m SET m.previous = ?2 WHERE m.chat_id = ?1")
    public void updatePrevious(Long chat_id, String previous);

    @Modifying
    @Query(value = "UPDATE Member m SET m.chat_id = 0 WHERE m.chat_id = ?1")
    public void dropChatId(Long chat_id);

    @Modifying
    @Query(value = "UPDATE Member m SET m.state = ?1 WHERE m.chat_id = ?2")
    public void updateState(String state, Long chat_id);

    @Modifying
    @Query(value = "UPDATE Member m SET m.state_manager = ?1 WHERE m.chat_id = ?2")
    public void updateStateManager(String state_manager, Long chat_id);

    @Modifying
    @Query(value = "UPDATE members SET state=\"none\"", nativeQuery = true)
    public void dropState();

    @Modifying
    @Query(value = "UPDATE members SET state_manager=\"none\"", nativeQuery = true)
    public void dropStateManager();
}
