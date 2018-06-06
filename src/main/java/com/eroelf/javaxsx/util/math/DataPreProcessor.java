package com.eroelf.javaxsx.util.math;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.eroelf.javaxsx.util.math.DataPreProcessor.DataStatistics.HistInfo;
import com.eroelf.javaxsx.util.math.mapping.Mapping;
import com.google.common.base.CaseFormat;

/**
 * This class implements some simple APIs to pre-process data
 * 
 * @author weikun.zhong
 */
public class DataPreProcessor
{
	public static class DataStatistics
	{
		public static class HistInfo
		{
			public int[] hist;
			public double[] regions;

			public HistInfo()
			{}

			public HistInfo(int groupNum)
			{
				hist=new int[groupNum];
				regions=new double[groupNum+1];
			}

			public HistInfo(int[] hist, double[] regions)
			{
				this.hist=hist;
				this.regions=regions;
			}

			@Override
			public HistInfo clone()
			{
				return new HistInfo(Arrays.copyOf(hist, hist.length), Arrays.copyOf(regions, regions.length));
			}
		}

		public double[] data;

		public double mean;
		public double std;
		public double sum;
		public int maxIdx;
		public int minIdx;

		public HistInfo histInfo=null;
		public int histAutoGroupNum;

		public boolean validStatistics;
		public boolean validMaxMin;

		public boolean ignoreInfinite=false;

		public void setIgnoreInfinite(boolean ignoreInfinite)
		{
			this.ignoreInfinite=ignoreInfinite;
		}

		public void resetStatistics()
		{
			mean=0;
			std=0;
			maxIdx=0;
			minIdx=0;
			sum=0;

			histInfo=null;
			histAutoGroupNum=-1;

			validStatistics=false;
			validMaxMin=false;
		}

		public void calStatistics()
		{
			if(!validStatistics || !validMaxMin)
			{
				int validCount=0;
				if(ignoreInfinite)
				{
					for(int i=0; i<data.length; i++)
					{
						if(Double.isFinite(data[i]))
						{
							maxIdx=i;
							minIdx=i;
							break;
						}
					}
				}
				for(int i=0; i<data.length; i++)
				{
					if(!ignoreInfinite || Double.isFinite(data[i]))
					{
						sum+=data[i];
						std+=data[i]*data[i];
						if(data[maxIdx]<data[i])
							maxIdx=i;
						else if(data[minIdx]>data[i])
							minIdx=i;
						++validCount;
					}
				}
				if(validCount>0)
				{
					mean=sum/validCount;
					if(validCount>1)
					{
						std=(std-mean*mean*data.length)/(data.length-1);
						std=std>=0 ? Math.sqrt(std) : 0;
					}
					else
						std=Double.NaN;
				}
				else
				{
					sum=Double.NaN;
					mean=Double.NaN;
					std=Double.NaN;
				}
				validStatistics=true;
				validMaxMin=true;
			}
		}

		public void calMaxMin()
		{
			if(!validMaxMin)
				calStatistics();
		}

		public void scaleStatistics(double gain, double offset)
		{
			if(validStatistics)
			{
				mean=gain*mean+offset;
				std=Math.abs(gain)*std;
				sum=gain*sum+offset*data.length;
			}
			scaleMaxMin(gain);
		}

		public void scaleMaxMin(double gain)
		{
			if(validMaxMin && gain<0)
			{
				int temp=maxIdx;
				maxIdx=minIdx;
				minIdx=temp;
			}
		}

		public HistInfo histogram(int groupNum)
		{
			assert groupNum>0 : "groupNum must be greater than 0!";
			if(histAutoGroupNum!=groupNum)
			{
				calStatistics();
				histInfo=new HistInfo(groupNum);
				double interval=(data[maxIdx]-data[minIdx])/(groupNum-1);
				double min=data[minIdx]-interval/2d;
				for(int i=0; i<histInfo.regions.length; i++)
				{
					histInfo.regions[i]=min+i*interval;
				}
				for(double elem : data)
				{
					++histInfo.hist[(int)((elem-min)/interval)];
				}
				histAutoGroupNum=groupNum;
			}
			return histInfo;
		}

