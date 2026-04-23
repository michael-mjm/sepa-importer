package com.sps.ids.swift_importer.repository;

import com.sps.ids.swift_importer.model.SwiftDeadLetterRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeadLetterQueueRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeadLetterQueueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SwiftDeadLetterRecord> findAll(int page, int size) {
        String sql = """
            SELECT Id, RecordKey, ModificationType, RawData, ErrorMessage, 
                   ErrorTimestamp, RetryCount, Resolved
            FROM swift.dead_letter_queue
            ORDER BY ErrorTimestamp DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        return jdbcTemplate.query(sql, new Object[]{page * size, size},
                (rs, rowNum) -> {
                    SwiftDeadLetterRecord record = new SwiftDeadLetterRecord();
                    record.setId(rs.getLong("Id"));
                    record.setRecordKey(rs.getString("RecordKey"));
                    record.setModificationType(rs.getString("ModificationType"));
                    record.setRawData(rs.getString("RawData"));
                    record.setErrorMessage(rs.getString("ErrorMessage"));
                    record.setErrorTimestamp(rs.getTimestamp("ErrorTimestamp").toLocalDateTime());
                    record.setRetryCount(rs.getInt("RetryCount"));
                    record.setResolved(rs.getBoolean("Resolved"));
                    return record;
                });
    }

    public int countTotal() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM swift.dead_letter_queue", Integer.class);
    }

    public void markAsResolved(long id) {
        jdbcTemplate.update(
                "UPDATE swift.dead_letter_queue SET Resolved = 1 WHERE Id = ?", id);
    }

    public SwiftDeadLetterRecord findById(long id) {
        return jdbcTemplate.queryForObject(
            """
            SELECT Id, RecordKey, ModificationType, RawData, ErrorMessage,
                   ErrorTimestamp, RetryCount, Resolved
            FROM swift.dead_letter_queue
            WHERE Id = ?
            """,
            (rs, rowNum) -> {
                SwiftDeadLetterRecord r = new SwiftDeadLetterRecord();
                r.setId(rs.getLong("Id"));
                r.setRecordKey(rs.getString("RecordKey"));
                r.setModificationType(rs.getString("ModificationType"));
                r.setRawData(rs.getString("RawData"));
                r.setErrorMessage(rs.getString("ErrorMessage"));
                r.setErrorTimestamp(rs.getTimestamp("ErrorTimestamp").toLocalDateTime());
                r.setRetryCount(rs.getInt("RetryCount"));
                r.setResolved(rs.getBoolean("Resolved"));
                return r;
            },
            id
        );
    }

    public void incrementRetryCount(long id, String errorMessage) {
        jdbcTemplate.update(
            """
            UPDATE swift.dead_letter_queue
            SET RetryCount = RetryCount + 1,
                ErrorMessage = ?
            WHERE Id = ?
            """,
            errorMessage, id
        );
    }
}