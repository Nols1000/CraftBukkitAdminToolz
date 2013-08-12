package dev.bukkit.Nols1000.AdminToolz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

enum VoteType{
	
	KICK, BAN
}

public class Vote{

	private VoteType type;
	private Player player;
	private CommandSender sender;
	
	private String cause = "";
	
	private int time = 100;
	
	private Score pro;
	private Score contra;
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	private Scoreboard voteBoard = manager.getNewScoreboard();
	private Objective voteObjective = voteBoard.registerNewObjective("vote", "dummy");
	
	public Vote(Player player, CommandSender sender, VoteType type){
		
		this.player = player;
		this.sender = sender;
		this.type = type;
	}
	
	public Vote(Player player, CommandSender sender, VoteType type, String cause){
		
		this.player = player;
		this.sender = sender;
		this.type = type;
		this.cause = cause;
	}
	
	public Vote(Player player, CommandSender sender, VoteType type, int time){
		
		this.player = player;
		this.sender = sender;
		this.type = type;
		this.time = time;
	}
	
	public Vote(Player player, CommandSender sender, VoteType type, int time, String cause){
		
		this.player = player;
		this.sender = sender;
		this.type = type;
		this.time = time;
		this.cause = cause;
	}
	
	public void initScoreboard(){
		
		if(type == VoteType.KICK){
			
			voteObjective.setDisplayName("VoteKick: "+ player.getName());
		}else if(type == VoteType.BAN){
			
			voteObjective.setDisplayName("VoteBan: "+ player.getName());
		}
		
		voteObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		pro = voteObjective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN+"#1 Yes"));
		contra = voteObjective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED+"#2 No"));
		
		pro.setScore(0);
		contra.setScore(0);
		
		for(Player online : Bukkit.getOnlinePlayers()){
			
			online.setScoreboard(voteBoard);
		}
	}
	
	public void end(){
		
		voteBoard.clearSlot(DisplaySlot.SIDEBAR);
	}
	
	public void increasePro(){
		
		pro.setScore(getPro()+1);
	}
	
	public void increaseContra(){
		
		contra.setScore(getContra()+1);
	}
	
	public CommandSender getSender(){
		
		return sender;
	}
	
	public int getTime(){
		
		return time;
	}

	public VoteType getType() {
		
		return type;
	}

	public int getPro() {
		
		return pro.getScore();
	}

	public int getContra() {
		
		return contra.getScore();
	}

	public Player getPlayer() {
		
		return player;
	}

	public String getCause() {
		
		return cause;
	}
	
	
}
