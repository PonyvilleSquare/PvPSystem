package com.hepolite.pvp.cappoints;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.hepolite.pillar.utility.EntityHelper;
import com.hepolite.pvp.PvP;
import com.hepolite.pvp.towny.TownyHelper;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class CapturePointManager implements Listener
{
	/** Event used to allow players to capture control points */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerBreakBlockInitial(BlockBreakEvent event)
	{
		if (event.getBlock().getType() != Material.STANDING_BANNER)
			return;
		CapturePoint point = getPoint(event.getBlock().getLocation());
		if (point == null || point.getOwner() == null)
			return;
		event.setCancelled(false);
	}
	
	/** Event used to allow players to capture control points */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBreakBlockFinal(BlockBreakEvent event)
	{
		if (event.getBlock().getType() != Material.STANDING_BANNER)
			return;
		CapturePoint point = getPoint(event.getBlock().getLocation());
		if (point == null || point.getOwner() == null)
			return;

		broadcast(point.getOwner(), point.getLocation(), String.format("&c%s&f is no longer controlled by &c%s&f!", point.getName(), point.getOwner()));
		point.setOwner(null);
	}

	/** Event used to allow players to capture control points */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerPlaceBlockInitial(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player == null || event.getBlock().getType() != Material.STANDING_BANNER)
			return;
		CapturePoint point = getPoint(event.getBlock().getLocation());
		if (point == null)
			return;
		Town town = TownyHelper.getPlayerTown(player);
		if (town == null)
		{
			player.sendMessage(ChatColor.RED + "You have to be part of a town to capture points!");
			event.setCancelled(true);
			return;
		}
		Entry<DyeColor, List<Pattern>> pattern = PvP.getCoreData().getBannerPattern(town);
		if (pattern == null || pattern.getKey() == null || pattern.getValue() == null || pattern.getValue().size() == 0)
		{
			player.sendMessage(ChatColor.RED + "Your town must define a banner in order to capture points!");
			player.sendMessage(ChatColor.RED + "Please use /psb while holding a custom banner with patterns");
			event.setCancelled(true);
			return;
		}

		if (point.getOwner() == null)
		{
			Banner banner = (Banner) event.getBlock().getState();
			banner.setBaseColor(pattern.getKey());
			banner.setPatterns(pattern.getValue());
			banner.update(true);
			event.setCancelled(false);
		}
		else if (point.getOwner().equals(town.getName()))
		{
			player.sendMessage(ChatColor.RED + "You cannot capture your own point!");
			event.setCancelled(true);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You cannot capture this point while another town has claimed it!");
			event.setCancelled(true);
		}
	}
	
	/** Event used to allow players to capture control points */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPlaceBlockFinal(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player == null || event.getBlock().getType() != Material.STANDING_BANNER)
			return;
		CapturePoint point = getPoint(event.getBlock().getLocation());
		if (point == null)
			return;
		if (point.getOwner() == null)
		{
			Town town = TownyHelper.getPlayerTown(player);
			broadcast(town.getName(), point.getLocation(), String.format("&c%s&f is now under control of &c%s&f!", point.getName(), town.getName()));
			point.setOwner(town.getName());
		}
	}

	/** Returns the control point at the given location, if any */
	private final CapturePoint getPoint(Location location)
	{
		for (CapturePoint point : PvP.getCoreData().getPoints())
		{
			if (point.isInside(location))
				return point;
		}
		return null;
	}

	/** Broadcasts a message to all members of the given town, and all nearby players */
	private final void broadcast(String townName, Location location, String message)
	{
		List<Player> players = EntityHelper.getPlayersInRange(location, 100.0f);
		Town town = TownyHelper.getTown(townName);
		if (town != null)
		{
			for (Resident resident : town.getResidents())
			{
				@SuppressWarnings("deprecation")
				Player player = Bukkit.getPlayerExact(resident.getName());
				if (player != null && !players.contains(player))
					players.add(player);
			}
		}
		for (Player player : players)
		{
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			player.playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 2.0f);
		}
	}
}
