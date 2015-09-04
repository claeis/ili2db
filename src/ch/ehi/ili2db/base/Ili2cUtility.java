package ch.ehi.ili2db.base;

import ch.interlis.ili2c.metamodel.Viewable;

/** functions that should be moved to ili2c.
 */
public class Ili2cUtility {

	/** tests if a viewable has no (known) extensions.
	 * @param def viewable to test
	 * @return true if no subtypes known; true if subtypes known.
	 */
	public static boolean isViewableWithExtension(Viewable def){
		return def.getExtensions().size()>1;
	}

}
