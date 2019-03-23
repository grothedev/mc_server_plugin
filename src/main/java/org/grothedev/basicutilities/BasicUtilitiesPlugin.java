package org.grothedev.basicutilities;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

//TODO implement event listeners
//TODO interface with frogpond (only active at certain world location?)
//TODO message delivery online and offline
	/*on writtenbook, add to db. on player login, check. spawn vill/mule with waypoint */
//TODO something cool with redstone
//TODO remove all near leaves with shears

public class BasicUtilitiesPlugin extends JavaPlugin implements Listener {
	@Override
	public void onDisable() {
		super.onDisable();
		HandlerList.unregisterAll((Plugin)this);
		
	}

	@Override
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		String msg = getConfig().getString("join_msg");
		if (msg.length() == 0) return;
		
		p.sendMessage(msg);
		//p.beginConversation(this, getServer().getConsoleSender(), (Prompt) (new MessagePrompt()));
		//p.sendRawMessage("[plugin test] welcome");
		
		handleMail(p);
	}
	
	//TODO implement better command processing utilities
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().charAt(0) == 'f') {
			if (args[0].equalsIgnoreCase("mcow")) {
				if (args[1].length() > 0) {
					Player p = getOnlinePlayerByString(args[1]);
					p.getWorld().spawnEntity(p.getLocation(), EntityType.MUSHROOM_COW);
				}
			}
			return true;
		} 
		return false; 
	}
	
	//chance of being innoculated with fungus spores, then water spawns something every once in a while, or something grows on surrounding blocks
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		
	}
	
	//spawn something
	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		
	}
	
	
	//chest: spawn npc behind player when opened
	//
	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
		
	}
	
	//do something cool if holding a certain item and maybe only if they have some other item
	//bonemeal: if tree chance mushroom tree, etc.
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		if (e == null) return;
		if (e.getClickedBlock().getType() == Material.CHEST && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			log(e.getPlayer().getName() + "clicked chest");
		}
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		if (e.getItem().getType() != Material.WRITTEN_BOOK) return;
		if (e.getDestination().getType() == InventoryType.CHEST) {
			//check that title of book is a user name
			for (OfflinePlayer p : getServer().getOfflinePlayers()) {
				if (p.getName().equalsIgnoreCase( ((BookMeta) e.getItem()).getTitle())) {
					//add message to the DB
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		Random rand = new Random();
		if (rand.nextInt() % 5 != 1) return;
		if (e.getItem().getType() == Material.MUSHROOM_STEW) {
			Player p = e.getPlayer();
			int spawnRad = 8;
			Location loc = p.getLocation().subtract(p.getLocation().getDirection().multiply(6));
			Entity cow = p.getWorld().spawnEntity(loc, EntityType.MUSHROOM_COW);
			if (!cow.isOnGround()) {
				cow.teleport(loc.add(new Location(cow.getWorld(), rand.nextInt()%spawnRad - spawnRad/2, 1, rand.nextInt()%spawnRad - spawnRad/2)));
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity ent = e.getEntity();
		Random r = new Random();
		switch (ent.getType()) {
		case CREEPER:
			if (r.nextInt() % 14 == 1)
				ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.FIREWORK_STAR, r.nextInt() % 3 + 1));
			break;
		case ZOMBIE:
			if (r.nextInt() %35 == 1) {
				ent.getWorld().spawnEntity(ent.getLocation(), EntityType.GIANT);
				ent.remove();
				
			}
			break;
		case GIANT:
			if (r.nextInt() % 6 == 1) {
				ent.getWorld().spawnEntity(ent.getLocation(), EntityType.VILLAGER);
			}
			break;
		}
	}
	
	
	
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.REDSTONE_WIRE || b.getType() == Material.REDSTONE_TORCH || b.getType() == Material.REPEATER) return;
		if (b.getType() == Material.CHEST) {
			log(Integer.toString(e.getNewCurrent()));
			Entity villager = b.getWorld().spawnEntity(e.getBlock().getLocation().add(0, 1, 0), EntityType.VILLAGER );
			//villager.setTarget()  recipient player  which is read from book
			Inventory i = ((InventoryHolder) b).getInventory();
			for (ItemStack thing : i) {
				if (thing.getType() == Material.WRITTEN_BOOK) {
					log(thing.toString());
					//get the recipient and do what needs to be done
				}
			}
			
		} else if (b.getType() == Material.COBBLESTONE || b.getType() == Material.STONE) {
			log(e.getBlock().getType().toString());
		}
		log(b.toString());
	}
	
	private void handleMail(Player p) {
		
	}
	
	//PlayerPortalEvent touching portal. play music
	//Block events: keep track of environment changes, derive mathematical model to trigger relevant events
	//PlayerBedEnterEvent : sleep paralysis, 
	//WeatherEvent : amplify
	//consume mushroom soup : something cool

	
	
	private void log(String msg) {
		getLogger().info(msg);
	}
	
	private Player getOnlinePlayerByString(String name) {
		if (name == null) return null;
		for (Player p : getServer().getOnlinePlayers()) {
			if (p.getName().equals(name)) return p;
		}
		return null;
	}
}
