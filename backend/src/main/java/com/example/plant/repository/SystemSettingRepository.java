package com.example.plant.repository;

import com.example.plant.model.SystemSetting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SystemSettingRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<SystemSetting> rowMapper = (rs, rowNum) -> {
        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(rs.getString("setting_key"));
        setting.setSettingValue(rs.getString("setting_value"));
        setting.setSecret(rs.getInt("is_secret"));
        long updatedBy = rs.getLong("updated_by");
        setting.setUpdatedBy(rs.wasNull() ? null : updatedBy);
        if (rs.getTimestamp("update_time") != null) {
            setting.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        }
        return setting;
    };

    public SystemSettingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<SystemSetting> findByKey(String key) {
        return jdbcTemplate.query(
                "SELECT * FROM system_setting WHERE setting_key=?",
                rowMapper, key).stream().findFirst();
    }

    public void upsert(String key, String value, boolean secret, Long updatedBy) {
        jdbcTemplate.update("""
                INSERT INTO system_setting(setting_key, setting_value, is_secret, updated_by, update_time)
                VALUES(?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value),
                is_secret=VALUES(is_secret), updated_by=VALUES(updated_by), update_time=VALUES(update_time)
                """, key, value, secret ? 1 : 0, updatedBy, LocalDateTime.now());
    }

    public void delete(String key) {
        jdbcTemplate.update("DELETE FROM system_setting WHERE setting_key=?", key);
    }

    public List<SystemSetting> findAll() {
        return jdbcTemplate.query("SELECT * FROM system_setting", rowMapper);
    }
}
