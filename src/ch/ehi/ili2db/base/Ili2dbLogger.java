package ch.ehi.ili2db.base;

import java.io.File;

import ch.ehi.basics.logging.FileListener;
import ch.ehi.basics.logging.LogEvent;

public class Ili2dbLogger extends FileListener {

	public Ili2dbLogger(File arg0) {
		super(arg0);
	}

	public Ili2dbLogger(File arg0, boolean arg1) {
		super(arg0, arg1);
	}
	@Override
	public String getMessageTag(LogEvent event){
		return event.getEventKind()==LogEvent.ERROR ? "Error" : "Info";
	}

}
