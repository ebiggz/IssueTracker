package com.mythicacraft.IssueTrackerExecutors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.mythicacraft.IssueTracker.cIssueTracker;

public class SQLExecutors {
    public String sqlURL;
    public final Logger logger = Logger.getLogger("Minecraft");

    public ResultSet selectSQL;
    public PreparedStatement statusQueryStatement;
    public PreparedStatement sampleQueryStatement;
    public Connection conn;
    public String errorString;
    
    /* TESTING INITIATING URL VARIABLE 
    public void sqlHostString() {
    	sqlURL = "jdbc:mysql://" + cIssueTracker.sqlHost + ":" + cIssueTracker.sqlPort+ "/" + cIssueTracker.sqlDbase;
    } */
    
  //Creates the connection
    public void dbConnect() throws SQLException {
    	sqlURL = "jdbc:mysql://" + cIssueTracker.sqlHost + ":" + cIssueTracker.sqlPort+ "/" + cIssueTracker.sqlDbase;
		conn = DriverManager.getConnection(sqlURL, cIssueTracker.sqlUser, cIssueTracker.sqlPass);
    }
    
   //Closes open connections
    public void dbClose() throws SQLException {
		sampleQueryStatement.close(); //Closes the query
		conn.close(); //Closes the connection
    }
    
    //For /issue status
	public void statusQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status !=  '3' AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;

	}
	//For /issue create
	public void createQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("INSERT INTO itrack_issuetracker (player, status, reason) VALUES ('"+ IssueCommand.senderName + "', 1, '"+ IssueCommand.issueReason + "')"); //Put your query in the quotes
		sampleQueryStatement.executeUpdate(); //Executes the query
		dbClose();
	}
	
	//For /issue close
	public void closeQuery() throws SQLException { 
		dbConnect();
		String tempname = null;
		//Connect to pull players name from database
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE issue_ID = " + IssueCommand.closeIssueID + " AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet nameCheck = sampleQueryStatement.executeQuery();
		while (nameCheck.next()){
		tempname = nameCheck.getString("player");
		}
		dbClose();	
		//Check players name in database is equal to the command sender and fire the SQL update
			if (tempname.matches(IssueCommand.senderName)){		
				dbConnect();
				sampleQueryStatement = conn.prepareStatement("UPDATE itrack_issuetracker SET status = 3 WHERE issue_ID = " + IssueCommand.closeIssueID); //Put your query in the quotes
				sampleQueryStatement.executeUpdate(); //Executes the query	
				}
		dbClose();
	}
	
	//For admin /issue status set #
	public void adminSetQuery() throws SQLException { 
		dbConnect();	
		try{
		sampleQueryStatement = conn.prepareStatement("UPDATE itrack_issuetracker SET status = " + IssueCommand.setStatus +" WHERE issue_ID = " + IssueCommand.closeIssueID); //Put your query in the quotes
		sampleQueryStatement.executeUpdate(); //Executes the query	
		}
		catch (Exception e){
			errorString = ("Please enter a valid issue ID.");
		}
		dbClose();
	}
	
	public void adminStatusQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status !=  '3'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
	}
	
	public void adminCloseQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status = '3' AND player = '" + IssueCommand.closePlayer + "'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
	}
	
	public void viewCloseQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status = '3' AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
	}
	
	//Called onEnable - checks if database exists, if not - creates database
	public void CreateTable() throws SQLException { 
		dbConnect();
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "itrack_issuetracker", null);
		this.logger.info("Checking for IssueTracker database table....");
		if (!tables.next()) {
			this.logger.info("Table not found, creating table");
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE itrack_issuetracker(issue_id INT AUTO_INCREMENT KEY, player varchar(255) NOT NULL, status int, reason varchar(255))";
			stmt.executeUpdate(sql);
		}
		conn.close();
	}

	
}
