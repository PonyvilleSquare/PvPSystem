package com.hepolite.pvp.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.base.Joiner;
import com.hepolite.pvp.PvP;
import com.hepolite.pvp.cappoints.CapturePoint;
import com.hepolite.pvp.towny.TownyHelper;
import com.palmergames.bukkit.towny.object.Town;

public class CmdDisplayPoints implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO: Collapse points that are controlled by only one point (Say all mill points -> Mill is controlled by name)

		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Checking all points...");
			boolean anyPointIsControlled = false;
			for (CapturePoint point : PvP.getCoreData().getPoints())
			{
				if (point.getOwner() != null)
				{
					anyPointIsControlled = true;
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f belongs to &c%s&f", point.getName(), point.getOwner())));
				}
				else
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f is not controlled by anyone", point.getName())));
			}
			if (!anyPointIsControlled)
				sender.sendMessage(ChatColor.RED + "No town controls any control points!");
		}
		else
		{
			String townName = Joiner.on(' ').join(args);
			Town town = TownyHelper.getTown(townName);
			if (town != null)
			{
				if (PvP.getCoreData().getPointsOwnedBy(town).size() == 0)
					sender.sendMessage(ChatColor.RED + town.getName() + ChatColor.WHITE + " does not control any points");
				else
					sender.sendMessage(ChatColor.RED + town.getName() + ChatColor.WHITE + " owns the following points:");
				for (CapturePoint point : PvP.getCoreData().getPointsOwnedBy(town))
					sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.RED + point.getName());
			}
			else
			{
				Set<CapturePoint> points = PvP.getCoreData().getPointsInZone(townName);
				if (points.size() == 0)
					sender.sendMessage(ChatColor.RED + "Could not find any town or zone named " + townName);
				else
				{
					sender.sendMessage(ChatColor.RED + "Control points under " + townName + ":");
					for (CapturePoint point : points)
					{
						if (point.getOwner() == null)
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f is not controlled by anyone", point.getName())));
						else
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("- &c%s&f is controlled by &c%s&f", point.getName(), point.getOwner())));
					}
				}
			}
		}
		return true;
	}
}