		public HistInfo histogram(double[] regions, boolean keepOutliers)
		{
			assert regions!=null && regions.length>1 : "regions must be a sorted double array with length greater than 1!";
			if(histInfo==null || histInfo.regions==null || !Arrays.equals(histInfo.regions, regions))
			{
				calStatistics();
				histInfo=new HistInfo(keepOutliers ? new int[regions.length] : new int[regions.length-1], regions);
				for(double elem : data)
				{
					int pos=Arrays.binarySearch(regions, elem);
					if(pos<0)
					{
						pos=-pos-1;
						if(pos!=0 && pos!=regions.length)
							++histInfo.hist[pos-1];
						else if(keepOutliers)
							++histInfo.hist[regions.length-1];
					}
					else
					{
						if(pos!=regions.length-1 || keepOutliers)
							++histInfo.hist[pos];
					}
				}
				histAutoGroupNum=-1;
			}
			return histInfo;
		}

		public double getMean()
		{
			calStatistics();
			return mean;
		}

		public double getStd()
		{
			calStatistics();
			return std;
		}

		public double getSum()
		{
			calStatistics();
			return sum;
		}

		public double getMax()
		{
			calMaxMin();
			return data[maxIdx];
		}

		public double getMin()
		{
			calMaxMin();
			return data[minIdx];
		}
	}

	private DataStatistics original=new DataStatistics();
	private DataStatistics processed=new DataStatistics();

	public DataPreProcessor()
	{}

	public DataPreProcessor(double[] data, boolean ifMakesACopy)
	{
		setData(data, ifMakesACopy);
	}

	public <T> DataPreProcessor(List<? extends T> dataList, String fieldName, Class<? extends T> clazz)
	{
		setData(dataList, fieldName, clazz);
	}

	public void setIgnoreInfinite(boolean ignoreInfinite)
	{
		original.setIgnoreInfinite(ignoreInfinite);
		processed.setIgnoreInfinite(ignoreInfinite);
	}

	public void setData(double[] data, boolean ifMakesACopy)
	{
		if(ifMakesACopy)
			this.original.data=Arrays.copyOf(data, data.length);
		else
			this.original.data=data;
		resetStatistics();
	}

	public <T> void setData(List<? extends T> dataList, String fieldName, Class<? extends T> clazz)
	{
		double[] oldOriginalData=original.data;
		original.data=new double[dataList.size()];
		try
		{
			Field field=clazz.getField(fieldName);
			if(field.getType().getSimpleName().equals("double"))
			{
				int i=0;
				Iterator<? extends T> iter=dataList.iterator();
				while(iter.hasNext())
				{
					original.data[i++]=(Double)field.get(iter.next());
				}
			}
			else
			{
				int i=0;
				Iterator<? extends T> iter=dataList.iterator();
				while(iter.hasNext())
				{
					original.data[i++]=((Number)field.get(iter.next())).doubleValue();
				}
			}
			resetStatistics();
		}
		catch(Exception e)
		{
			try
			{
				Set<Method> methodsSet=new HashSet<Method>();
				Method[] allMethods=clazz.getMethods();
				String methodName="get"+CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
				for(Method method : allMethods)
				{
					if(method.getName().equals(methodName))
						methodsSet.add(method);
				}
				if(!methodsSet.isEmpty())
				{
					int count=0;
					List<Exception> exceptions=new ArrayList<Exception>();
					for(Method method : methodsSet)
					{
						try
						{
							int i=0;
							Iterator<? extends T> iter=dataList.iterator();
							while(iter.hasNext())
							{
								original.data[i++]=((Number)method.invoke(iter.next())).doubleValue();
							}
							break;
						}
						catch(Exception e1)
						{
							exceptions.add(e1);
						}
						++count;
					}
					if(count==methodsSet.size())
					{
						Exception exception=new UnsupportedOperationException("setData::cannot invoke \"get\" method for data field "+fieldName+" of "+clazz.getSimpleName());
						exceptions.get(0).initCause(e);
						for(int i=1; i<exceptions.size(); i++)
						{
							exceptions.get(i).initCause(exceptions.get(i-1));
						}
						exception.initCause(exceptions.get(exceptions.size()-1));
						throw exception;
					}
					resetStatistics();
				}
				else
					throw new IllegalArgumentException("setData::cannot access data field "+fieldName+" of "+clazz.getSimpleName(), e);
			}
			catch(Exception e2)
			{
				if(Number.class.isAssignableFrom(clazz))
				{
					int i=0;
					Iterator<? extends T> iter=dataList.iterator();
					while(iter.hasNext())
					{
						original.data[i++]=((Number)iter.next()).doubleValue();
					}
					resetStatistics();
				}
				else
				{
					original.data=oldOriginalData;
					e2.initCause(e);
					throw new IllegalArgumentException(e2);
				}
			}
		}
	}

