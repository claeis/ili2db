package ch.ehi.ili2fgdb;

import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.fgdb4j.Fgdb4jException;

public class InitFgdbApi implements ch.ehi.ili2db.base.Ili2dbLibraryInit {
	private static int refc=0;
	public void init()
	{
		refc++;
		if(refc==1){
			try {
				Fgdb4j.initialize();
			} catch (Fgdb4jException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	public void end()
	{
		refc--;
		if(refc==0){
			Fgdb4j.cleanup();
		}
	}
}
