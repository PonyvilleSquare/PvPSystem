package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pvp.Account;
import com.hepolite.pvp.PvP;

public class CmdPvPToggle implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
			sender.sendMessage(ChatColor.RED + "You may only use this command from a player");
		Account account = PvP.getPvPManager().getAccount((Player) sender);
		account.setPvPFlag(!account.hasPvPOn());
		if (account.hasPvPOn())
			sender.sendMessage("PvP has now been " + ChatColor.RED + " enabled" + ChatColor.WHITE + ".");
		else
			sender.sendMessage("PvP is now " + ChatColor.AQUA + " disabled" + ChatColor.WHITE + ".");
		return true;
	}
}
