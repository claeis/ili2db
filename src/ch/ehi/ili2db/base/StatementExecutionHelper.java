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

    public void executeSingleOrAddTobatch(PreparedStatement ps) throws SQLException {
        if (shouldBatch) {
            ps.addBatch();
            queuedBatch++;
        } else {
            ps.executeUpdate();
            singleUpdatesCount++;
        }
    }

    public void executeBatch(PreparedStatement ps, boolean isLastStatement) throws SQLException {
        if (shouldBatch && (queuedBatch >= batchSize || isLastStatement)) {
            int[] updates = ps.executeBatch();
            totalBatchUpdatesCount += updates.length;
            ps.clearBatch();
            queuedBatch = 0;
            EhiLogger.logState("batch executed, update counts: " + updates.length);
            EhiLogger.logState("batch executed, total batch updates counts: " + totalBatchUpdatesCount);
        } else if (!shouldBatch && isLastStatement) {
            EhiLogger.logState("single updates executed: " + singleUpdatesCount);
        }
    }

    // TODO remove this method exist only for performances testing purpose
    public void writeToFile(String methodName, int processedStatements, Integer batchSize, long duration) {
        String fileName = methodName + "_processedStatements_"+processedStatements+".csv";
        System.err.println("WR FILE");
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(batchSize + ";" + duration + ";"+"\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write report" + fileName, e);
        }
    }

}