package com.space_asians.w1ncent.repositories;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Component;

@Component
public class SessionRepository {


    RedisClient redisClient = RedisClient.create("redis://localhost:6379/0");
    StatefulRedisConnection<String, String> redisConnection = this.redisClient.connect();
    RedisCommands<String, String> session = redisConnection.sync();

    public RedisCommands<String, String> create_connection() {
        return this.session;
    }

    public void create_session(Long chat_id) {
        String id = chat_id.toString();
        this.session.set(id + ":state", "none");
        this.session.set(id + ":state_manager", "none");
        this.session.set(id + ":state_finances_update", "0");
        this.session.set(id + ":awaiting_response", "false");

        this.session.set(id + ":date", "");
        this.session.set(id + ":custom_date", "false");
        this.session.set(id + ":payer", "");
        this.session.set(id + ":receivers", "");
        this.session.set(id + ":selected_members", "");
        this.session.set(id + ":sum", "");
        this.session.set(id + ":occasion", "");
    }


}
