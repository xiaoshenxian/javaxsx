package com.eroelf.javaxsx.util.ml.flow.estimate;

import java.util.ArrayList;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Generator interface provides method to generate {@link Item} objects.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of {@link Item} objects generated.
 */
public interface ItemGenerator<T extends Item>
{
	/**
	 * Runs the flow and generates {@link Item} objects without sorting.
	 * 
	 * @param destination a {@link Item} {@link List} to which the generated objects are added.
	 * @return the input {@link Item} {@link List} with the generated objects appended.
	 */
	public List<T> generate(List<T> destination);

	/**
	 * Runs the flow and generates {@link Item} objects without sorting.
	 * 
	 * @return a new {@link Item} {@link List} contains the generated objects.
	 * @see {@link #generate(List)}
	 */
	default public List<T> generate()
	{
		return generate(new ArrayList<>());
	}
}
