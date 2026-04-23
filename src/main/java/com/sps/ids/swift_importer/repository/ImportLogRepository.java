package com.sps.ids.swift_importer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;

@Repository
public class ImportLogRepository {

    private final JdbcTemplate jdbc;

    public ImportLogRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long create(String fileName) {
        jdbc.update("""
            INSERT INTO swift.import_log
            (FileName, ProcessedRows, ErrorCount, Status)
            VALUES (?, 0, 0, 'STARTED')
        """, fileName);

        return jdbc.queryForObject(
            "SELECT SCOPE_IDENTITY()", Long.class
        );
    }


    public long create(String fileName, Long jobExecutionId) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                """
                INSERT INTO swift.import_log
                  (FileName, ProcessedRows, ErrorCount, Status, JobExecutionId)
                VALUES (?, 0, 0, 'STARTED', ?)
                """,
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, fileName);
            ps.setLong(2, jobExecutionId);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException(
                "Could not retrieve generated LogId from swift.import_log"
            );
        }

        return key.longValue();
    }


    public void finish(long logId,
                       int processedRows,
                       int errorCount,
                       String status) {

        jdbc.update("""
            UPDATE swift.import_log
            SET ProcessedRows = ?,
                ErrorCount = ?,
                EndTime = ?,
                Status = ?
            WHERE LogId = ?
        """,
            processedRows,
            errorCount,
            LocalDateTime.now(),
            status,
            logId
        );
    }
}