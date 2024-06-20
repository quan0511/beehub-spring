package vn.aptech.beehub.querydb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String sql = "ALTER TABLE like_user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_bin";
        jdbcTemplate.execute(sql);
    }
}
