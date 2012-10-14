/*
 * Copyright (c) 2012 Philipp Meinen <philipp@bind.ch>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ch.bind.philib.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * A statically initialized and immutable map of {@code long -> T}.
 * 
 * @author Philipp Meinen
 */
public class StaticLongMap<T> {

	private final long[] keys;

	private final Object[] values;

	private StaticLongMap(long[] keys, Object[] values) {
		this.keys = keys;
		this.values = values;
	}

	/**
	 * 
	 * @param elements
	 * @return
	 * @throws IllegalArgumentException If the {@code elements} parameter is {@code null}, <i>empty</i>, contains
	 *             {@code null LongPair}s or contains duplicate keys.
	 */
	public static <T> StaticLongMap<T> create(Collection<LongPair<T>> elements) {
		if (elements == null || elements.isEmpty()) {
			throw new IllegalArgumentException("null or empty collection provided");
		}
		final int l = elements.size();
		LongPair<?>[] elems = new LongPair<?>[l];
		elems = elements.toArray(elems);
		return create((LongPair<T>[]) elems);
	}

	/**
	 * 
	 * @param elements
	 * @return
	 * @throws IllegalArgumentException If the {@code elements} parameter is {@code null}, <i>empty</i>, contains
	 *             {@code null LongPair}s or contains duplicate keys.
	 */
	public static <T> StaticLongMap<T> create(LongPair<T>... elements) {
		if (elements == null || elements.length == 0) {
			throw new IllegalArgumentException("null or empty collection provided");
		}
		// make a copy which we can sort so that we do not disturb the caller's array
		elements = elements.clone();
		return init(elements);
	}

	private static <T> StaticLongMap<T> init(LongPair<T>[] elements) {
		int len = elements.length;
		Arrays.sort(elements, LongPair.KEY_COMPARATOR);
		long[] keys = new long[len];
		Object[] values = new Object[len];
		long prevKey = 0;
		for (int i = 0; i < len; i++) {
			LongPair<T> elem = elements[i];
			long key = elem.getKey();
			T value = elem.getValue();
			if (i > 0 && prevKey == key) {
				throw new IllegalArgumentException("duplicate key: " + key);
			}
			prevKey = key;
			keys[i] = key;
			values[i] = value;
		}
		return new StaticLongMap<T>(keys, values);
	}

	public T get(long key) {
		int idx = Arrays.binarySearch(keys, key);
		return (T) (idx < 0 ? null : values[idx]);
	}

	public boolean containsKey(long key) {
		return Arrays.binarySearch(keys, key) >= 0;
	}

	public int size() {
		return keys.length;
	}
}
