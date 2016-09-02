package com.hepolite.pvp.towny;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyHelper
{
	/** Returns the residence object that is associated with the player, if it exists */
	public final static Resident getResident(UUID uuid)
	{
		return getResident(Bukkit.getPlayer(uuid));
	}

	/** Returns the residence object that is associated with the player, if it exists */
	public final static Resident getResident(Player player)
	{
		try
		{
			return TownyUniverse.getDataSource().getResident(player.getName());
		}
		catch (Exception e)
		{
		}
		return null;
	}

	/** Returns the town with the given name */
	public final static Town getTown(String name)
	{
		try
		{
			return TownyUniverse.getDataSource().getTown(name);
		}
		catch (NotRegisteredException e)
		{
		}
		return null;
	}

	/** Returns the town that a player is part of; return null if the player isn't part of any town */
	public final static Town getPlayerTown(UUID uuid)
	{
		return getPlayerTown(Bukkit.getPlayer(uuid));
	}

	/** Returns the town that a player is part of; return null if the player isn't part of any town */
	public final static Town getPlayerTown(Player player)
	{
		try
		{
			return getResident(player).getTown();
		}
		catch (Exception e)
		{
		}
		return null;
	}

	/** Returns the nation the player is part of; returns null if the player isn't part of any nation */
	public final static Nation getPlayerNation(UUID uuid)
	{
		return getPlayerNation(Bukkit.getPlayer(uuid));
	}

	/** Returns the nation the player is part of; returns null if the player isn't part of any nation */
	public final static Nation getPlayerNation(Player player)
	{
		try
		{
			return getPlayerTown(player).getNation();
		}
		catch (Exception e)
		{
		}
		return null;
	}
}
