package com.example.plant.repository;

import com.example.plant.model.Plant;
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
public class PlantRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Plant> rowMapper = (rs, i) -> {
        Plant p = new Plant();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setScientificName(rs.getString("scientific_name"));
        p.setCategory(rs.getString("category"));
        p.setOrigin(rs.getString("origin"));
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getString("status"));
        p.setImageUrl(rs.getString("image_url"));
        long value = rs.getLong("intro_record_id");
        p.setIntroRecordId(rs.wasNull() ? null : value);
        p.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        p.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        return p;
    };

    public PlantRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Plant> findAll() {
        return jdbc.query("SELECT * FROM plant ORDER BY id DESC", rowMapper);
    }

    public Optional<Plant> findById(Long id) {
        return jdbc.query("SELECT * FROM plant WHERE id=?", rowMapper, id).stream().findFirst();
    }

    public Plant insert(Plant x) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO plant(name, scientific_name, category, origin, quantity,
                    status, image_url, intro_record_id, create_time, update_time)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, x.getName());
            ps.setString(2, x.getScientificName());
            ps.setString(3, x.getCategory());
            ps.setString(4, x.getOrigin());
            ps.setInt(5, x.getQuantity());
            ps.setString(6, x.getStatus());
            ps.setString(7, x.getImageUrl());
            if (x.getIntroRecordId() == null) ps.setObject(8, null);
            else ps.setLong(8, x.getIntroRecordId());
            ps.setObject(9, LocalDateTime.now());
            ps.setObject(10, LocalDateTime.now());
            return ps;
        }, keyHolder);
        x.setId(keyHolder.getKey().longValue());
        return x;
    }

    public void update(Plant x) {
        jdbc.update("""
                UPDATE plant SET name=?, scientific_name=?, category=?, origin=?, quantity=?,
                status=?, image_url=?, update_time=? WHERE id=?
                """, x.getName(), x.getScientificName(), x.getCategory(), x.getOrigin(),
                x.getQuantity(), x.getStatus(), x.getImageUrl(), LocalDateTime.now(), x.getId());
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM plant WHERE id=?", id);
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM plant", Long.class);
    }
}
