package com.hepolite.pvp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.hepolite.pvp.cappoints.CapturePoint;

public class PvPManager implements Listener, Runnable
{
	private final HashMap<UUID, Account> playerAccounts = new HashMap<UUID, Account>();
	
	private int announcementCountdownTimer = -1;

	/** Returns the account associated with the given player */
	public final Account getAccount(Player player)
	{
		if (player == null)
			return null;
		UUID uuid = player.getUniqueId();
		if (playerAccounts.containsKey(uuid))
			return playerAccounts.get(uuid);
		Account account = new Account(player);
		playerAccounts.put(uuid, account);
		return account;
	}

	/** Invoked once every second */
	@Override
	public void run()
	{
		for (Account account : playerAccounts.values())
			account.onTick();
		
		announcementCountdownTimer--;
		if (announcementCountdownTimer == 0)
		{
			boolean anyPointIsControlled = false;
			for (CapturePoint point : PvP.getCoreData().getPoints())
			{
				if (point.getOwner() != null)
				{
					anyPointIsControlled = true;
					Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f belongs to &c%s&f", point.getName(), point.getOwner())));
				}
				else
					Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f is not controlled by anyone", point.getName())));
			}
			if (!anyPointIsControlled)
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "No town controls any control points!");
		}
		else if (announcementCountdownTimer == 60 * 1)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in one minute!");
		else if (announcementCountdownTimer == 60 * 5)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in five minutes!");
		else if (announcementCountdownTimer == 60 * 15)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in fifteen minutes!");
		else if (announcementCountdownTimer == 60 * 30)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in thirty minutes!");
		else if (announcementCountdownTimer == 60 * 60)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in one hour!");
		else if (announcementCountdownTimer == 60 * 90)
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Capture points will be tallied in one and a half hour!");
	}

	/** Event used to prevent players from being able to hurt each other in certain cases */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		getAccount(event.getPlayer());
	}

	/** Event used to prevent players from being able to hurt each other in certain cases */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerAttackPlayer(EntityDamageByEntityEvent event)
	{
		Player attacker = getAttacker(event);
		if (attacker == null || !(event.getEntity() instanceof Player))
			return;
		Player target = (Player) event.getEntity();

		Account attackerAccount = getAccount(attacker);
		Account targetAccount = getAccount(target);
		if (attackerAccount.equals(targetAccount))
			return;

		if ((attackerAccount.hasPvPOn() && targetAccount.hasPvPOn()) || (attackerAccount.isPvPActive() && targetAccount.isPvPActive() && !attackerAccount.isAlly(targetAccount)))
		{
			if (!attackerAccount.isAlly(targetAccount))
			{
				attackerAccount.invokeAttack();
				targetAccount.invokeAttack();
			}
			return;
		}
		event.setCancelled(true);
	}

	/** Returns a player object from the event, if a player was attacking */
	private final Player getAttacker(EntityDamageByEntityEvent event)
	{
		Entity attacker = event.getDamager();
		if (attacker instanceof Projectile)
		{
			ProjectileSource source = ((Projectile) attacker).getShooter();
			if (source instanceof LivingEntity)
				attacker = (Entity) source;
		}
		return (attacker instanceof Player ? (Player) attacker : null);
	}
	
	/** Starts the capture point countdown timer; will count from 1.5 hours */
	public final void startAnnouncementTimer()
	{
		announcementCountdownTimer = 60 * 90 + 1;
	}
}
