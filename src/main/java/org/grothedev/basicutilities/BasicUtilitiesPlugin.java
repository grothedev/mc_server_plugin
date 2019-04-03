package org.grothedev.basicutilities;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

//TODO interface with frogpond (only active at certain world location?)
//TODO message delivery online and offline
	/*on writtenbook, add to db. on player login, check. spawn vill/mule with waypoint */
//TODO something cool with redstone
//TODO remove all near leaves with shears

public class BasicUtilitiesPlugin extends JavaPlugin implements Listener {
	
	Configuration conf;
	Random rand;
	
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
		rand = new Random();
		conf = getConfig();
		updateLocalConfig(conf);
		
		//getDatabase();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		String msg = conf.getString("join_msg");
		if (msg.length() > 0) p.sendMessage(msg);
		
		
		if (conf.getBoolean("maiL_active")) handleMail(p);
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
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		//BukkitTask t = new HandleEvent(event, this).runTask(this);
		Random r = new Random();
		if (Config.FUNGUS_INNOC != 0 && r.nextInt() % Config.FUNGUS_INNOC == 1) {
			if (e.getBucket() == Material.WATER_BUCKET) {
				log("water bucket");
			} 
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		log("block break");
		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
			}
			
		});
	}
	
	@EventHandler
	public void onBlockFertilize(BlockFertilizeEvent e) {
		if (Config.FUNGUS_INNOC != 0 && rand.nextInt() % Config.FUNGUS_INNOC == 1) {
			
		}
	}
	
	//spawn something
	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		
	}
	
	
	//chest: spawn npc behind player when opened
	//
	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
		if (e == null) return;
	}
	
	//do something cool if holding a certain item and maybe only if they have some other item
	//bonemeal: if tree chance mushroom tree, etc.
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		//testing if event handling is on other thread
		/*log("starting sleep");
		try {
			Thread.sleep(10000);
			log("sleep done");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
				
		Random rand = new Random();
		/*
		if (e == null) return;
		if (e.getClickedBlock().getType() == Material.CHEST && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			log(e.getPlayer().getName() + "clicked chest");
		}
		*/
		
		if (e.getItem().getType() == Material.GOLDEN_HOE) {
			e.getPlayer().spawnParticle(Particle.CLOUD, e.getPlayer().getLocation(), rand.nextInt() % 35 + 10);
		}
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		/*  going to go straight from book writing instead of having to put into chest
		 * 
		log("inv move event" + e.getSource().getHolder().toString());
		if (e.getItem().getType() != Material.WRITTEN_BOOK) return;
		if (e.getDestination().getType() == InventoryType.CHEST) {
			log("put written book in chest");
			//check that title of book is a user name
			for (OfflinePlayer p : getServer().getOfflinePlayers()) {
				if (p.getName().equalsIgnoreCase( ((BookMeta) e.getItem()).getTitle())) {
					//add message to the DB
					String msg = ((BookMeta) e.getItem()).getPages().toString();
					log("queue mail delivery to " + p.getName() + ": " + msg);
				}
			}
		}
		*/
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		/*  
		log("inv click event" + e.getWhoClicked().getName());
		if (e.getAction() != InventoryAction.PLACE_ONE && e.getAction() != InventoryAction.PLACE_ALL) return;
		log("inv: " + e.getClickedInventory().getType().toString() + "\n item: " + e.getCurrentItem().toString());
		*/
		
	}
	
	//TODO boat speed when eat fish. requires state system so know when player has eaten and if in boat
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		
		Player p = e.getPlayer();
		
		switch(e.getItem().getType()) {
		case MUSHROOM_STEW:
			if (rand.nextInt() % Config.MUSHROOM_COW == 1) {
				log("mooshroom visiting " + p.getName());
				int spawnRad = 8;
				Location loc = p.getLocation().subtract(p.getLocation().getDirection().multiply(6));
				Entity cow = p.getWorld().spawnEntity(loc, EntityType.MUSHROOM_COW);
				if (!cow.isOnGround()) {
					cow.teleport(loc.add(new Location(cow.getWorld(), rand.nextInt()%spawnRad - spawnRad/2, 1, rand.nextInt()%spawnRad - spawnRad/2)));
				}
			}
			if (rand.nextInt() % 10 == Config.MUSHROOM_TRIP) {
				log(p.getName() + " ate some magic mushroom stew");
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, rand.nextInt() % 20, rand.nextInt() % 8 + 2, true));
			}
			break;
		case TROPICAL_FISH:
			if (rand.nextInt() % Config.FISH_BOAT_SPEED == 1) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, rand.nextInt() % 5, rand.nextInt() % 5));
				if (p.isInsideVehicle()) {
					
				}
			}
			break;
		case APPLE:
			if (rand.nextInt() % 10 == 1) {
				p.setGravity(false);
			}
		}
		
		
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		//BukkitTask t = new HandleEvent(e, this).runTask(this);
		
		
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity ent = (LivingEntity) e.getEntity();
			Random r = new Random();
			switch (ent.getType()) {
				case ZOMBIE:
					if (r.nextInt() % Config.ZOMBIE_GIANT == 1) {
						ent.getWorld().spawnEntity(ent.getLocation(), EntityType.GIANT);
						ent.remove();
						
					}
					break;
			}
		}
	
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		//LivingEntity ent = e.getEntity();
		//BukkitTask handleEntityDeath = new EntityDeathHandler(e).runTask(this);
		//BukkitTask t = new HandleEvent(e, this).runTask(this);
		
		LivingEntity ent = e.getEntity();
		Random r = new Random();
		switch (ent.getType()) {
			case CREEPER:
				if (r.nextInt() % Config.CREEPER_FIREWORK == 1)
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.FIREWORK_STAR, r.nextInt() % 3 + 1));
				if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.CREEPER_HEAD, 1));
				}
				if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				}
				break;
			case ZOMBIE:
				if (r.nextInt() % Config.ZOMBIE_GIANT == 1) {
					ent.getWorld().spawnEntity(ent.getLocation(), EntityType.GIANT);
					ent.remove();
					
				}
				if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.ZOMBIE_HEAD, 1));
				}
				if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				}
				break;
			case GIANT:
				if (r.nextInt() % Config.GIANT_VILLAGER == 1) {
					ent.getWorld().spawnEntity(ent.getLocation(), EntityType.VILLAGER);
				}
				break;
			case SKELETON:
				if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SKELETON_SKULL, 1));
				}
				if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				}
				break;
			case ENDERMAN:
				if (e.getDrops().size() == 0 || e.getDrops().get(0).getType() != Material.ENDER_PEARL) {
					ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
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

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		if (e.getEntered() instanceof Player) {
			Player p = (Player) e.getEntered();
			if (p.getInventory().contains(Material.LEATHER)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50, 2));
			}
		}
	}
	
	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent e) {
		if (e == null) {
			log("book edit event null");
			return;
		}
		Player from = e.getPlayer();
		String toStr = e.getNewBookMeta().getTitle().toString();
		OfflinePlayer to = getOfflinePlayerByString(toStr);
		if (to == null) {
			from.sendMessage("that player was never on");
			return;
		} else {
			from.sendMessage("found the player");
		}
		//add to DB
		Entity villager = from.getWorld().spawnEntity(from.getLocation().add(5, 1, 5), EntityType.VILLAGER );
		((Villager) villager).setTarget(from);
	}
	
	private void log(String msg) {
		getLogger().info(msg);
	}
	
	private Player getOnlinePlayerByString(String name) {
		if (name == null) return null;
		for (Player p : getServer().getOnlinePlayers()) {
			//getServer().broadcastMessage(p.getName());
			if (p.getName().equals(name)) return p;
		}
		return null;
	}
	
	private OfflinePlayer getOfflinePlayerByString(String name) {
		if (name == null) return null;
		for (OfflinePlayer p : getServer().getOfflinePlayers()) {
			if (p.getName() == null) continue;
			//getServer().broadcastMessage(p.getName());
			//log("name = " + name);
			//log("p = " + p.getName());
			if (p.getName().equals(name)) return p;
		}
		return null;
	}
	
	private void updateLocalConfig(Configuration cfg) {
		Config.MAIL_ACTIVE = cfg.getBoolean("mail_active");
		Config.MUSHROOM_COW = cfg.getInt("mushroom_cow"); //these ints are the reciprical of the probability that the event will occur
		Config.MUSHROOM_TRIP = cfg.getInt("mushroom_trip");
		Config.ZOMBIE_GIANT = cfg.getInt("zombie_giant");
		Config.FISH_BOAT_SPEED = cfg.getInt("fish_boat_speed");
		Config.CREEPER_FIREWORK = cfg.getInt("creeper_firework");
		Config.GIANT_VILLAGER = cfg.getInt("giant_villager");
		Config.DROP_HEADS = cfg.getBoolean("drop_heads");
		Config.DROP_SLIMES = cfg.getInt("drop_slimes");
		Config.FUNGUS_INNOC = cfg.getInt("fungus_innoc");
	}
	
	class EntityDeathHandler extends BukkitRunnable{

		EntityDeathEvent e;
		
		public EntityDeathHandler(EntityDeathEvent event) {
			this.e = event;
		}
		
		@Override
		public void run() {
			LivingEntity ent = e.getEntity();
			Random r = new Random();
			switch (ent.getType()) {
				case CREEPER:
					if (r.nextInt() % Config.CREEPER_FIREWORK == 1)
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.FIREWORK_STAR, r.nextInt() % 3 + 1));
					if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.CREEPER_HEAD, 1));
					}
					if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
					}
					break;
				case ZOMBIE:
					if (r.nextInt() % Config.ZOMBIE_GIANT == 1) {
						ent.getWorld().spawnEntity(ent.getLocation(), EntityType.GIANT);
						ent.remove();
						
					}
					if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.ZOMBIE_HEAD, 1));
					}
					if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
					}
					break;
				case GIANT:
					if (r.nextInt() % Config.GIANT_VILLAGER == 1) {
						ent.getWorld().spawnEntity(ent.getLocation(), EntityType.VILLAGER);
					}
					break;
				case SKELETON:
					if (Config.DROP_HEADS && r.nextInt() % 1000 == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SKELETON_SKULL, 1));
					}
					if (Config.DROP_SLIMES != 0 && r.nextInt()%Config.DROP_SLIMES == 1) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
					}
					break;
				case ENDERMAN:
					if (e.getDrops().size() == 0 || e.getDrops().get(0).getType() != Material.ENDER_PEARL) {
						ent.getWorld().dropItem(ent.getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
					}
					break;
			}
		}
		
	}
}
