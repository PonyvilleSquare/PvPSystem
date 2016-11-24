package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.hepolite.pvp.PvP;

public class CmdStartTimers implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		sender.sendMessage(ChatColor.RED + "Starting combat timer!");
		PvP.getPvPManager().startAnnouncementTimer();
		return true;
	}
}
