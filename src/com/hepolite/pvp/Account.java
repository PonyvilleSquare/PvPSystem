package com.hepolite.pvp;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.hepolite.pvp.towny.TownyHelper;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class Account
{
	private final UUID uuid;

	private boolean allowPvP;
	private boolean insidePvPRegion;
	private int combatTimer = -1;
	private int teleportTimer = -1;

	public Account(Player player)
	{
		this.uuid = player.getUniqueId();
		this.allowPvP = false;
		this.insidePvPRegion = false;
	}

	/** Invoked once every second */
	public final void onTick()
	{
		Player player = Bukkit.getPlayer(uuid);
		if (player == null)
			return;
		handleZoneTransitions();
		combatTimer--;

		if (--teleportTimer == 0)
		{
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeHome));
			player.teleport(PvP.getCoreData().getPlayerHome(player));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0f, 0.0f);
		}
	}

	/** Handles changing various sections */
	private final void handleZoneTransitions()
	{
		Player player = Bukkit.getPlayer(uuid);
		boolean isInZone = PvP.getCoreData().isInsideCombatZone(player.getLocation());
		if (isInZone && !insidePvPRegion)
		{
			insidePvPRegion = true;
			player.sendMessage(ChatColor.RED + "You entered a combat zone. Enabled forced PvP");
		}
		else if (!isInZone && insidePvPRegion)
		{
			insidePvPRegion = false;
			if (combatTimer > 0)
				player.sendMessage(ChatColor.RED + "You left a combat zone, but you are still in a fight!");
			else
				player.sendMessage(ChatColor.AQUA + "You left a combat zone. Disabled forced PvP");
		}
		if (!insidePvPRegion && combatTimer == 0)
			player.sendMessage(ChatColor.AQUA + "Your fight timed out and you are outside combat zones. Disabled forced PvP");
	}

	/** Sets the PvP flag; if true, the player may be attacked by others */
	public final void setPvPFlag(boolean flag)
	{
		this.allowPvP = flag;
	}

	/** Returns true if others may attack this player */
	public final boolean hasPvPOn()
	{
		return allowPvP;
	}

	/** Returns true if the player has is either active in PvP, or inside combat regions */
	public final boolean isPvPActive()
	{
		return insidePvPRegion || combatTimer > 0;
	}

	/** Returns true if the account is allied with the other account */
	public final boolean isAlly(Account account)
	{
		Town town = TownyHelper.getPlayerTown(uuid);
		Town otherTown = TownyHelper.getPlayerTown(account.uuid);
		if (town == null || otherTown == null)
			return false;
		if (town.getName().equals(otherTown.getName()))
			return true;

		Nation nation = TownyHelper.getPlayerNation(uuid);
		Nation otherNation = TownyHelper.getPlayerNation(account.uuid);
		if (nation == null || otherNation == null)
			return false;
		return (nation.getName().equals(otherNation.getName()) || nation.hasAlly(otherNation));
	}

	/** Invokes a combat action, such as either taking damage or attacking someone */
	public void invokeAttack()
	{
		invokeAttack(PvP.getSettings().combatTimer);
		setTeleportTime(0);
	}

	/** Invokes a combat action, such as either taking damage or attacking someone; sets the duration of the PvP combat time in ticks as well */
	public void invokeAttack(int duration)
	{
		if (combatTimer <= -1)
		{
			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				player.sendMessage(ChatColor.RED + String.format("You have been tagged for %d seconds!", duration));
		}
		combatTimer = duration;
	}

	/** Sets the teleporting time of the player; set to 0 to prevent teleporting; time is measured in ticks */
	public final void setTeleportTime(int time)
	{
		if (teleportTimer > 0 && time == 0)
		{
			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeDisrupt));
			teleportTimer = 0;
		}
		if (teleportTimer <= 0 && time > 0)
			teleportTimer = time;
	}

	/** Returns the teleport time of the player */
	public final int getTeleportTime()
	{
		return teleportTimer;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Account)
			return uuid.equals(((Account) object).uuid);
		return false;
	}
}
