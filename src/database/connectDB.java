//Author : Vincent Lim
//Email  : vince.lim@outlook.com

package database;
import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;
@SuppressWarnings("unused")
public class connectDB{
	// JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost:8889/DAD";

	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "root";
	   
	   Connection conn = null;
	   Statement stmt = null;
	   
	   public connectDB(){
		   try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver").newInstance();
			      //STEP 3: Open a connection
			      conn = DriverManager.getConnection(DB_URL, USER, PASS);
			      
			      //STEP 4: Execute a query
			      System.out.println("Create table if not exist...");
			      stmt = conn.createStatement();
			      
			      String sql = "create table if not exists User("
			      		+ "username varchar(256) not null,"
			      		+ "password varchar(256) not null,"
			      		+ "PRIMARY KEY (username))";
			      
				int update = stmt.executeUpdate(sql);
			  

			   }catch(SQLException se){
			      //Handle errors for JDBC
			      System.out.println("SQL Connection Error...");
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            conn.close();
			      }catch(SQLException se){
			      }// do nothing
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			    	  System.out.println("Failed to close connection...");
			      }//end finally try
			   }//end try
	   }
	   
	   public HashMap<String,String> queryUser(){
		   HashMap<String,String> userList = new HashMap<String,String>();
		   
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Gathering data from database...");
		      stmt = conn.createStatement();
		      
		      String sql = "SELECT * FROM User";
		      
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		    	  userList.put(rs.getString("username"), rs.getString("password"));
		      }
		      rs.close();

		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   return userList;
	   }

	   public void regUser(String username,String password){

		   try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver").newInstance();
			      //STEP 3: Open a connection
			      System.out.println("Connecting to a selected database...");
			      conn = DriverManager.getConnection(DB_URL, USER, PASS);
			      System.out.println("Connected database successfully...");
			      
			      //STEP 4: Execute a query
			      System.out.println("Inserting records into the table...");
			      stmt = conn.createStatement();
			      
			      String sql = "INSERT INTO User Values ('" + username + "','" + password + "')";/////////////Check syntax
			      

				int update = stmt.executeUpdate(sql);
			  

			   }catch(SQLException se){
			      //Handle errors for JDBC
			      System.out.println("SQL Connection Error...");
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            conn.close();
			      }catch(SQLException se){
			      }// do nothing
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			    	  System.out.println("Failed to close connection...");
			      }//end finally try
			   }//end try
	   }

	   public void userLogout(String username){
		   //remove auth_token
	   }
	   
	   public void userLogin(String username){
		   //add auth_token to db
	   }
}
