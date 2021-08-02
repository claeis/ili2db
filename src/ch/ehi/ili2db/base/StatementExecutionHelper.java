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

    public void executeSingleOrBatch(PreparedStatement ps, boolean isLastStatement) throws SQLException {
        if (shouldBatch) {

            if (!isLastStatement) {
                ps.addBatch();
                queuedBatch++;
            }

            if (queuedBatch >= batchSize || isLastStatement) {
                int[] updates = ps.executeBatch();
                totalBatchUpdatesCount += updates.length;
                ps.clearBatch();
                queuedBatch = 0;
                EhiLogger.logState("batch executed, update counts: " + updates.length);

                if (isLastStatement) {
                    EhiLogger.logState("total batch updates executed: " + totalBatchUpdatesCount);
                }
            }
        } else {
            if (!isLastStatement) {
                ps.executeUpdate();
                singleUpdatesCount++;
            } else {
                EhiLogger.logState("single updates executed: " + singleUpdatesCount);
            }
        }
    }

    // TODO remove this method exist only for performances testing purpose
    public void writeToFile(String methodName, int processedStatements, Integer batchSize, long duration) {
        String fileName = methodName + "_processedStatements_" + processedStatements + ".csv";
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(batchSize + ";" + duration + ";" + "\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write report" + fileName, e);
        }
    }

}