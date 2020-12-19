package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {
	
	public static void main(String[] args) {  
		System.out.println("in the method");
		try{  
		Class.forName("oracle.jdbc.driver.OracleDriver");  
		Connection con=DriverManager.getConnection(  
		"jdbc:oracle:thin:@ERPODADEV.FULLERTON.EDU:1521/OBIADVL","DEVOBIA_DW","obia4dev#");  
		              
		PreparedStatement ps=con.prepareStatement("select * from WC_INTER_STG");  
		ResultSet rs=ps.executeQuery();  
		rs.next();//now on 1st row  
		
		con.close();  
		              
		System.out.println("success");  
		}catch (Exception e) {
			e.printStackTrace();  
			}  
		}  

}
