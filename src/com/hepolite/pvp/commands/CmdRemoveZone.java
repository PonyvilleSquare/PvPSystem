package com.hepolite.pvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.hepolite.pvp.PvP;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemoveZone implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length < 2)
			return false;

		World world = Bukkit.getWorld(args[0]);
		if (world == null)
		{
			sender.sendMessage(ChatColor.RED + "Was unable to locate a world with the name " + args[0] + "!");
			return true;
		}
		String zone = get(args, 1);
		ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(world).getRegion(zone);
		if (region == null)
		{
			sender.sendMessage(ChatColor.RED + "Was unable to locate a region with name " + zone + " in world " + world.getName() + "!");
			return true;
		}
		PvP.getCoreData().removeZone(world, region);
		sender.sendMessage(ChatColor.WHITE + "Removed zone " + ChatColor.RED + region.getId() + ChatColor.WHITE + " in world " + ChatColor.RED + world.getName() + ChatColor.WHITE + " from the system");
		return true;
	}

	private final String get(String[] args, int start)
	{
		if (args.length <= start)
			return "";
		String string = args[start];
		for (int i = start + 1; i < args.length; i++)
			string += " " + args[i];
		return string;
	}
}
