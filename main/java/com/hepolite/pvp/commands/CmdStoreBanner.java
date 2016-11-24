package com.hepolite.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import com.hepolite.pvp.PvP;
import com.hepolite.pvp.towny.TownyHelper;
import com.palmergames.bukkit.towny.object.Town;

public class CmdStoreBanner implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "This command may only be used by a player");
			return true;
		}
		Player player = (Player) sender;
		Town town = TownyHelper.getPlayerTown(player);
		if (town == null)
		{
			sender.sendMessage(ChatColor.RED + "You must be part of a town to do this");
			return true;
		}
		ItemStack banner = player.getInventory().getItemInMainHand();
		if (banner.getType() != Material.BANNER)
		{
			sender.sendMessage(ChatColor.RED + "You must hold a banner to do this");
			return true;
		}
		BannerMeta meta = (BannerMeta) banner.getItemMeta();
		@SuppressWarnings("deprecation")
		DyeColor baseColor = (meta.getBaseColor() != null ? meta.getBaseColor() : DyeColor.getByDyeData((byte) banner.getDurability()));

		if (baseColor == null || meta.getPatterns() == null || meta.getPatterns().size() == 0)
			player.sendMessage(ChatColor.RED + "The banner is too simple to be used");
		else if (PvP.getCoreData().bannerExists(baseColor, meta.getPatterns()))
			player.sendMessage(ChatColor.RED + "The banner is already in use by a town!");
		else
		{
			PvP.getCoreData().setBannerPattern(town, baseColor, meta.getPatterns());
			player.sendMessage(ChatColor.WHITE + "The banner for the town " + ChatColor.RED + town.getName() + ChatColor.WHITE + " has been set");
		}
		return true;
	}
}
