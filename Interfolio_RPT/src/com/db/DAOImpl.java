package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.business.ParamVO;

public class DAOImpl {
	
	private String INTERFOLIO_STG_TABLE_NAME = "WC_INTER_STG_1210";
	
	//changes made in the file Kalmesh 
	public static Connection createConnection() throws Exception {
		try {
			
			 Class.forName("oracle.jdbc.driver.OracleDriver");	
			 return java.sql.DriverManager.getConnection(
					 "jdbc:oracle:thin:@erpodaprd.fullerton.edu:1521/obiaprd","OBIA_DW","obia4prd#");  
		} catch (SQLException sqlException) {	
			System.out.println(sqlException.getMessage());
			
			throw sqlException;
		}
	}
	
	public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				//resultSet.clearWarnings();
				resultSet.close();
			}
			if (statement != null) {
				//statement.clearWarnings();
				statement.close();
			}
			if (connection != null) {
				if (!connection.getAutoCommit()) {
					
					connection.setAutoCommit(true);
				}
				
				connection.close();
			}

		} catch (Exception ex) {
			System.out.println("Error in closing the JDBC Connection " + "or ResultSet or Statement:" + ex.getMessage());
			
		}
	}
	
	public ParamVO getParamValues() throws Exception{
		ParamVO paramVO = new ParamVO();
		List<ParamVO> paramList	= new ArrayList<ParamVO>();
		Connection conn = null;
		PreparedStatement ps	= null;
		ResultSet rs = null;
		try{  
			
			conn = createConnection();
			ps=conn.prepareStatement("select * from "+INTERFOLIO_STG_TABLE_NAME+" where CREATED_FLG is null");  
			rs=ps.executeQuery();  		
			if(rs.next()){
				ParamVO tempParamVO = null;
				do{					
					tempParamVO = new ParamVO();
					tempParamVO.setCandidate_first_name(rs.getString("CAND_FIRST_NM"));
					tempParamVO.setCandidate_last_name(rs.getString("CAND_LAST_NM"));
					tempParamVO.setCandidate_email(rs.getString("CAND_EMAIL_ADDR"));
					//tempParamVO.setDue_at(rs.getDate("DUE_AT"));
					tempParamVO.setCwid(rs.getString("CWID"));
					tempParamVO.setTemplate_id(rs.getString("TEMPLATE_ID"));
					paramList.add(tempParamVO);
					
				}while(rs.next());
				paramVO.setParamList(paramList);
			}
			
			conn.close();  
			              
			System.out.println("success");  
			}catch (Exception e) {
				e.printStackTrace();  
				}  finally {
					closeConnection(conn, null, null);
				}
			 
	return paramVO;
	}
	
	public void updateAuditFlg(String cwid, String templateID){
		PreparedStatement ps	= null;
		Connection conn = null;
		try{  
			
			conn = createConnection();
			ps=conn.prepareStatement("update "+INTERFOLIO_STG_TABLE_NAME+" set created_flg='Y' where cwid="+cwid+" and TEMPLATE_ID="+templateID);  
			System.out.println("auto commit - "+conn.getAutoCommit());
			ps.executeQuery();  		
		}catch(Exception e){
			e.printStackTrace();  
		}finally {
			closeConnection(conn, null, null);
		}
		
	}
	
	public static void main(String[] args) throws Exception {  
		DAOImpl imp = new DAOImpl();
		imp.getParamValues();
	}

}
