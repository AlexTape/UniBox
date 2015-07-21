package de.unibox.core.provider;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The Class Reversed.
 *
 * @param <T>
 *            the generic type
 */
public class Reversed<T> implements Iterable<T> {

	/**
	 * Reversed.
	 *
	 * @param <T>
	 *            the generic type
	 * @param original
	 *            the original
	 * @return the reversed
	 */
	public static <T> Reversed<T> reversed(final List<T> original) {
		return new Reversed<T>(original);
	}

	/** The original. */
	private final List<T> original;

	/**
	 * Instantiates a new reversed.
	 *
	 * @param original
	 *            the original
	 */
	public Reversed(final List<T> original) {
		this.original = original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		final ListIterator<T> i = this.original.listIterator(this.original
				.size());

		return new Iterator<T>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#hasNext()
			 */
			@Override
			public boolean hasNext() {
				return i.hasPrevious();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#next()
			 */
			@Override
			public T next() {
				return i.previous();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Iterator#remove()
			 */
			@Override
			public void remove() {
				i.remove();
			}
		};
	}
}