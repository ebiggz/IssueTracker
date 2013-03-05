package com.mythicacraft.IssueTrackerExecutors;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.IssueTracker.cIssueTracker;

public class IssueCommand implements CommandExecutor{

	private cIssueTracker plugin;
	public static String issueReason = "";
	public static String senderName;
	public static String closeIssueID;
	public static String setStatus;
	
	public IssueCommand(cIssueTracker plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		senderName = sender.getName();
		String status = "Open";
		SQLExecutors sqlExec = new SQLExecutors();
		
		if(sender.hasPermission("issuetracker.issue")){
		
		//Triggered when /issue [arg] is typed in any form
		if(commandLabel.equalsIgnoreCase("issue")){
			cIssueTracker tracker = new cIssueTracker();
			//When a player types /issue, /issue ?, /issue help
			if(args.length == 0 || ((args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) && args.length < 2)){
				sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.YELLOW + "IssueTracker Help" + ChatColor.GREEN + "-----");
				sender.sendMessage(ChatColor.YELLOW + "/issue create {Description}     " + ChatColor.BLUE + "Creates an issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue status     " + ChatColor.BLUE + "Displays issue status");
				sender.sendMessage(ChatColor.YELLOW + "/issue close <issueID>     " + ChatColor.BLUE + "Removes or closes an issue");
				if(sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.YELLOW + "Admin Commands" + ChatColor.GREEN + "-----");
					sender.sendMessage(ChatColor.YELLOW + "/issue status <issue_ID> (close/reviewed)     " + ChatColor.BLUE + "Sets status of an issue to close or reviewed");
					sender.sendMessage(ChatColor.YELLOW + "/issue status all     " + ChatColor.BLUE + "Shows all open or reviewed issues");
					
				}
			}
			//When a player types '/issue status' do...
			else if(args[0].equalsIgnoreCase("status") && args.length == 1){
				try {				
					//Calling the SELECT query for status
					sqlExec.statusQuery();
					//Pulls each row of the database. Displays each row
					while (sqlExec.selectSQL.next()) {
						int tempstatus = sqlExec.selectSQL.getInt("status");
						if (tempstatus == 2){
							status = "Reviewed";
						}
			            sender.sendMessage(ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + sqlExec.selectSQL.getString("issue_id") + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + sqlExec.selectSQL.getString("reason"));
			            status = "Open";
			            }
					
					//Close database connection
						sqlExec.dbClose();
					} 	
				catch (SQLException e) {
					e.printStackTrace();
					}
				}
			//Triggers when /issue create [arg] is typed
			else if(args[0].equalsIgnoreCase("create") && args.length >= 2){
				for(int i = 1; i < args.length; i++){
					issueReason += " " + args[i];
				}
				issueReason = issueReason.substring(1);
				try {
					sqlExec.createQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.GREEN + "Your issue has successfully been submitted. A moderator will review it as soon as possible. You may type '/issue status' to view the status of your issues.");
				issueReason = "";
				}
			//Triggers when /issue close is typed
			else if(args[0].equalsIgnoreCase("close") && args.length == 2){
				try {
				closeIssueID = args[1];
				sqlExec.closeQuery();
				sender.sendMessage(ChatColor.GREEN + "You have successfully closed your issue!");
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "You must enter a valid issue ID. Type '/issue status' to view your issues.");
				}
			}
			
			//Admin commands
			else if(args[0].equalsIgnoreCase("status") && args[2].equalsIgnoreCase("close") | args[2].equalsIgnoreCase("closed") | args[2].equalsIgnoreCase("reviewed") && args.length == 3){
				if(sender.hasPermission("issuetracker.admin")){
				closeIssueID = args[1];
				//if '/issue status # close' is typed
					if(args[2].equalsIgnoreCase("close") | args[2].equalsIgnoreCase("closed")){
						setStatus = "3";
						try {
							sqlExec.adminSetQuery();
							sender.sendMessage(ChatColor.GREEN + "Issue is now set as 'closed'.");
						} catch (SQLException e) {
							sender.sendMessage(ChatColor.GOLD + sqlExec.errorString);
						}
						}
					//if '/issue status # reviewed' is typed
					else if (args[2].equalsIgnoreCase("reviewed")){
						setStatus = "2";
						try {
							sqlExec.adminSetQuery();
							sender.sendMessage(ChatColor.GREEN + "Issue is now set as 'reviewed'.");
						} catch (SQLException e) {
							sender.sendMessage(ChatColor.GOLD + sqlExec.errorString);
						}
						}
					else {
						//if the format was wrong
						sender.sendMessage(ChatColor.GOLD + "Please enter an appropriate issue status. (Close or Reviewed");
						}
					
			} //close if permission issuetracker.admin
				
				else{
					//If player does not have permission issuetracker.admin
					sender.sendMessage(ChatColor.GOLD + "You do not have permissions to use this command!");
				}
			} //close if admin types /issue status # <param>
			
			else if(args[0].equalsIgnoreCase("status") && args[1].equalsIgnoreCase("all") && args.length == 2){
				if(sender.hasPermission("issuetracker.admin")){
					//Calling the SELECT query for status
					try {
						sqlExec.adminStatusQuery();
					//Pulls each row of the database. Displays each row
					while (sqlExec.selectSQL.next()) {
						int tempstatus = sqlExec.selectSQL.getInt("status");
						if (tempstatus == 2){
							status = "Reviewed";
						}
			            sender.sendMessage(ChatColor.BLUE + "Player: " + ChatColor.GOLD + sqlExec.selectSQL.getString("player") + ChatColor.BLUE + " - Issue ID: " + ChatColor.GOLD + sqlExec.selectSQL.getString("issue_id") + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + sqlExec.selectSQL.getString("reason"));
			            status = "Open";
			            }
					//Close database connection
					sqlExec.dbClose();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else{
					//If player does not have permission issuetracker.admin
					sender.sendMessage(ChatColor.GOLD + "You do not have permissions to use this command!");
				}
			}
			
			//If none of the triggers are hit - tell them how to view correct syntax
			else {
				sender.sendMessage("Please type /issue for help.");
			}
			
			
		} //close if commandlabel = "issue" 
		} //close if permission issuetracker.issue
		else {
			//If player does not have issuetracker.issue
			sender.sendMessage(ChatColor.GOLD + "You do not have permissions to use this command!");
		}
		return true;
	}
	
	}
