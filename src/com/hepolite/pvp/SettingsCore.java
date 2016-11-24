package com.hepolite.pvp;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hepolite.pillar.settings.Settings;
import com.hepolite.pvp.cappoints.CapturePoint;
import com.hepolite.pvp.towny.TownyHelper;
import com.palmergames.bukkit.towny.object.Town;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SettingsCore extends Settings
{
	private HashMap<String, List<ProtectedRegion>> zones;
	private TreeMap<String, CapturePoint> points;
	private HashMap<String, Entry<DyeColor, List<Pattern>>> patterns;

	public SettingsCore()
	{
		super(PvP.getInstance(), "core");
	}

	/** Reads all the various configurations from the config file */
	@Override
	public final void onReload()
	{
		loadZones();
		loadPoints();
		loadPatterns();
	}

	/** Loads up the zones */
	private final void loadZones()
	{
		this.zones = new HashMap<String, List<ProtectedRegion>>();
		for (String key : getKeys("zones"))
		{
			World world = Bukkit.getWorld(key);
			if (world == null)
			{
				PvP.getInstance().getLogger().info("Failed to parse world '" + key + "'");
				continue;
			}

			List<ProtectedRegion> zones = new LinkedList<ProtectedRegion>();
			for (String zone : getStringList("zones." + key))
			{
				ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(world).getRegion(zone);
				if (region == null)
					PvP.getInstance().getLogger().info("Failed to parse region '" + zone + "' under world '" + key + "'");
				else
					zones.add(region);
			}
			this.zones.put(key, zones);
		}
	}

	/** Load up capture points */
	private final void loadPoints()
	{
		this.points = new TreeMap<String, CapturePoint>();

		for (String key : getKeys("points"))
		{
			Location location = parseSimpleLocation(key);
			if (location == null || location.getWorld() == null)
			{
				PvP.getInstance().getLogger().info("Failed to parse location '" + key + "'");
				continue;
			}
			String path = "points." + key + ".";

			String name = getString(path + "name");
			CapturePoint point = new CapturePoint(name, location);
			if (has(path + "owner"))
				point.setOwner(getString(path + "owner"));
			this.points.put(point.getName(), point);
		}
	}

	/** Loads up all the patterns from the config file */
	private final void loadPatterns()
	{
		this.patterns = new HashMap<String, Entry<DyeColor, List<Pattern>>>();

		for (String key : getKeys("patterns"))
		{
			Town town = TownyHelper.getTown(key);
			if (town == null)
			{
				PvP.getInstance().getLogger().info("Failed to parse town '" + key + "'");
				continue;
			}

			String baseColorString = getString("patterns." + key + ".base");
			DyeColor baseColor = DyeColor.BLACK;
			try
			{
				baseColor = DyeColor.valueOf(baseColorString);
			}
			catch (Exception e)
			{
				PvP.getInstance().getLogger().info("Failed to parse base color '" + baseColorString + "'");
				continue;
			}

			List<Pattern> pattern = new LinkedList<Pattern>();
			for (String string : getStringList("patterns." + key + ".pattern"))
			{
				try
				{
					String[] parts = string.split("=");
					pattern.add(new Pattern(DyeColor.valueOf(parts[1]), PatternType.getByIdentifier(parts[0])));
				}
				catch (Exception e)
				{
					PvP.getInstance().getLogger().info("Failed to parse pattern string '" + string + "'");
					continue;
				}
			}

			patterns.put(town.getName(), new AbstractMap.SimpleEntry<DyeColor, List<Pattern>>(baseColor, pattern));
		}
	}

	// //////////////////////////////////////////////////////////////////////

	/** Returns true if the given location is inside a combat region */
	public final boolean isInsideCombatZone(Location location)
	{
		List<ProtectedRegion> zones = PvP.getCoreData().zones.get(location.getWorld().getName());
		if (zones == null)
			return false;
		Vector v = location.toVector();

		for (ProtectedRegion zone : zones)
		{
			BlockVector max = zone.getMaximumPoint();
			BlockVector min = zone.getMinimumPoint();
			if (!(v.getX() < min.getX() || v.getX() > max.getX() || v.getY() < min.getY() || v.getY() > max.getY() || v.getZ() < min.getZ() || v.getZ() > max.getZ()))
				return true;
		}
		return false;
	}

	// //////////////////////////////////////////////////////////////////////

	/** Stores the given point owner into the config */
	public final void setPointOwner(CapturePoint point, String owner)
	{
		set("points." + writeSimpleLocation(point.getLocation()) + ".owner", owner);
		save();
	}

	/** Returns all the points in the system, sorted in alphabetical order */
	public final Collection<CapturePoint> getPoints()
	{
		return points.values();
	}

	/** Returns the points controlled by the given town */
	public final Set<CapturePoint> getPointsOwnedBy(Town town)
	{
		Set<CapturePoint> set = new TreeSet<CapturePoint>();
		if (town == null)
			return set;
		for (CapturePoint point : points.values())
		{
			if (point.getOwner() != null && point.getOwner().equals(town.getName()))
				set.add(point);
		}
		return set;
	}

	/** Returns the points under the given zone */
	public final Set<CapturePoint> getPointsInZone(String zone)
	{
		zone = zone.toLowerCase();
		Set<CapturePoint> set = new TreeSet<CapturePoint>();
		for (CapturePoint point : points.values())
		{
			if (point.getName().toLowerCase().startsWith(zone))
				set.add(point);
		}
		return set;
	}

	/** Adds a new zone to the system */
	public final void addPoint(CapturePoint point)
	{
		points.put(point.getName(), point);
		set("points." + writeSimpleLocation(point.getLocation()) + ".name", point.getName());
		save();
	}

	/** Removes a zone from the system */
	public final void removePoint(CapturePoint point)
	{
		points.remove(point.getName());
		remove("points." + writeSimpleLocation(point.getLocation()));
		save();
	}

	/** Adds a new zone to the system */
	public final void addZone(World world, ProtectedRegion region)
	{
		if (world == null || region == null)
			return;
		if (!zones.containsKey(world.getName()))
			zones.put(world.getName(), new LinkedList<ProtectedRegion>());
		if (zones.get(world.getName()).contains(region))
			return;
		zones.get(world.getName()).add(region);

		List<String> list = getStringList("zones." + world.getName());
		list.add(region.getId());
		set("zones." + world.getName(), list);
		save();
	}

	/** Removes a zone from the system */
	public final void removeZone(World world, ProtectedRegion region)
	{
		if (world == null || region == null || !zones.containsKey(world.getName()))
			return;
		zones.get(world.getName()).remove(region);

		List<String> list = getStringList("zones." + world.getName());
		list.remove(region.getId());
		set("zones." + world.getName(), list);
		save();
	}

	/** Sets the banner pattern for the given town */
	public final void setBannerPattern(Town town, DyeColor baseColor, List<Pattern> pattern)
	{
		List<String> patternStrings = new LinkedList<String>();
		for (Pattern p : pattern)
			patternStrings.add(p.getPattern().getIdentifier() + "=" + p.getColor().name());
		if (baseColor == null)
			baseColor = DyeColor.BLACK;

		set("patterns." + town.getName() + ".base", baseColor.name());
		set("patterns." + town.getName() + ".pattern", patternStrings);

		patterns.put(town.getName(), new AbstractMap.SimpleEntry<DyeColor, List<Pattern>>(baseColor, pattern));
		save();
	}

	/** Returns the banner pattern for the given town */
	public final Entry<DyeColor, List<Pattern>> getBannerPattern(Town town)
	{
		return patterns.get(town.getName());
	}

	/** Check if any existing banners already exists with the given settings */
	public final boolean bannerExists(DyeColor baseColor, List<Pattern> pattern)
	{
		for (Entry<DyeColor, List<Pattern>> entry : patterns.values())
		{
			if (!baseColor.equals(entry.getKey()))
				return false;

			List<Pattern> current = entry.getValue();
			if (pattern.size() != current.size())
				return false;

			for (int i = 0; i < pattern.size(); i++)
			{
				Pattern patternA = pattern.get(i);
				Pattern patternB = current.get(i);
				if (!patternA.getColor().equals(patternB.getColor()) || !patternA.getPattern().equals(patternB.getPattern()))
					return false;
			}
		}
		return true;
	}

	// //////////////////////////////////////////////////////////////////////

	/** Assigns the home for the given player */
	public final void setPlayerHome(Player player)
	{
		set("homes." + player.getUniqueId().toString(), writeSimpleLocation(player.getLocation()));
		save();
	}

	/** Returns the home for the given player, or null if it didn't exist */
	public final Location getPlayerHome(Player player)
	{
		String path = "homes." + player.getUniqueId().toString();
		return has(path) ? parseSimpleLocation(getString(path)) : null;
	}
}
