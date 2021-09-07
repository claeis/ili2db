package ch.ehi.ili2db.base;

import ch.ehi.basics.logging.EhiLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementExecutionHelper {

    private Integer batchSize = null;
    private int queuedBatch = 0;
    private boolean shouldBatch = false;
    private int singleUpdatesCount = 0;
    private int totalBatchUpdatesCount = 0;

    public StatementExecutionHelper(Integer batchSize) {
        this.batchSize = batchSize;
        shouldBatch = this.batchSize != null && batchSize > 1; // batchsize 1 or less is pointless
    }

    public void write(PreparedStatement ps) throws SQLException {
        if (shouldBatch) {
            ps.addBatch();
            queuedBatch++;

            if (queuedBatch >= batchSize) {
                int[] updates = ps.executeBatch();
                totalBatchUpdatesCount += updates.length;
                ps.clearBatch();
                queuedBatch = 0;
                EhiLogger.traceState("batch executed, update counts: " + updates.length);
            }
        } else {
            ps.executeUpdate();
            singleUpdatesCount++;
        }

    }

    public void flush(PreparedStatement ps) throws SQLException {
        if (shouldBatch) {
            int[] updates = ps.executeBatch();
            totalBatchUpdatesCount += updates.length;
            ps.clearBatch();
            queuedBatch = 0;
            EhiLogger.traceState("total batch updates executed: " + totalBatchUpdatesCount);
        } else {
            EhiLogger.traceState("single updates executed: " + singleUpdatesCount);
        }
    }

}