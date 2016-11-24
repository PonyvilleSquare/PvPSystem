package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.hepolite.pvp.PvP;

public class CmdReload implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		PvP.getSettings().reload();
		sender.sendMessage(ChatColor.AQUA + "Reloaded the configs");
		return true;
	}
}
