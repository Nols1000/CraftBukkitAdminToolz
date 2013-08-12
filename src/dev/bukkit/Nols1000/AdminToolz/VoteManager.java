package dev.bukkit.Nols1000.AdminToolz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteManager extends Thread implements CommandExecutor {
	
	private List<Vote> votes = new ArrayList<Vote>();
	private List<CommandSender> voted = new ArrayList<CommandSender>();
	private Vote currentVote;
	
	private boolean started = false;
	
	private JavaPlugin plugin;
	
	Timer timer = new Timer();
	
	public VoteManager(JavaPlugin plugin){
		
		this.plugin = plugin;
	}
	
	public void addVote(Player player, CommandSender sender, VoteType type, int time){
		
		votes.add(new Vote(player, sender, type, time));
		
		if(!started){
			
			try {
				
				startVote();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
		sender.sendMessage(new String[]{ChatColor.RED+"You have started a vote against: "+ player.getName(), ChatColor.RED+"If you want to cancel please performe the command: /vote 3"});
	}
	
	public void addVote(Player player, CommandSender sender, VoteType type, int time, String cause){
		
		votes.add(new Vote(player, sender, type, time, cause));
		
		if(!started){
			
			try {
				
				startVote();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
		}
		
		sender.sendMessage(new String[]{ChatColor.RED+"You have started a vote against: "+ player.getName(), ChatColor.RED+"If you want to cancel please performe the command: /vote 3"});
	}
	
	@Override
	public void run(){
		
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if(label.equalsIgnoreCase("vote")){
			
			if(currentVote != null){
			
				if(args.length == 1){	
		
					if(args[0].equalsIgnoreCase("1")){
				
						if(!voted.contains(sender)){
					
							currentVote.increasePro();
							voted.add(sender);
					
							sender.sendMessage("You have voted against "+currentVote.getPlayer().getName());
						}else{
					
							sender.sendMessage("You've voted already.");
						}
				
						return true;
					}
		
					if(args[0].equalsIgnoreCase("2")){
				
						if(!voted.contains(sender)){
					
							currentVote.increaseContra();
							voted.add(sender);
					
							sender.sendMessage("You have voted for "+currentVote.getPlayer().getName());
						}else{
					
							sender.sendMessage("You've voted already.");
						}
					
						return true;
					}
		
					if(args[0].equalsIgnoreCase("3")){
			
						Iterator<Vote> arg0 = votes.iterator(); 
					
						while(arg0.hasNext()){
					
							Vote arg1 = arg0.next();
							
							if(arg1.getSender() == sender){
						
								sender.sendMessage("Vote was canceld.");
						
								if(arg1 == currentVote){
							
									sendMsgAtAll("Vote was canceld by initiator.");
									started = false;

									currentVote.end();
								}
						
								votes.remove(arg1);
								
								try {
									
									startVote();
								} catch (InterruptedException e) {
									
									e.printStackTrace();
								}
								
								return true;
							}
						}
					}
				}else{
					
					System.out.println("Error: currentVote == null.");
				}
			}
		}
		
		return false;
	}
	
	private void startVote() throws InterruptedException{
		
		timer = new Timer();
		
		if(votes.size() > 0){
			
			started = true;
			
			currentVote = votes.get(0);
			
			currentVote.initScoreboard();
			
			if(currentVote.getType() == VoteType.KICK){
			
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.kick.msg.init"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
				sendMsgAtAll("/vote 1 - Yes");
				sendMsgAtAll("/vote 2 - No");
			}else if(currentVote.getType() == VoteType.BAN){
				
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.ban.msg.init"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
				sendMsgAtAll("/vote 1 - Yes");
				sendMsgAtAll("/vote 2 - No");
			}
			
			timer.schedule(
			        
				new TimerTask() {
					
					public void run() {
						
						try {
							
							endVote();
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
			        }
			     }, currentVote.getTime());
		}else{
			
			started = false;
		}
	}
	
	private void endVote() throws InterruptedException{
		
		voted.clear();
		
		timer.cancel();
		
		if(currentVote.getType() == VoteType.KICK){
			
			int difference = currentVote.getPro() - currentVote.getContra();
			
			System.out.println("D: "+difference);
			System.out.println("C: "+plugin.getConfig().getInt("vote.kick.difference"));
			
			if(difference > plugin.getConfig().getInt("vote.kick.difference")){
				
				currentVote.getPlayer().kickPlayer("voteKick: "+ currentVote.getCause());
				
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.kick.msg.succeed"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
			}else{
				
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.kick.msg.failed"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
			}
			
		}else if(currentVote.getType() == VoteType.BAN){
			
			int difference = currentVote.getPro() - currentVote.getContra();
			
			System.out.println("D: "+difference);
			System.out.println("C: "+plugin.getConfig().getInt("vote.ban.difference"));
			
			if(difference > plugin.getConfig().getInt("vote.ban.difference")){
				
				currentVote.getPlayer().kickPlayer("VoteBan: "+ currentVote.getCause());
				currentVote.getPlayer().setBanned(true);
				
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.ban.msg.succeed"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
			}else{
				
				sendMsgAtAll(makeMsg(plugin.getConfig().getString("vote.ban.msg.failed"), currentVote.getPlayer().getName(), currentVote.getSender().getName(), currentVote.getCause()));
			}
		}
		
		currentVote.end();
		
		votes.remove(currentVote);
		
		currentVote = null;
		
		startVote();
	}
	
	private void sendMsgAtAll(String msg){
		
		Player[] onlinePlayer = plugin.getServer().getOnlinePlayers();
		
		for(int i = 0; onlinePlayer.length > i; i++){
			
			onlinePlayer[i].sendMessage(msg);
		}
	}
	
	@SuppressWarnings("unused")
	private void sendMsgAtAll(String[] msg){
		
		Player[] onlinePlayer = plugin.getServer().getOnlinePlayers();
		
		for(int i = 0; onlinePlayer.length > i; i++){
			
			onlinePlayer[i].sendMessage(msg);
		}
	}
	
	private String makeMsg(String msg, String player, String sender, String cause){
		
		if(msg.contains("{Player}")){
			
			msg = msg.replace("{Player}", player);
		}
		
		if(msg.contains("{Sender}")){
			
			msg = msg.replace("{Sender}", sender);
		}

		if(msg.contains("{Cause}")){
			
			msg = msg.replace("{Cause}", cause);
		}
		
		return msg;
	}
}
