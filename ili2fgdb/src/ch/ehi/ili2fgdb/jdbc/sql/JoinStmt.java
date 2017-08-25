package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class JoinStmt extends AbstractSelectStmt {

	private AbstractSelectStmt leftStmt=null;
	private List<AbstractSelectStmt> rightStmt=new ArrayList<AbstractSelectStmt>();
	private String leftKeyCol=null;
	private List<String> rightKeyCol=new ArrayList<String>();
	public JoinStmt(AbstractSelectStmt leftStmt,SqlQname c0) {
		this.leftStmt=leftStmt;
		leftKeyCol=c0.getLocalName();
	}
	public void addRight(AbstractSelectStmt rightStmt,SqlQname c1) {
		this.rightStmt.add(rightStmt);
		rightKeyCol.add(c1.getLocalName());
	}

	public AbstractSelectStmt getLeftStmt() {
		return leftStmt;
	}

	public List<AbstractSelectStmt> getRightStmt() {
		return rightStmt;
	}

	public String getLeftKeyCol() {
		return leftKeyCol;
	}
	public List<String> getRightKeyCol() {
		return rightKeyCol;
	}
	@Override
	public String toString() {
		return "JoinStmt [leftKeyCol=" + leftKeyCol + ", rightKeyCol=" + rightKeyCol 
				+",leftStmt=" + leftStmt + ", rightStmt=" + rightStmt
				+ "]";
	}
	@Override
	public void addCond(Value col, Value value) {
		leftStmt.addCond(col, value);
	}

}
