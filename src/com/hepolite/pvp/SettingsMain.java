package com.hepolite.pvp;

import java.util.HashSet;
import java.util.List;

import com.hepolite.pillar.settings.Settings;

public class SettingsMain extends Settings
{
	public int combatTimer;
	public int teleportTimer;
	public int teleportCooldown;

	public List<Integer> announceTimes;
	public List<Integer> endTimes;

	public String sethomeInvalidWorld;
	public String sethomeCombatZone;
	public String sethomeValid;
	public String homeNoHome;
	public String homeUnsafe;
	public String homeValid;
	public String homeDisrupt;
	public String homeCooldown;
	public String homeTeleporting;
	public String homeHome;
	public HashSet<String> homeWorlds = new HashSet<String>();

	public SettingsMain()
	{
		super(PvP.getInstance(), "settings");
	}

	/** Reads all the various configurations from the config file */
	@Override
	public final void onReload()
	{
		combatTimer = getInt("combatTimer");
		teleportTimer = getInt("teleportTimer");
		teleportCooldown = getInt("teleportCooldown");

		try
		{
			for (String string : getStringList("announceTimes"))
				announceTimes.add(Integer.parseInt(string));
			for (String string : getStringList("endTimes"))
				endTimes.add(Integer.parseInt(string));
		}
		catch (Exception e)
		{
		}

		sethomeInvalidWorld = getString("sethomeInvalidWorld");
		sethomeCombatZone = getString("sethomeCombatZone");
		sethomeValid = getString("sethomeValid");
		homeNoHome = getString("homeNoHome");
		homeUnsafe = getString("homeUnsafe");
		homeValid = getString("homeValid");
		homeDisrupt = getString("homeDisrupt");
		homeCooldown = getString("homeCooldown");
		homeTeleporting = getString("homeTeleporting");
		homeHome = getString("homeHome");

		homeWorlds.clear();
		for (String world : getStringList("setHomeWorlds"))
			homeWorlds.add(world);
	}
}
