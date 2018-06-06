package com.eroelf.javaxsx.util.geo;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Provides APIs for converting coordinates under China geographic coordinate reference systems.
 * 
 * @author weikun.zhong
 * @see <a href="http://my.oschina.net/Thinkeryjgfn/blog/402565">http://my.oschina.net/Thinkeryjgfn/blog/402565</a>
 */
public class CoordinateConverter
{
	private static final double x_pi=Math.PI*3000.0/180.0;

	/**
	 * WGS84 to GCJ02
	 * 
	 * @param wgs84Lat
	 * @param wgs84Lgt
	 * @return
	 */
	public static Coordinate fromWGS84ToGCJ02(double wgs84Lat, double wgs84Lgt)
	{
		if(isOutOfChina(wgs84Lat, wgs84Lgt))
			return new Coordinate(wgs84Lat, wgs84Lgt);
		else
		{
			double dLat=transformLat(wgs84Lgt-105.0, wgs84Lat-35.0);
			double dLgt=transformLgt(wgs84Lgt-105.0, wgs84Lat-35.0);
			double radLat=wgs84Lat/180.0*Math.PI;
			double magic=Math.sin(radLat);
			magic=1-GeoConst.ee*magic*magic;
			double sqrtMagic=Math.sqrt(magic);
			dLat=(dLat*180.0)/((GeoConst.a*(1-GeoConst.ee))/(magic*sqrtMagic)*Math.PI);
			dLgt=(dLgt*180.0)/(GeoConst.a/sqrtMagic*Math.cos(radLat)*Math.PI);
			return new Coordinate(wgs84Lat+dLat, wgs84Lgt+dLgt);
		}
	}

	/**
	 * WGS84 to GCJ02
	 * 
	 * @param wgs84LatLgt
	 * @return
	 */
	public static Coordinate fromWGS84ToGCJ02(Coordinate wgs84LatLgt)
	{
		return fromWGS84ToGCJ02(wgs84LatLgt.x, wgs84LatLgt.y);
	}

	/**
	 * WGS84 to BD09
	 * 
	 * @param wgs84Lat
	 * @param wgs84Lgt
	 * @return
	 */
	public static Coordinate fromWGS84ToBD09(double wgs84Lat, double wgs84Lgt)
	{
		return fromGCJ02ToBD09(fromWGS84ToGCJ02(wgs84Lat, wgs84Lgt));
	}

	/**
	 * WGS84 to BD09
	 * 
	 * @param wgs84LatLgt
	 * @return
	 */
	public static Coordinate fromWGS84ToBD09(Coordinate wgs84LatLgt)
	{
		return fromWGS84ToBD09(wgs84LatLgt.x, wgs84LatLgt.y);
	}

	/**
	 * GCJ02 to WGS84
	 * 
	 * @param gcj02Lat
	 * @param gcj02Lgt
	 * @return
	 */
	public static Coordinate fromGCJ02ToWGS84(double gcj02Lat, double gcj02Lgt)
	{
		if(isOutOfChina(gcj02Lat, gcj02Lgt))
			return new Coordinate(gcj02Lat, gcj02Lgt);
		else
		{
			double dLat=transformLat(gcj02Lgt-105.0, gcj02Lat-35.0);
			double dLgt=transformLgt(gcj02Lgt-105.0, gcj02Lat-35.0);
			double radLat=gcj02Lat/180.0*Math.PI;
			double magic=Math.sin(radLat);
			magic=1-GeoConst.ee*magic*magic;
			double sqrtMagic=Math.sqrt(magic);
			dLat=gcj02Lat+(dLat*180.0)/((GeoConst.a*(1-GeoConst.ee))/(magic*sqrtMagic)*Math.PI);
			dLgt=gcj02Lgt+(dLgt*180.0)/(GeoConst.a/sqrtMagic*Math.cos(radLat)*Math.PI);
			return new Coordinate(gcj02Lat*2-dLat, gcj02Lgt*2-dLgt);
		}
	}

	/**
	 * GCJ02 to WGS84
	 * 
	 * @param gcj02LatLgt
	 * @return
	 */
	public static Coordinate fromGCJ02ToWGS84(Coordinate gcj02LatLgt)
	{
		return fromGCJ02ToWGS84(gcj02LatLgt.x, gcj02LatLgt.y);
	}

