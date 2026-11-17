package com.example.plant.repository;

import com.example.plant.model.IntroductionRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IntroductionRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<IntroductionRecord> rowMapper = (rs, i) -> {
        IntroductionRecord x = new IntroductionRecord();
        x.setId(rs.getLong("id"));
        x.setUserId(rs.getLong("user_id"));
        x.setCreatorName(rs.getString("creator_name"));
        x.setPlantName(rs.getString("plant_name"));
        x.setScientificName(rs.getString("scientific_name"));
        x.setOrigin(rs.getString("origin"));
        x.setIntroductionDate(rs.getDate("introduction_date").toLocalDate());
        x.setQuantity(rs.getInt("quantity"));
        x.setDescription(rs.getString("description"));
        x.setImageUrl(rs.getString("image_url"));
        x.setStatus(rs.getString("status"));
        x.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        x.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        return x;
    };
    private final String base = """
            SELECT p.*, u.nickname creator_name
            FROM plant_introduction p JOIN sys_user u ON p.user_id = u.id
            """;

    public IntroductionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<IntroductionRecord> findAll(String status, String keyword) {
        StringBuilder sql = new StringBuilder(base + "WHERE 1=1 ");
        if (status != null && !status.isBlank()) sql.append("AND p.status=? ");
        if (keyword != null && !keyword.isBlank()) sql.append("AND (p.plant_name LIKE ? OR p.origin LIKE ?) ");
        sql.append("ORDER BY p.id DESC");
        List<Object> args = new ArrayList<>();
        if (status != null && !status.isBlank()) args.add(status);
        if (keyword != null && !keyword.isBlank()) {
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
        }
        return jdbc.query(sql.toString(), rowMapper, args.toArray());
    }

    public List<IntroductionRecord> findByUserId(Long id) {
        return jdbc.query(base + "WHERE p.user_id=? ORDER BY p.id DESC", rowMapper, id);
    }

    public List<IntroductionRecord> findByStatus(String status) {
        return jdbc.query(base + "WHERE p.status=? ORDER BY p.id DESC", rowMapper, status);
    }

    public Optional<IntroductionRecord> findById(Long id) {
        return jdbc.query(base + "WHERE p.id=?", rowMapper, id).stream().findFirst();
    }

    public IntroductionRecord insert(IntroductionRecord x) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO plant_introduction(user_id, plant_name, scientific_name, origin,
                    introduction_date, quantity, description, image_url, status, create_time, update_time)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, x.getUserId());
            ps.setString(2, x.getPlantName());
            ps.setString(3, x.getScientificName());
            ps.setString(4, x.getOrigin());
            ps.setDate(5, Date.valueOf(x.getIntroductionDate()));
            ps.setInt(6, x.getQuantity());
            ps.setString(7, x.getDescription());
            ps.setString(8, x.getImageUrl());
            ps.setString(9, x.getStatus());
            ps.setObject(10, LocalDateTime.now());
            ps.setObject(11, LocalDateTime.now());
            return ps;
        }, keyHolder);
        x.setId(keyHolder.getKey().longValue());
        return x;
    }

    public void update(IntroductionRecord x) {
        jdbc.update("""
                UPDATE plant_introduction SET plant_name=?, scientific_name=?, origin=?,
                introduction_date=?, quantity=?, description=?, image_url=?, status=?, update_time=? WHERE id=?
                """, x.getPlantName(), x.getScientificName(), x.getOrigin(),
                Date.valueOf(x.getIntroductionDate()), x.getQuantity(), x.getDescription(),
                x.getImageUrl(), x.getStatus(), LocalDateTime.now(), x.getId());
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM plant_introduction WHERE id=?", id);
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM plant_introduction", Long.class);
    }

    public long countPending() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM plant_introduction WHERE status='PENDING'", Long.class);
    }
}
