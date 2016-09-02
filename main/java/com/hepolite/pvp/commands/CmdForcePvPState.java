package com.hepolite.pvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pvp.PvP;

public class CmdForcePvPState implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// TODO: Allow the forced on time to be arbitrarily set
		
		if (args.length < 2)
			return false;
		boolean state;
		try
		{
			state = Boolean.parseBoolean(args[1]);
		}
		catch (Exception e)
		{
			return false;
		}

		if (args[0].equals("*"))
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (state)
					player.sendMessage(ChatColor.RED + "Your PvP state was enabled");
				else
					player.sendMessage(ChatColor.AQUA + "Your PvP state was disabled");
				PvP.getPvPManager().getAccount(player).setPvPFlag(state);
			}
		}
		else
		{
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null)
			{
				sender.sendMessage(ChatColor.RED + "The player '" + args[0] + "' couldn't be found");
				return true;
			}
			else
			{
				if (state)
					player.sendMessage(ChatColor.RED + "Your PvP state was enabled");
				else
					player.sendMessage(ChatColor.AQUA + "Your PvP state was disabled");
				PvP.getPvPManager().getAccount(player).setPvPFlag(state);
			}
		}
		return true;
	}
}
