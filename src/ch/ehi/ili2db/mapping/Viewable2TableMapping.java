package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.Set;

import ch.interlis.ili2c.metamodel.Viewable;

public class Viewable2TableMapping {
	private HashMap<Viewable,ViewableWrapper> mapping=new HashMap<Viewable,ViewableWrapper>(); 
	public Viewable2TableMapping() {
	}
	public void add(Viewable aclass,ViewableWrapper table){
		mapping.put(aclass, table);
	}
	public ViewableWrapper get(Viewable base) {
		return mapping.get(base);
	}
	public Set<Viewable> getViewables(){
	    return mapping.keySet();
	}
}
