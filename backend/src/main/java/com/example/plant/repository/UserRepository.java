package com.example.plant.repository;

import com.example.plant.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> rowMapper = (rs, i) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getInt("status"));
        u.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        u.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        return u;
    };

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.query("SELECT * FROM sys_user ORDER BY id DESC", rowMapper);
    }

    public Optional<User> findById(Long id) {
        return jdbc.query("SELECT * FROM sys_user WHERE id=?", rowMapper, id).stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return jdbc.query("SELECT * FROM sys_user WHERE username=?", rowMapper, username).stream().findFirst();
    }

    public User insert(User u) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO sys_user(username, password, nickname, phone, email,
                    role, status, create_time, update_time)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNickname());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getRole());
            ps.setInt(7, u.getStatus());
            ps.setObject(8, LocalDateTime.now());
            ps.setObject(9, LocalDateTime.now());
            return ps;
        }, keyHolder);
        u.setId(keyHolder.getKey().longValue());
        return u;
    }

    public void update(User u) {
        jdbc.update("""
                UPDATE sys_user SET nickname=?, phone=?, email=?, role=?, status=?,
                update_time=? WHERE id=?
                """, u.getNickname(), u.getPhone(), u.getEmail(), u.getRole(),
                u.getStatus(), LocalDateTime.now(), u.getId());
    }

    public void updateProfile(Long id, String nickname, String phone, String email) {
        jdbc.update("UPDATE sys_user SET nickname=?, phone=?, email=?, update_time=? WHERE id=?",
                nickname, phone, email, LocalDateTime.now(), id);
    }

    public void updatePassword(Long id, String password) {
        jdbc.update("UPDATE sys_user SET password=?, update_time=? WHERE id=?",
                password, LocalDateTime.now(), id);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM sys_user WHERE id=?", id);
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM sys_user", Long.class);
    }
}
