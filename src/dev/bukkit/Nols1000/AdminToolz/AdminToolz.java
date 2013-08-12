package dev.bukkit.Nols1000.AdminToolz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class AdminToolz extends JavaPlugin {

	private Logger logger;
	private FileConfiguration config;
	
	private boolean votekickEnabled = false;
	private boolean votebanEnabled = false;
	
	private VoteManager voteManager;
	
	private List<CommandSender> admins;
	
	@Override
	public void onEnable(){
		
		logger = this.getLogger();
		config = this.getConfig();
		
		votekickEnabled = config.getBoolean("vote.kick.enabled");
		votebanEnabled = config.getBoolean("vote.ban.enabled");
		
		voteManager = new VoteManager(this);
		voteManager.start();
		
		admins = getAdmins();
		
		getCommand("vote").setExecutor(voteManager);
		
		logger.log(Level.INFO, "AdminToolz is enabled.");
	}
	
	@Override
	public void onDisable(){
		
		logger.log(Level.INFO, "AdminToolz is disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(label.equalsIgnoreCase("admin")){
			
			getAdmins();
			
			Iterator<CommandSender> admins = this.admins.iterator();
			
			if(args.length > 0){
				
				String msg = sender.getName()+" | "+args[0];
				
				if(args.length > 1){
					
					for(int i = 1; args.length > i; i++){
						
						msg += " "+args[i];
					}
				}
				
				while(admins.hasNext()){
					
					admins.next().sendMessage(msg);
				}
				
				sender.sendMessage("@admin | "+msg);
				
				return true;
			}else{
				
				sender.sendMessage("You have to type a text ;D");
			}
		}
		
		if(label.equalsIgnoreCase("re")){
			
			if(!getAdmins().contains(sender)){
				
				return false;
			}
			
			if(args.length > 1){
				
				String msg = sender.getName()+" | "+args[1];
				
				if(args.length > 2){
					
					for(int i = 2; args.length > i; i++){
						
						msg += " "+args[i];
					}
				}
				
				Player player = getServer().getPlayer(args[0]);
				
				if(!player.isOnline()){
					
					return false;
				}
				
				player.sendMessage(msg);
				
				sender.sendMessage("@"+args[0]+" | "+msg);
				
				return true;
			}else{
				
				sender.sendMessage("You have to type a text ;D");
			}
		}
		
		if(label.equalsIgnoreCase("votekick")){
			
			if(!votekickEnabled){
				
				sender.sendMessage("VoteKick is disabled.");
				
				return false;
			}
			
			if(args.length > 0){
				
				Player pl =  getServer().getPlayer(args[0]);
				
				if(pl != null && pl.isOnline()){
				
					if(args.length == 1){
						
						initVotekick(sender, pl);
					}
								
					if(args.length > 1){
					
						String cause = args[1];
						
						if(args.length > 2){
							
							for(int i = 2; args.length > i; i++){
							
								cause += " "+args[i];
							}
						}
						
						initVotekick(sender, pl, cause);
					}
				}
				
				return true;
			}
		}
		
		if(label.equalsIgnoreCase("voteban")){
			
			if(!votebanEnabled){
				
				sender.sendMessage("VoteBan is disabled.");
				
				return false;
			}
			
			if(args.length > 0){
				
				Player pl =  getServer().getPlayer(args[0]);
				
				if(pl != null && pl.isOnline()){
				
					if(args.length == 1){
						
						initVoteban(sender, pl);
					}
								
					if(args.length > 1){
					
						String cause = args[1];
						
						if(args.length > 2){
							
							for(int i = 2; args.length > i; i++){
							
								cause += " "+args[i];
							}
						}
						
						initVoteban(sender, pl, cause);
					}
				}
				
				return true;
			}
		}
		
		if(label.equalsIgnoreCase("at")){
			
			if(args.length > 0){
				
				if(args.length == 1){
					
					if(args[0].equalsIgnoreCase("reload")){
						
						reload();
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private void reload(){
		
		getAdmins();		
	}
	
	private void initVotekick(CommandSender sender, Player pl) {
		
		int time = getConfig().getInt("vote.kick.time")*1000;
		
		voteManager.addVote(pl, sender, VoteType.KICK, time);
	}

	private void initVotekick(CommandSender sender, Player pl, String cause) {
		
		int time = getConfig().getInt("vote.kick.time")*1000;
		
		voteManager.addVote(pl, sender, VoteType.KICK, time, cause);
	}
	
	private void initVoteban(CommandSender sender, Player pl) {
		
		int time = getConfig().getInt("vote.ban.time")*1000;
		
		voteManager.addVote(pl, sender, VoteType.BAN, time);
	}

	private void initVoteban(CommandSender sender, Player pl, String cause) {
	
		int time = getConfig().getInt("vote.ban.time")*1000;
		
		voteManager.addVote(pl, sender, VoteType.BAN, time, cause);
	}
	
	private List<CommandSender> getAdmins() {
		
		List<CommandSender> list = new ArrayList<CommandSender>();	
		
		list.add(this.getServer().getConsoleSender());
		
		boolean isOpAdmin = this.getConfig().getBoolean("admin.op");
		Player[] onlinePlayer = this.getServer().getOnlinePlayers();
		
		
		for(int i = 0; onlinePlayer.length > i; i++){
			
			if(isOpAdmin){
				
				if(onlinePlayer[i].hasPermission("AdminToolz.admin") || onlinePlayer[i].isOp()){
					
					list.add(onlinePlayer[i]);
				}
			}else{
				
				if(onlinePlayer[i].hasPermission("AdminToolz.admin")){
					
					list.add(onlinePlayer[i]);
				}
			}
		}
		
		return list;
	}
}
