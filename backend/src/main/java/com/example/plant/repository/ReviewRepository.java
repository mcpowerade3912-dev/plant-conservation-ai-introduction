package com.example.plant.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewRepository {
    private final JdbcTemplate jdbc;

    public ReviewRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findByIntroductionId(Long id) {
        return jdbc.queryForList("""
                SELECT r.id, r.introduction_id, r.reviewer_id, u.nickname reviewer_name,
                r.result, r.comment, r.review_time
                FROM plant_review r JOIN sys_user u ON r.reviewer_id=u.id
                WHERE r.introduction_id=? ORDER BY r.id DESC
                """, id);
    }

    public long insert(Long introductionId, Long reviewerId, String result, String comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO plant_review(introduction_id, reviewer_id, result, comment, review_time)
                    VALUES(?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, introductionId);
            ps.setLong(2, reviewerId);
            ps.setString(3, result);
            ps.setString(4, comment);
            ps.setObject(5, LocalDateTime.now());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