	private void resetStatistics()
	{
		original.resetStatistics();
		processed.resetStatistics();
		if(processed.data==null || processed.data.length!=original.data.length)
			processed.data=new double[original.data.length];
	}

	public double[] getProcessed()
	{
		return processed.data;
	}

	@SuppressWarnings("unchecked")
	public <T> void fillProcessed(List<? extends T> dataList, String fieldName, Class<? extends T> clazz)
	{
		if(dataList.size()==processed.data.length)
		{
			try
			{
				Field field=clazz.getField(fieldName);
				if(field.getType().getSimpleName().equals("double"))
				{
					int i=0;
					Iterator<? extends T> iter=dataList.iterator();
					while(iter.hasNext())
					{
						field.set(iter.next(), processed.data[i++]);
					}
				}
				else
				{
					Method typeValue=null;
					if(field.getType().isPrimitive())
						typeValue=Double.class.getMethod(field.getType().getSimpleName()+"Value");
					else
						typeValue=Double.class.getMethod(((Class<? extends Number>)((Class<? extends Number>)field.getType()).getField("TYPE").get(null)).getSimpleName()+"Value");
					int i=0;
					Iterator<? extends T> iter=dataList.iterator();
					while(iter.hasNext())
					{
						field.set(iter.next(), typeValue.invoke(processed.data[i++]));
					}
				}
			}
			catch(Exception e)
			{
				try
				{
					Set<Method> methodsSet=new HashSet<Method>();
					Method[] allMethods=clazz.getMethods();
					String methodName="set"+CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
					for(Method method : allMethods)
					{
						if(method.getName().equals(methodName))
							methodsSet.add(method);
					}
					if(!methodsSet.isEmpty())
					{
						int count=0;
						List<Exception> exceptions=new ArrayList<Exception>();
						for(Method method : methodsSet)
						{
							try
							{
								int i=0;
								Iterator<? extends T> iter=dataList.iterator();
								while(iter.hasNext())
								{
									method.invoke(iter.next(), processed.data[i++]);
								}
								break;
							}
							catch(Exception e1)
							{
								exceptions.add(e1);
							}
							++count;
						}
						if(count==methodsSet.size())
						{
							Exception exception=new UnsupportedOperationException("fillProcessed::cannot invoke \"set\" method on data field "+fieldName+" of "+clazz.getSimpleName());
							exceptions.get(0).initCause(e);
							for(int i=1; i<exceptions.size(); i++)
							{
								exceptions.get(i).initCause(exceptions.get(i-1));
							}
							exception.initCause(exceptions.get(exceptions.size()-1));
							throw exception;
						}
					}
					else
						throw new IllegalArgumentException("fillProcessed::cannot access data field "+fieldName+" of "+clazz.getSimpleName());
				}
				catch(Exception e2)
				{
					e2.initCause(e);
					if(Number.class.isAssignableFrom(clazz))
					{
						try
						{
							Method typeValue=Double.class.getMethod(((Class<? extends Number>)((Class<? extends Number>)clazz).getField("TYPE").get(null)).getSimpleName()+"Value");
							int i=0;
							ListIterator<T> iter=(ListIterator<T>)dataList.listIterator();
							while(iter.hasNext())
							{
								iter.next();
								iter.set((T)typeValue.invoke(original.data[i++]));
							}
						}
						catch(Exception e3)
						{
							e3.initCause(e2);
							throw new UnsupportedOperationException(e3);
						}
					}
					else
						throw new IllegalArgumentException(e2);
				}
			}
		}
		else
			throw new IllegalArgumentException("fillProcessed::size mismatch!");
	}

