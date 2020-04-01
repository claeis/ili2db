package ch.ehi.ili2mssql.test_utils;

import java.io.File;

public class TestUtils {
    private static String dropSchemaScript;
    
    private TestUtils() { }
    
    public static String getDropScript(String dbschema) throws java.io.IOException {
        if(dropSchemaScript==null) {
            File file = new File("test/data/MssqlBase/dropSchema.sql");
            java.io.InputStream is = null;
            java.io.BufferedReader buf = null;
            StringBuilder sb = new StringBuilder();
            try {
                is = new java.io.FileInputStream(file.getPath());
                buf = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                String line = buf.readLine();
                
                while(line != null){
                   sb.append(line).append("\n");
                   line = buf.readLine();
                }
            } finally {
                if(is!=null) {
                    if(buf!=null) buf.close();
                    is.close();
                }
            }
            dropSchemaScript = sb.toString();
        }
        return dropSchemaScript.replace("{{{schema}}}", dbschema);
    }
}
