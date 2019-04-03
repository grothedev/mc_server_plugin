package org.grothedev.basicutilities;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HandleEvent extends BukkitRunnable{

	//made this class before i realized that event threads are already dealt with on spigot (thought it was causing too much lag)
	//leaving it incase i want to use it later. not going to use it because it introduces performance hinderences. 
	
	Event event;
	JavaPlugin plugin;
	
	public HandleEvent(Event e, JavaPlugin p) {
		this.event = e;
		this.plugin = p;
	}
	
	@Override
	public void run() {
		
		log("test");
		if (event instanceof EntityDeathEvent) entityDeath();
		if (event instanceof EntitySpawnEvent) entitySpawn();
		if (event instanceof BlockBreakEvent) blockBreak();
		if (event instanceof PlayerBucketEmptyEvent) playerBucketEmpty(); 
		if (event instanceof BlockBreakEvent) blockBreak();
	}

	private void blockBreak() {
		BlockBreakEvent e = (BlockBreakEvent) event;
		
		

		Block b = e.getBlock();
		Player p = e.getPlayer();
		switch(b.getType()) {
		case COAL_ORE:
			//p.setMetadata("mined_coal", p.getMetadata("mined_coal").get(0).asInt() + 1);
		}
	}
	
	private void entitySpawn() {
		EntitySpawnEvent e = (EntitySpawnEvent) event;
		if (e instanceof LivingEntity) {
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
	
	private void entityDeath() {
		EntityDeathEvent e = (EntityDeathEvent) event;
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
	
	public void playerBucketEmpty() {
		
		Random r = new Random();
		if (Config.FUNGUS_INNOC != 0 && r.nextInt() % Config.FUNGUS_INNOC == 1) {
			
		}
	}
	
	private void log(String msg) {
		plugin.getLogger().info(msg);
	}
}
