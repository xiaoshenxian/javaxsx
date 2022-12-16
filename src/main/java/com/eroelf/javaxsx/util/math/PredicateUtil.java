package com.eroelf.javaxsx.util.math;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

/**
 * 
 * @author weikun.zhong
 */
public class PredicateUtil
{
	public static enum NormType
	{
		/**
		 * ∩(∪sublist)
		 */
		CNF
		{
			@SuppressWarnings("unchecked")
			@Override
			public <T, P extends Predicate<? super T>> Predicate<T> norm(List<List<P>> form)
			{
				return form.parallelStream().map(sub -> sub.stream().map(p -> (Predicate<T>)p).reduce((p1, p2) -> p1.or(p2)).orElse(Predicates.alwaysFalse())).reduce((p1, p2) -> p1.and(p2)).orElse(Predicates.alwaysFalse());
			}
		},

		/**
		 * ∪(∩sublist)
		 */
		DNF
		{
			@SuppressWarnings("unchecked")
			@Override
			public <T, P extends Predicate<? super T>> Predicate<T> norm(List<List<P>> form)
			{
				return form.parallelStream().map(sub -> sub.stream().map(p -> (Predicate<T>)p).reduce((p1, p2) -> p1.and(p2)).orElse(Predicates.alwaysFalse())).reduce((p1, p2) -> p1.or(p2)).orElse(Predicates.alwaysFalse());
			}
		};

		public abstract <T, P extends Predicate<? super T>> Predicate<T> norm(List<List<P>> form);
	}

	public static <T, P extends Predicate<? super T>> Predicate<T> norm(List<List<P>> form, NormType normType)
	{
		return normType.norm(form);
	}
}
