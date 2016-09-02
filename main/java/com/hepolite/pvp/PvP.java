package com.hepolite.pvp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.hepolite.pvp.cappoints.CapturePointManager;
import com.hepolite.pvp.commands.CmdAddPoint;
import com.hepolite.pvp.commands.CmdAddZone;
import com.hepolite.pvp.commands.CmdDisplayPoints;
import com.hepolite.pvp.commands.CmdForcePvPState;
import com.hepolite.pvp.commands.CmdHome;
import com.hepolite.pvp.commands.CmdSetHome;
import com.hepolite.pvp.commands.CmdPvPToggle;
import com.hepolite.pvp.commands.CmdReload;
import com.hepolite.pvp.commands.CmdRemovePoint;
import com.hepolite.pvp.commands.CmdRemoveZone;
import com.hepolite.pvp.commands.CmdResetPoint;
import com.hepolite.pvp.commands.CmdStoreBanner;

public class PvP extends JavaPlugin
{
	private static PvP instance;
	private SettingsMain settings;
	private SettingsCore coreData;
	private PvPManager pvpManager;
	private CapturePointManager capturePointManager;

	@Override
	public void onEnable()
	{
		instance = this;
		settings = new SettingsMain();
		coreData = new SettingsCore();
		settings.initialize();
		coreData.initialize();
		pvpManager = new PvPManager();
		Bukkit.getPluginManager().registerEvents(pvpManager, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, pvpManager, 0, 20);
		capturePointManager = new CapturePointManager();
		Bukkit.getPluginManager().registerEvents(capturePointManager, this);

		getCommand("pvptoggle").setExecutor(new CmdPvPToggle());
		getCommand("pvpreload").setExecutor(new CmdReload());
		getCommand("pvpforce").setExecutor(new CmdForcePvPState());
		getCommand("pvpshowpoints").setExecutor(new CmdDisplayPoints());
		getCommand("pvpaddpoint").setExecutor(new CmdAddPoint());
		getCommand("pvprempoint").setExecutor(new CmdRemovePoint());
		getCommand("pvprespoint").setExecutor(new CmdResetPoint());
		getCommand("pvpsetbanner").setExecutor(new CmdStoreBanner());
		getCommand("pvpaddzone").setExecutor(new CmdAddZone());
		getCommand("pvpremzone").setExecutor(new CmdRemoveZone());
		getCommand("pvpsethome").setExecutor(new CmdSetHome());
		getCommand("pvphome").setExecutor(new CmdHome());
	}

	@Override
	public void onDisable()
	{
	}

	/** Returns the PvP plugin instance */
	public final static PvP getInstance()
	{
		return instance;
	}

	/** Returns the settings */
	public final static SettingsMain getSettings()
	{
		return instance.settings;
	}
	
	/** Returns the core data */
	public final static SettingsCore getCoreData()
	{
		return instance.coreData;
	}

	/** Returns the PvP manager */
	public final static PvPManager getPvPManager()
	{
		return instance.pvpManager;
	}

	/** Returns the capture point manager */
	public final static CapturePointManager getCapturePointManager()
	{
		return instance.capturePointManager;
	}
}
