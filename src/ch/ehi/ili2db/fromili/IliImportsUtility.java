package ch.ehi.ili2db.fromili;

import ch.interlis.ili2c.modelscan.IliFile;
import ch.interlis.ili2c.modelscan.IliModel;

public class IliImportsUtility {

	public static String getIliImports(IliFile ilifile)
	{
		StringBuffer ret=new StringBuffer();
		java.util.Iterator<ch.interlis.ili2c.modelscan.IliModel> modeli=ilifile.iteratorModel();
		String sep="";
		while(modeli.hasNext()){
			ch.interlis.ili2c.modelscan.IliModel model=modeli.next();
			ret.append(sep+model.getName());sep=" ";
			java.util.Iterator<String> deps=model.getDependencies().iterator();
			if(deps.hasNext()){
				ret.append("{");
				while(deps.hasNext()){
					String dep=deps.next();
					ret.append(" ");
					ret.append(dep);
				}
				ret.append("}");
			}
		}
		return ret.toString();
	}
	public static IliFile parseIliImports(double iliversion,String value)
	{
		IliFile ret=null;
		IliImportsUtility parser = new IliImportsUtility(value);
		String token=parser.nextToken();
		while(token!=null){
			String modelName=token;
			if(ret==null){
				ret=new IliFile();
			}
			token=parser.nextToken();
			if(token!=null && token.equals("{")){
				IliModel model=new IliModel();
				model.setIliVersion(iliversion);
				model.setName(modelName);
				token=parser.nextToken();
				while(token!=null && !token.equals("}")){
					String imports=token;
					model.addDepenedency(imports);
					token=parser.nextToken();
				}
				ret.addModel(model);
				token=parser.nextToken();
			}else{
				// model without imports
				IliModel model=new IliModel();
				model.setIliVersion(iliversion);
				model.setName(modelName);
				ret.addModel(model);
			}
		}
		return ret;
	}
	private String content=null;
	private int pos=0;
	private IliImportsUtility(String value)
	{
		content=value;
	}
	private String nextToken()
	{
		if(content==null){
			return null;
		}
		if(pos>=content.length()){
			return null;
		}
		// skip whitespace
		while(pos<content.length() && content.charAt(pos)==' '){
			pos++;
		}
		if(pos>=content.length()){
			return null;
		}
		if(content.charAt(pos)=='{'){
			pos++;
			return "{";
		}else if(content.charAt(pos)=='}'){
			pos++;
			return "}";
		}
		StringBuffer name=new StringBuffer();
		char c;
		while(pos<content.length() && (c=content.charAt(pos))!=' ' && c!='{'  && c!='}'){
			name.append(c);
			pos++;
		}
		return name.toString();
	}
	public static void main(String args[])
	{
		IliFile file=parseIliImports(1.0,"ModelA{ModelC}ModelB");
		System.out.println(file);
		
	}
}
