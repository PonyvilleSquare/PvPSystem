package com.hepolite.pvp;

import java.util.HashMap;
import java.util.UUID;

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

public class PvPManager implements Listener, Runnable
{
	private final HashMap<UUID, Account> playerAccounts = new HashMap<UUID, Account>();

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
}
