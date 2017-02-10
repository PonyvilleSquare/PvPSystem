package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pvp.PvP;

public class CmdSetHome implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Silly user, console is a homeless hobo!");
			return true;
		}
		Player player = (Player) sender;

		if (!PvP.getSettings().homeWorlds.contains(player.getWorld().getName()))
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().sethomeInvalidWorld));
		else if (PvP.getCoreData().isInsideCombatZone(player.getLocation()))
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().sethomeCombatZone));
		else
		{
			PvP.getCoreData().setPlayerHome(player);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().sethomeValid));
		}
		return true;
	}
}