	/**
	 * GCJ02 to BD09
	 * 
	 * @param gcj02Lat
	 * @param gcj02Lgt
	 * @return
	 */
	public static Coordinate fromGCJ02ToBD09(double gcj02Lat, double gcj02Lgt)
	{
		double z=Math.sqrt(gcj02Lgt*gcj02Lgt+gcj02Lat*gcj02Lat)+0.00002*Math.sin(gcj02Lat*x_pi);
		double theta=Math.atan2(gcj02Lat, gcj02Lgt)+0.000003*Math.cos(gcj02Lgt*x_pi);
		return new Coordinate(z*Math.sin(theta)+0.006, z*Math.cos(theta)+0.0065);
	}

	/**
	 * GCJ02 to BD09
	 * 
	 * @param gcj02LatLgt
	 * @return
	 */
	public static Coordinate fromGCJ02ToBD09(Coordinate gcj02LatLgt)
	{
		return fromGCJ02ToBD09(gcj02LatLgt.x, gcj02LatLgt.y);
	}

	/**
	 * BD09 to GCJ02
	 * 
	 * @param bd09Lat
	 * @param bd09Lgt
	 * @return
	 */
	public static Coordinate fromBD09ToGCJ02(double bd09Lat, double bd09Lgt)
	{
		double x=bd09Lgt-0.0065, y=bd09Lat-0.006;
		double z=Math.sqrt(x*x+y*y)-0.00002*Math.sin(y*x_pi);
		double theta=Math.atan2(y, x)-0.000003*Math.cos(x*x_pi);
		return new Coordinate(z*Math.sin(theta), z*Math.cos(theta));
	}

	/**
	 * BD09 to GCJ02
	 * 
	 * @param bd09LatLgt
	 * @return
	 */
	public static Coordinate fromBD09ToGCJ02(Coordinate bd09LatLgt)
	{
		return fromBD09ToGCJ02(bd09LatLgt.x, bd09LatLgt.y);
	}

	/**
	 * BD09 to WGS84
	 * 
	 * @param bd09Lat
	 * @param bd09Lgt
	 * @return
	 */
	public static Coordinate fromBD09ToWGS84(double bd09Lat, double bd09Lgt)
	{
		return fromGCJ02ToWGS84(fromBD09ToGCJ02(bd09Lat, bd09Lgt));
	}

	/**
	 * BD09 to WGS84
	 * 
	 * @param bd09LatLgt
	 * @return
	 */
	public static Coordinate fromBD09ToWGS84(Coordinate bd09LatLgt)
	{
		return fromBD09ToWGS84(bd09LatLgt.x, bd09LatLgt.y);
	}

	/**
	 * If the given latitude and longitude is out of China.
	 * 
	 * @param wgs84Lat
	 * @param wgs84Lgt
	 * @return
	 */
	public static boolean isOutOfChina(double wgs84Lat, double wgs84Lgt)
	{
		return (wgs84Lat<0.8293 || wgs84Lat>55.8271 || wgs84Lgt<72.004 || wgs84Lgt>137.8347);
	}

	/**
	 * If the given coordinate is out of China.
	 * 
	 * @param wgs84LatLgt
	 * @return
	 */
	public static boolean isOutOfChina(Coordinate wgs84LatLgt)
	{
		return isOutOfChina(wgs84LatLgt.x, wgs84LatLgt.y);
	}

	private static double transformLat(double x, double y)
	{
		return -100.0+2.0*x+3.0*y+0.2*y*y+0.1*x*y+0.2*Math.sqrt(Math.abs(x))
			   +(20.0*Math.sin(6.0*x*Math.PI)+20.0*Math.sin(2.0*x*Math.PI)
			   +20.0*Math.sin(y*Math.PI)+40.0*Math.sin(y/3.0*Math.PI)
			   +160.0*Math.sin(y/12.0*Math.PI)+320*Math.sin(y*Math.PI/30.0))*2.0/3.0;
	}

	private static double transformLgt(double x, double y)
	{
		return 300.0+x+2.0*y+0.1*x*x+0.1*x*y+0.1*Math.sqrt(Math.abs(x))
			   +(20.0*Math.sin(6.0*x*Math.PI)+20.0*Math.sin(2.0*x*Math.PI)
			   +20.0*Math.sin(x*Math.PI)+40.0*Math.sin(x/3.0*Math.PI)
			   +150.0*Math.sin(x/12.0*Math.PI)+300.0*Math.sin(x/30.0*Math.PI))*2.0/3.0;
	}

	private CoordinateConverter()
	{}
}
