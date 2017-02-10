package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pvp.Account;
import com.hepolite.pvp.PvP;

public class CmdHome implements CommandExecutor
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
		Account account = PvP.getPvPManager().getAccount(player);
		Location location = PvP.getCoreData().getPlayerHome(player);
		if (location == null)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeNoHome));
		else if (!PvP.getSettings().homeWorlds.contains(player.getWorld().getName()))
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().sethomeInvalidWorld));
		else if (PvP.getCoreData().isInsideCombatZone(player.getLocation()))
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().sethomeCombatZone));
		else if (account.getTeleportTime() > 0)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeTeleporting));
		else if (account.getTeleportTime() > -PvP.getSettings().teleportCooldown)
		{
			int time = account.getTeleportTime() + PvP.getSettings().teleportCooldown;
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeCooldown).replaceAll("&time&", Integer.toString(time)));
		}
		else
		{
			Block block = location.getBlock().getRelative(0, 1, 0);
			if (block.isEmpty() && block.getRelative(0, 1, 0).isEmpty())
			{
				account.setTeleportTime(PvP.getSettings().teleportTimer);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeValid));
			}
			else
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', PvP.getSettings().homeUnsafe));
		}
		return true;
	}
}
