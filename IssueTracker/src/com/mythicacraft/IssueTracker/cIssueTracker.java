package com.mythicacraft.IssueTracker;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mythicacraft.IssueTrackerExecutors.IssueCommand;
import com.mythicacraft.IssueTrackerExecutors.SQLExecutors;


public class cIssueTracker extends JavaPlugin{
	public static cIssueTracker tracker;
	public final Logger logger = Logger.getLogger("Minecraft");
	SQLExecutors sqlExec = new SQLExecutors();
	
	public static String sqlHost;
	public static String sqlPort;
	public static String sqlUser;
	public static String sqlPass;
	public static String sqlDbase;
	
//**********ENABLE/DISABLE METHODS**************
	public void onEnable() {
		this.logMessage("Enabled.");
		
		//*******CREATES PLUGIN DIRECTORY AND TXT STORAGE FILE*********
		String pluginFolder = this.getDataFolder().getAbsolutePath();
		(new File(pluginFolder)).mkdirs();
		File file = new File(getDataFolder() + File.separator + "config.yml");
		
		//Creates default config and disables the plugin until valid data is input
		if (!file.exists()){
			this.getLogger().info("Generating config.yml");
			this.getConfig().addDefault("MySql.host", "hostname");
			this.getConfig().addDefault("MySql.port", "3306");
			this.getConfig().addDefault("MySql.user", "username");
			this.getConfig().addDefault("MySql.pass", "password");
			this.getConfig().addDefault("MySql.dbase", "database");
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
			logger.severe(String.format("[IssueTracker] - Disabled due to no database information configured!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
			}
		//If config exists but has not been edited
		else if (file.exists() && this.getConfig().getString("MySql.host") == "hostname"){
			logger.severe(String.format("[IssueTracker] - Default configuration detected. Please configure your database information!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
		}
		
		//Fills SQL connection variables
		else {
			getDBInfo();
		}
		
		//Checks for database table itrack_issuetracker
		try {
			sqlExec.CreateTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//prepares the /issue commands
		this.getCommand("issue").setExecutor(new IssueCommand(this));
	}

	//Disables the plugin
	public void onDisable() {
		this.logMessage("Disabled.");
		}
	

	
	//Logger method
	public void logMessage(String load){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is " + load);
		}
	
	//Pulls database information from the config
	public void getDBInfo() {
		sqlHost = this.getConfig().getString("MySql.host");
		sqlPort = this.getConfig().getString("MySql.port");
		sqlUser = this.getConfig().getString("MySql.user");
		sqlPass = this.getConfig().getString("MySql.pass");
		sqlDbase = this.getConfig().getString("MySql.dbase");
		}

	
	}


