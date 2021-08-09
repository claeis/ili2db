package ch.ehi.ili2db.base;

import static org.junit.Assert.*;
import org.junit.Test;

import java.sql.SQLException;

public class StatementExecutionHelperTest {

    @Test
    public void shouldNotExecuteBatch_withBatchSizeNull() throws SQLException {
        // given
        StatementExecutionHelper statementExecutionHelper = new StatementExecutionHelper(null);
        PreparedStatementMock psm = new PreparedStatementMock();

        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);

        // when
        statementExecutionHelper.flush(psm);

        // then
        assertEquals(5, psm.getExecutedUpdates());
    }

    @Test
    public void shouldNotExecuteBatch_withBatchSizeOne() throws SQLException {
        // given
        StatementExecutionHelper statementExecutionHelper = new StatementExecutionHelper(1);
        PreparedStatementMock psm = new PreparedStatementMock();

        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);

        // when
        statementExecutionHelper.flush(psm);

        // then
        assertEquals(5, psm.getExecutedUpdates());
    }

    @Test
    public void shouldExecuteBatch_oneExecution() throws SQLException {
        // given

        int batchSize = 6;
        StatementExecutionHelper statementExecutionHelper = new StatementExecutionHelper(batchSize);
        PreparedStatementMock psm = new PreparedStatementMock();

        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);

        // when
        statementExecutionHelper.flush(psm);

        // then
        assertEquals(0, psm.getExecutedUpdates());
        assertEquals(0, psm.getAddedToBatch());
        assertEquals(1, psm.getExecutedBatches());
    }

    @Test
    public void shouldExecuteBatch_twoExecutions() throws SQLException {
        // given

        int batchSize = 5;
        StatementExecutionHelper statementExecutionHelper = new StatementExecutionHelper(batchSize);
        PreparedStatementMock psm = new PreparedStatementMock();

        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);

        // when
        statementExecutionHelper.flush(psm);

        // then
        assertEquals(0, psm.getExecutedUpdates());
        assertEquals(0, psm.getAddedToBatch());
        assertEquals(2, psm.getExecutedBatches());
    }

    @Test
    public void shouldExecuteBatch_threeExecutions() throws SQLException {
        // given

        int batchSize = 5;
        StatementExecutionHelper statementExecutionHelper = new StatementExecutionHelper(batchSize);
        PreparedStatementMock psm = new PreparedStatementMock();

        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);
        statementExecutionHelper.write(psm);

        // when
        statementExecutionHelper.flush(psm);

        // then
        assertEquals(0, psm.getExecutedUpdates());
        assertEquals(0, psm.getAddedToBatch());
        assertEquals(3, psm.getExecutedBatches());
    }

}