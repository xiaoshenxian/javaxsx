package com.eroelf.javaxsx.util.geo;

import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;

/**
 * Some APIs to deal with geographic information.
 * 
 * @author weikun.zhong
 */
public class GeoUtil
{
	public static double distance(double fromLat, double fromLgt, double toLat, double toLgt)
	{
		return distance(new Coordinate(fromLat, fromLgt), new Coordinate(toLat, toLgt));
	}

	public static double distance(Coordinate fromLatLgt, Coordinate toLatLgt)
	{
		GeodeticCalculator geodeticCalculator=new GeodeticCalculator();
		geodeticCalculator.setStartingGeographicPoint(fromLatLgt.y, fromLatLgt.x);
		geodeticCalculator.setDestinationGeographicPoint(toLatLgt.y, toLatLgt.x);
		return geodeticCalculator.getOrthodromicDistance();
	}

	public static double getLatitudeDifferentialFromDistanceDifferential(Coordinate currLatLgt, double latDis)
	{
		return getLatitudeDifferentialFromDistanceDifferential(currLatLgt.x, currLatLgt.y, latDis);
	}

	public static double getLongitudeDifferentialFromDistanceDifferential(Coordinate currLatLgt, double lgtDis)
	{
		return getLongitudeDifferentialFromDistanceDifferential(currLatLgt.x, currLatLgt.y, lgtDis);
	}

	public static Coordinate getLatLgtDifferentialFromDistanceDifferential(Coordinate currLatLgt, double latDis, double lgtDis)
	{
		return getLatLgtDifferentialFromDistanceDifferential(currLatLgt.x, currLatLgt.y, latDis, lgtDis);
	}

	public static double getLatitudeDifferentialFromDistanceDifferential(double currLat, double currLgt, double latDis)
	{
		return latDis/GeoConst.a/Math.PI*180;
	}

	public static double getLongitudeDifferentialFromDistanceDifferential(double currLat, double currLgt, double lgtDis)
	{
		return Math.abs(currLat)<90 ? lgtDis/GeoConst.a/Math.cos(currLat/180*Math.PI)/Math.PI*180 : 360;
	}

	public static Coordinate getLatLgtDifferentialFromDistanceDifferential(double currLat, double currLgt, double latDis, double lgtDis)
	{
		return new Coordinate(getLatitudeDifferentialFromDistanceDifferential(currLat, currLgt, latDis), getLongitudeDifferentialFromDistanceDifferential(currLat, currLgt, lgtDis));
	}

	private GeoUtil()
	{}
}
