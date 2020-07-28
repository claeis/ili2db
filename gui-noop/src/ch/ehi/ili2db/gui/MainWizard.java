package ch.ehi.ili2db.gui;


import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;

public class MainWizard {
	public static void main(Config config,String appHome,String appName,AbstractDbPanelDescriptor dbPanelDescriptor,DbUrlConverter makeUrl) {
        EhiLogger.logError("no GUI implemented");
        System.exit(1);
	}

}
