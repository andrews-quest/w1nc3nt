package com.space_asians.w1ncent.repositories;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SessionRepository {

    @Value("${session.url}")
    String url;
    RedisClient redisClient;
    StatefulRedisConnection<String, String> redisConnection;
    RedisCommands<String, String> session;

    @PostConstruct
    public void init () {
        this.redisClient = RedisClient.create(url);
        this.redisConnection = this.redisClient.connect();
        this.session = redisConnection.sync();
    }

    public RedisCommands<String, String> create_connection() {
        return this.session;
    }

    public void create_session(Long chat_id) {
        String id = chat_id.toString();
        this.session.set(id + ":state", "none");
        this.session.set(id + ":state_manager", "none");
        this.session.set(id + ":state_finances_update", "0");
        this.session.set(id + ":awaiting_response", "false");
        this.session.set(id + ":back", "false");

        this.session.set(id + ":date", "");
        this.session.set(id + ":custom_date", "false");
        this.session.set(id + ":payer", "");
        this.session.del(id + ":receivers");
        this.session.del(id + ":selected_members");
        this.session.del(id + ":previous_transactions");
        this.session.set(id + ":multiple_members", "false");
        this.session.set(id + ":sum", "");
        this.session.set(id + ":occasion", "");
    }


}
