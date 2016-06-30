package hive;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import org.apache.hadoop.conf.Configuration;

public class Rule_two {
    
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
  
    public static void main(String[] args) throws SQLException {
    	//hive
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection con = DriverManager.getConnection("jdbc:hive2://ubuntu:10000/log", "hadoop", "19930403");
        Statement stmt = con.createStatement();
        String sql =  "select transform(rule_two.c_ip,rule_two.date,rule_two.time,rule_two.uri_stem) using '/usr/bin/python /home/hadoop/json.py c_ip date time uri_stem' as (result string) from rule_two";
        ResultSet res = stmt.executeQuery(sql);
        //file
        FileOutputStream out = null;
        try{
        	out = new FileOutputStream(new File("/home/hadoop/logJson/rule_two.json"));
        	while(res.next()) {
                out.write(res.getString(1).getBytes());
            }
        	out.close();
        }catch (Exception e) {   

            e.printStackTrace();   

        }   
        finally {   

            try {   
            	out.close();   
            } catch (Exception e) {   

                e.printStackTrace();   

            }   
         }     
    }
}