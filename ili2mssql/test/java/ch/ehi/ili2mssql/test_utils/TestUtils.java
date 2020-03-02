package ch.ehi.ili2mssql.test_utils;

import java.io.File;

public class TestUtils {
    static String dropSchemaScript;
    
    static public String getDropScript(String dbschema) throws java.io.IOException {
        if(dropSchemaScript==null) {
            File file = new File("test/data/MssqlBase/dropSchema.sql");
            java.io.InputStream is = new java.io.FileInputStream(file.getPath());
            java.io.BufferedReader buf = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
                    
            while(line != null){
               sb.append(line).append("\n");
               line = buf.readLine();
            }
            buf.close();
            dropSchemaScript = sb.toString();
        }
        return dropSchemaScript.replace("{{{schema}}}", dbschema);
    }
}
