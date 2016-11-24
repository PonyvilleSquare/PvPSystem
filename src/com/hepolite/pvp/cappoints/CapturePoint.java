package com.hepolite.pvp.cappoints;

import org.bukkit.Location;

import com.hepolite.pvp.PvP;

public class CapturePoint implements Comparable<CapturePoint>
{
	private final String name;
	private final Location location;
	private String owner;

	public CapturePoint(String name, Location location)
	{
		this.name = name;
		this.location = location;
	}

	/** Returns true if the given location is inside the capture point */
	public final boolean isInside(Location location)
	{
		if (!this.location.getWorld().getName().equals(location.getWorld().getName()))
			return false;
		return this.location.distance(location) < 2.0;
	}

	/** Returns the name of the point */
	public final String getName()
	{
		return name;
	}

	/** Returns the location of the capture point */
	public final Location getLocation()
	{
		return location;
	}

	/** Returns who controls the control point */
	public final String getOwner()
	{
		return owner;
	}

	/** Sets the town that controls this control point */
	public final void setOwner(String owner)
	{
		this.owner = owner;
		PvP.getCoreData().setPointOwner(this, owner);
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof CapturePoint))
			return false;
		CapturePoint o = (CapturePoint) other;
		return name.equals(o.name);
	}

	@Override
	public int compareTo(CapturePoint other)
	{
		return name.compareTo(other.name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