	public void normalize(double stdThreshold)
	{
		original.calStatistics();
		assert stdThreshold>0 : "stdThreshold must be greater than 0!";
		double div=Math.max(original.std, stdThreshold);
		for(int i=0; i<processed.data.length; i++)
		{
			processed.data[i]=(original.data[i]-original.mean)/div;
		}
		processed.resetStatistics();
		if(original.validMaxMin)
		{
			processed.maxIdx=original.maxIdx;
			processed.minIdx=original.minIdx;
			processed.validMaxMin=original.validMaxMin;
		}
	}

	public void toNormal(double min, double max)
	{
		final NormalDistribution normalDistribution=new NormalDistribution();

		double[] a=Arrays.copyOf(original.data, original.data.length);
		Arrays.sort(a);
		Map<Double, Double> lookupTable=new HashMap<Double, Double>();
		int i=0;
		double cumulativeAtMin=normalDistribution.cumulativeProbability(min);
		double cumulativeAtMax=normalDistribution.cumulativeProbability(max);
		double k=(cumulativeAtMax-cumulativeAtMin);
		while(i<a.length)
		{
			int j=i+1;
			while(j<a.length && a[j]==a[i])
			{
				++j;
			}
			lookupTable.put(a[i], normalDistribution.inverseCumulativeProbability(k*((double)(j-i)/2d+i)/a.length+cumulativeAtMin));
			i=j;
		}
		for(i=0; i<original.data.length; i++)
		{
			processed.data[i]=lookupTable.get(original.data[i]);
		}
		processed.resetStatistics();
		if(original.validMaxMin)
		{
			processed.maxIdx=original.maxIdx;
			processed.minIdx=original.minIdx;
			processed.validMaxMin=original.validMaxMin;
		}
	}

	public void toUniform()
	{
		double[] a=Arrays.copyOf(original.data, original.data.length);
		Arrays.sort(a);
		Map<Double, Double> lookupTable=new HashMap<Double, Double>();
		int i=0;
		while(i<a.length)
		{
			int j=i+1;
			while(j<a.length && a[j]==a[i])
			{
				++j;
			}
			lookupTable.put(a[i], ((double)(j-i)/2d+i)/a.length);
			i=j;
		}
		for(i=0; i<original.data.length; i++)
		{
			processed.data[i]=lookupTable.get(original.data[i]);
		}
		processed.resetStatistics();
		if(original.validMaxMin)
		{
			processed.maxIdx=original.maxIdx;
			processed.minIdx=original.minIdx;
			processed.validMaxMin=original.validMaxMin;
		}
	}

	public void scale(double gain, double offset)
	{
		for(int i=0; i<original.data.length; i++)
		{
			processed.data[i]=gain*original.data[i]+offset;
		}
		processed.mean=original.mean;
		processed.std=original.std;
		processed.sum=original.sum;
		processed.validStatistics=original.validStatistics;
		processed.maxIdx=original.maxIdx;
		processed.minIdx=original.minIdx;
		processed.validMaxMin=original.validMaxMin;
		processed.scaleStatistics(gain, offset);
	}

	public void scaleTo(double y1, double y2)
	{
		original.calStatistics();
		double k=(y2-y1)/(original.getMax()-original.getMin());
		double b=y1-k*original.getMin();
		scale(k, b);
	}

	public void reScale(double gain, double offset)
	{
		for(int i=0; i<processed.data.length; i++)
		{
			processed.data[i]=gain*processed.data[i]+offset;
		}
		processed.scaleStatistics(gain, offset);
	}

	public void reScaleTo(double y1, double y2)
	{
		processed.calStatistics();
		double k=(y2-y1)/(processed.getMax()-processed.getMin());
		double b=y1-k*processed.getMin();
		reScale(k, b);
	}

	public void mapBasedOn(Mapping function)
	{
		for(int i=0; i<original.data.length; i++)
		{
			processed.data[i]=function.map(original.data[i]);
		}
		processed.resetStatistics();
	}

	public void remapBasedOn(Mapping function)
	{
		for(int i=0; i<processed.data.length; i++)
		{
			processed.data[i]=function.map(processed.data[i]);
		}
		processed.resetStatistics();
	}

