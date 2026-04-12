package com.sps.ids.sepa_importer.repository;

import com.sps.ids.sepa_importer.entity.SepaDeadLetterRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeadLetterQueueRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeadLetterQueueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SepaDeadLetterRecord> findAll(int page, int size) {
        String sql = """
            SELECT Id, RecordKey, ModificationType, RawData, ErrorMessage, 
                   ErrorTimestamp, RetryCount, Resolved
            FROM SepaDeadLetterQueue
            ORDER BY ErrorTimestamp DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        return jdbcTemplate.query(sql, new Object[]{page * size, size},
                (rs, rowNum) -> {
                    SepaDeadLetterRecord record = new SepaDeadLetterRecord();
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
                "SELECT COUNT(*) FROM SepaDeadLetterQueue", Integer.class);
    }

    public void markAsResolved(long id) {
        jdbcTemplate.update(
                "UPDATE SepaDeadLetterQueue SET Resolved = 1 WHERE Id = ?", id);
    }
}