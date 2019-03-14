package com.eroelf.javaxsx.util.ml.feature.strategy;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.feature.model.Modeler;

/**
 * Any subclass of this class will be regarded as a strategy which generates a group of {@link Item} instances and models them according to some specified conditions and strategies.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances generated by this strategy.
 */
public abstract class Strategy<T extends Item> implements Modeler
{
	/**
	 * Provides a set contains {@link Item} instances which corresponded to this strategy but without being modeled.
	 * 
	 * @return an {@link Iterable} object of all the candidates corresponded to this strategy.
	 */
	protected abstract Iterable<T> candicates();

	/**
	 * Generates a list of modeled {@link Item} instances which corresponded to this strategy.
	 * 
	 * @param preFilter a filter provide some additional restrictions which may be related to a specified situation but has nothing to do with this strategy.
	 * @return an {@link Iterable} object of the generated {@link Item} instances.
	 */
	public Iterable<T> generate(Predicate<T> preFilter)
	{
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator()
			{
				return new Iterator<T>() {
					private Iterator<T> iter=candicates().iterator();
					private T item=null;

					@Override
					public boolean hasNext()
					{
						while(item==null && iter.hasNext())
						{
							T tempItem=iter.next();
							if(preFilter.test(tempItem))
							{
								tempItem.modelBy(Strategy.this);
								item=tempItem;
								break;
							}
						}
						return item!=null;
					}

					@Override
					public T next()
					{
						if(item!=null)
						{
							T tempItem=item;
							item=null;
							return tempItem;
						}
						throw new NoSuchElementException();
					}
				};
			}
		};
	}
}