	public double getOriginalMean()
	{
		return original.getMean();
	}

	public double getOriginalStd()
	{
		return original.getStd();
	}

	public double getOriginalSum()
	{
		return original.getSum();
	}

	public double getOriginalMax()
	{
		return original.getMax();
	}

	public double getOriginalMin()
	{
		return original.getMin();
	}

	public HistInfo getOriginalHistogram(int groupNum)
	{
		return original.histogram(groupNum);
	}
	
	public HistInfo getOriginalHistogram(double[] regions, boolean keepOutliers)
	{
		return original.histogram(regions, keepOutliers);
	}

	public double getProcessedMean()
	{
		return processed.getMean();
	}

	public double getProcessedStd()
	{
		return processed.getStd();
	}

	public double getProcessedSum()
	{
		return processed.getSum();
	}

	public double getProcessedMax()
	{
		return processed.getMax();
	}

	public double getProcessedMin()
	{
		return processed.getMin();
	}

	public HistInfo getProcessedHistogram(int groupNum)
	{
		return processed.histogram(groupNum);
	}
	
	public HistInfo getProcessedHistogram(double[] regions, boolean keepOutliers)
	{
		return processed.histogram(regions, keepOutliers);
	}

	public static String formatHistogram(int[] hist, int pointNumForOne)
	{
		return formatHistogram(hist, pointNumForOne, '|', '>');
	}

