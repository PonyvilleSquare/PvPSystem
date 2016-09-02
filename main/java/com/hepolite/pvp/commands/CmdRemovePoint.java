package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pvp.PvP;
import com.hepolite.pvp.cappoints.CapturePoint;

public class CmdRemovePoint implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player) && args.length < 4)
		{
			sender.sendMessage(ChatColor.RED + "This command may only be used by a player");
			return false;
		}

		if (args.length >= 2 && (args[0].startsWith("n") || args[0].startsWith("N")))
		{
			String name = get(args, 1);
			for (CapturePoint point : PvP.getCoreData().getPoints())
			{
				if (point.getName().equalsIgnoreCase(name))
				{
					PvP.getCoreData().removePoint(point);
					sender.sendMessage(ChatColor.RED + "Removed point " + ChatColor.WHITE + point.getName());
					return true;
				}
			}
			sender.sendMessage(ChatColor.RED + "Was unable to find point with name " + ChatColor.WHITE + name);
			return true;
		}

		Location location;
		if (args.length >= 4)
			location = PvP.getCoreData().parseSimpleLocation(args[0] + "=" + args[1] + "=" + args[2] + "=" + args[3]);
		else
			location = ((Player) sender).getLocation().subtract(0.5, 0.0, 0.5);
		if (location == null)
		{
			sender.sendMessage(ChatColor.RED + "Was unable to parse the location");
			return true;
		}

		for (CapturePoint point : PvP.getCoreData().getPoints())
		{
			if (point.isInside(location))
			{
				PvP.getCoreData().removePoint(point);
				sender.sendMessage(ChatColor.RED + "Removed point " + ChatColor.WHITE + point.getName() + ChatColor.RED + " at location " + ChatColor.WHITE + String.format("%s: %.0f,%.0f,%.0f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
				return true;
			}
		}
		sender.sendMessage(ChatColor.RED + "Found no points at location " + ChatColor.WHITE + String.format("%s: %.0f,%.0f,%.0f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
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
