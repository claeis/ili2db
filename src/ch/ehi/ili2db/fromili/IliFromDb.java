package ch.ehi.ili2db.fromili;

import java.io.IOException;
import java.io.InputStream;

import ch.ehi.ili2db.base.Ili2dbException;
import ch.interlis.ilirepository.IliResolver;

public class IliFromDb implements IliResolver {
	private String uri=null;
	private java.sql.Connection conn=null;
	private String schema=null; // dbschema or null
	public IliFromDb(String dburi, java.sql.Connection conn,String schema)
	{
		this.uri=dburi;
		this.conn=conn;
		this.schema=schema;
	}
	@Override
	public InputStream resolveIliFile(String dburi, String filename)
			throws IOException {
		String file=null;
		try {
			file=TransferFromIli.readIliFile(conn, schema,filename);
		} catch (Ili2dbException e) {
			throw new IOException(e); 
		}
		if(file==null){
			throw new java.io.FileNotFoundException("file <"+filename+"> not found in "+dburi); 
		}
		return new java.io.ByteArrayInputStream(file.getBytes("UTF-8"));
	}

	@Override
	public boolean resolvesUri(String uri) {
		if(uri!=null && uri.equals(this.uri)){
			return true;
		}
		return false;
	}

}