	public static String formatHistogram(int[] hist, int pointNumForOne, char separator, char histElem)
	{
		StringBuilder stringBuilder=new StringBuilder();
		double dataSize=0;
		for(int data : hist)
		{
			dataSize+=data;
		}
		for(int data : hist)
		{
			double p=(double)data/dataSize;
			long num=Math.round(p*(double)pointNumForOne);
			stringBuilder.append(String.format("%.8f%c", p, separator));
			for(int i=0; i<num; i++)
			{
				stringBuilder.append(histElem);
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	public static String formatHistogram(HistInfo histInfo, int pointNumForOne)
	{
		return formatHistogram(histInfo, pointNumForOne, '|', '>');
	}

	public static String formatHistogram(HistInfo histInfo, int pointNumForOne, char separator, char histElem)
	{
		return formatHistogram(histInfo.hist, pointNumForOne, histInfo.regions, separator, histElem);
	}

	public static String formatHistogram(int[] hist, int pointNumForOne, double[] regions)
	{
		return formatHistogram(hist, pointNumForOne, regions, '|', '>');
	}

	public static String formatHistogram(int[] hist, int pointNumForOne, double[] regions, char separator, char histElem)
	{
		final double maxNotExp=1e+8;
		final double minNotExp=1e-8;
		final double epsilon=1e-8;

		int maxIntDigits=0;
		int maxDecimalDigits=0;
		int expIntDigits=0;
		int expDecimalDigits=0;
		int expPowerDigits=0;

		double decimalResidual=-1;
		double expDecimalResidual=-1;
		double lastAbs=-1;
		for(int i=0; i<regions.length; i++)
		{
			double abs=Math.abs(regions[i]);
			if((abs>=minNotExp && abs<maxNotExp) || abs==0)
			{
				int currIntDigits=(int)Math.log10(Math.max(abs, 1))+(regions[i]<0 ? 2 : 1);
				if(maxIntDigits<currIntDigits)
					maxIntDigits=currIntDigits;

				int currDecimalDigits;
				if(lastAbs>=minNotExp && lastAbs<maxNotExp)
				{
					double diff=regions[i]-regions[i-1];
					currDecimalDigits=(int)Math.ceil((-Math.log10(diff)));
					if(maxDecimalDigits<currDecimalDigits)
						maxDecimalDigits=currDecimalDigits;
				}

				double decimal=abs-(int)abs;
				currDecimalDigits=decimal<epsilon ? 0 : (int)Math.ceil((-Math.log10(decimal)));
				if(maxDecimalDigits<currDecimalDigits)
				{
					maxDecimalDigits=currDecimalDigits;

					double multi=abs*Math.pow(10, maxDecimalDigits);
					decimalResidual=multi-(int)multi;
				}
			}
			else if(abs<minNotExp)
			{
				expIntDigits=Math.max(expIntDigits, regions[i]<0 ? 2 : 1);

				double currPower=Math.ceil(-Math.log10(abs));
				int currExpDecimalDigits;
				if(lastAbs<minNotExp)
				{
					double diff=regions[i]-regions[i-1];
					currExpDecimalDigits=(int)Math.ceil((-Math.log10(diff)))-(int)(currPower);
					if(expDecimalDigits<currExpDecimalDigits)
						expDecimalDigits=currExpDecimalDigits;
				}

				double expDecimal=abs*Math.pow(10, (int)(currPower));
				expDecimal=expDecimal-(int)expDecimal;
				currExpDecimalDigits=expDecimal<epsilon ? 0 : (int)Math.ceil((-Math.log10(expDecimal)));
				if(expDecimalDigits<currExpDecimalDigits)
				{
					expDecimalDigits=currExpDecimalDigits;

					double multi=expDecimal*Math.pow(10, expDecimalDigits);
					expDecimalResidual=multi-(int)multi;
				}

				int currExpPowerDigits=(int)Math.log10(Math.max((int)(currPower), 1))+1;
				if(expPowerDigits<currExpPowerDigits)
					expPowerDigits=currExpPowerDigits;
			}
			else
			{
				expIntDigits=Math.max(expIntDigits, regions[i]<0 ? 2 : 1);

				double currPower=Math.log10(abs);
				int currExpDecimalDigits;
				if(lastAbs>=maxNotExp)
				{
					double diff=regions[i]-regions[i-1];
					currExpDecimalDigits=(int)Math.ceil((-Math.log10(diff)))+(int)currPower;
					if(expDecimalDigits<currExpDecimalDigits)
						expDecimalDigits=currExpDecimalDigits;
				}

				double expDecimal=abs/Math.pow(10, (int)currPower);
				expDecimal=expDecimal-(int)expDecimal;
				currExpDecimalDigits=expDecimal<epsilon ? 0 : (int)Math.ceil((-Math.log10(expDecimal)));
				if(expDecimalDigits<currExpDecimalDigits)
				{
					expDecimalDigits=currExpDecimalDigits;

					double multi=expDecimal*Math.pow(10, expDecimalDigits);
					expDecimalResidual=multi-(int)multi;
				}

				int currExpPowerDigits=(int)Math.log10(Math.max((int)currPower, 1))+1;
				if(expPowerDigits<currExpPowerDigits)
					expPowerDigits=currExpPowerDigits;
			}
			lastAbs=abs;
		}
		if(decimalResidual>=0.1)
			++maxDecimalDigits;
		if(expDecimalResidual>=0.1)
			++expDecimalDigits;
		String others="others"+separator;
		int len=Math.max(maxIntDigits+(maxDecimalDigits>0 ? maxDecimalDigits+1 : 0), expIntDigits+expDecimalDigits+expPowerDigits+3);
		for(int i=others.length(); i<len*2+5; i++)
		{
			others=" "+others;
		}
		String doublefmt=String.format("%%%d.%df", len, maxDecimalDigits);
		String expFmt=String.format("%%%d.%de", len, expDecimalDigits);

		StringBuilder stringBuilder=new StringBuilder();
		double dataSize=0;
		for(int data : hist)
		{
			dataSize+=data;
		}
		lastAbs=Math.abs(regions[0]);
		for(int i=0; i<hist.length; i++)
		{
			double p=(double)hist[i]/dataSize;
			long num=Math.round(p*(double)pointNumForOne);
			if(i+1<regions.length)
			{
				double abs=Math.abs(regions[i+1]);
				String fmt=String.format("[%s, %s)%c", ((lastAbs>=minNotExp && lastAbs<maxNotExp) || lastAbs==0) ? doublefmt : expFmt, ((abs>=minNotExp && abs<maxNotExp) || abs==0) ? doublefmt : expFmt, separator);
				stringBuilder.append(String.format(fmt, regions[i], regions[i+1])).append(String.format("%.8f%c", p, separator));
				lastAbs=abs;
			}
			else
				stringBuilder.append(others).append(String.format("%.8f%c", p, separator));
			for(int j=0; j<num; j++)
			{
				stringBuilder.append(histElem);
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}
}
