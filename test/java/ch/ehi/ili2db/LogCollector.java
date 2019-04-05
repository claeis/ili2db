package ch.ehi.ili2db;

import java.util.ArrayList;

import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.logging.LogListener;



public class LogCollector implements LogListener {
	private ArrayList<LogEvent> errs=new ArrayList<LogEvent>();
	private ArrayList<LogEvent> warn=new ArrayList<LogEvent>();

	public ArrayList<LogEvent> getErrs() {
		return errs;
	}
	
	public ArrayList<LogEvent> getWarn() {
		return warn;
	}

    @Override
    public void logEvent(LogEvent event) {
        if(event.getEventKind()==LogEvent.ERROR){
            errs.add(event);
        }else if(event.getEventKind()==LogEvent.STATE){
            warn.add(event);
        }
        
    }
}
