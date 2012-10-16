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

package ch.bind.philib.cache.lru.newimpl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.security.SecureRandom;

import org.testng.annotations.Test;

import ch.bind.philib.TestUtil;

public class SimpleCacheTest {

	@Test
	public void capacity() {
		Cache<Integer, Integer> cache;

		cache = new SimpleCache<Integer, Integer>();
		assertEquals(cache.capacity(), SimpleCache.DEFAULT_CACHE_CAPACITY);

		cache = new SimpleCache<Integer, Integer>(SimpleCache.DEFAULT_CACHE_CAPACITY * 4);
		assertEquals(cache.capacity(), SimpleCache.DEFAULT_CACHE_CAPACITY * 4);

		cache = new SimpleCache<Integer, Integer>(SimpleCache.MIN_CACHE_CAPACITY - 1);
		assertEquals(cache.capacity(), SimpleCache.MIN_CACHE_CAPACITY);

		cache = new SimpleCache<Integer, Integer>(0);
		assertEquals(cache.capacity(), SimpleCache.MIN_CACHE_CAPACITY);
	}

	@Test
	public void fullCacheWhereOldObjectGetRemoved() {
		final int testSize = SimpleCache.DEFAULT_CACHE_CAPACITY;

		SimpleCache<String, String> cache = new SimpleCache<String, String>(testSize);

		for (int i = 1; i <= testSize; i++) {
			cache.add(itos(i), itos(i * i * i));
		}

		// the key 1, value 1 falls away
		cache.add("-1", "-1");

		for (int i = 2; i <= testSize; i++) {
			String v = cache.get(itos(i));
			assertEquals(itos(i * i * i), v);
		}

		assertEquals("-1", cache.get("-1"));

		// the key 2, value 8 falls away
		cache.add("-2", "-2");

		for (int i = 3; i <= testSize; i++) {
			String v = cache.get(itos(i));
			assertEquals(itos(i * i * i), v);
		}

		assertEquals(cache.get("-1"), "-1");
		assertEquals(cache.get("-2"), "-2");
	}

	@Test
	public void fullCacheWhereOldObjectGetRemoved2() {
		final int testSize = 10000;
		SimpleCache<String, String> cache = new SimpleCache<String, String>(testSize);

		for (int i = 1; i <= testSize; i++) {
			cache.add(itos(i), itos(i * i));
		}

		// query the elements from 5000 to 8999 (4000 elements) so that
		// they are marked as having been accessed recently
		for (int i = 5000; i <= 8999; i++) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}

		// insert 6000 new elements
		// => 1-4999 and 9000-testSize get removed
		for (int i = 10001; i <= 16000; i++) {
			cache.add(itos(i), itos(i * i));
		}

		// elements 1 to 4999 == null
		for (int i = 1; i < 5000; i++) {
			assertNull(cache.get(itos(i)));
		}
		// elements 9000 to testSize == null
		for (int i = 9000; i <= testSize; i++) {
			assertNull(cache.get(itos(i)));
		}
		// elements 5000 to 8999 are present
		for (int i = 5000; i < 9000; i++) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}
		// elements 10001 to 16000 are present
		for (int i = 10001; i <= 16000; i++) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}
	}

	@Test
	public void fullCacheWhereOldObjectGetRemoved3() {
		SimpleCache<String, String> cache = new SimpleCache<String, String>(100000);

		for (int i = 1; i <= 100000; i++) {
			cache.add(itos(i), itos(i * i));
		}

		// query every second element so that
		// they are marked as beeing accessed recently
		for (int i = 2; i <= 100000; i += 2) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}

		// insert 50000 new elements
		// => all odd numbers from 1-100000 get removed
		for (int i = 100001; i <= 150000; i++) {
			cache.add(itos(i), itos(i * i));
		}

		// all odd numbers from 1-100000 are null
		for (int i = 1; i < 100000; i += 2) {
			assertNull(cache.get(itos(i)));
		}
		// all even numbers are present
		for (int i = 2; i <= 100000; i += 2) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}
		// elements 100001 to 150000 are present
		for (int i = 100001; i <= 150000; i++) {
			assertEquals(cache.get(itos(i)), itos(i * i));
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getNullKey() {
		Cache<String, String> cache = new SimpleCache<String, String>();
		cache.get(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addNullKey() {
		Cache<String, String> cache = new SimpleCache<String, String>();
		cache.add(null, "abc");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void removeNullKey() {
		Cache<String, String> cache = new SimpleCache<String, String>();
		cache.remove(null);
	}

	@Test
	public void get() {
		Cache<String, String> cache = new SimpleCache<String, String>();

		assertNull(cache.get("1"));
		cache.add("1", "one");
		assertEquals(cache.get("1"), "one");

		cache.remove("2");
		assertEquals(cache.get("1"), "one");

		cache.remove("1");
		assertNull(cache.get("1"));
	}

	@Test
	public void overwrite() {
		Cache<String, String> cache = new SimpleCache<String, String>();
		cache.add("1", "version 1");
		cache.add("1", "version 2");
		assertEquals(cache.get("1"), "version 2");
		cache.add("1", null);
		assertNull(cache.get("1"));
		// overwrite again for full branch-coverage
		cache.add("1", null);
		assertNull(cache.get("1"));
	}

	@Test
	public void softReferences() {
		byte[] data = new byte[512 * 1024]; // 512KiB
		new SecureRandom().nextBytes(data);

		// Make the cache hold on to more data than it possibly can
		// 16GiB if the vm has no specified upper limit
		// otherwise double the amount that the vm can use
		long vmmax = Runtime.getRuntime().maxMemory();
		vmmax = vmmax == Long.MAX_VALUE ? 16L * 1024 * 1024 * 1024 : vmmax * 2;
		final int cap = (int) (vmmax / data.length);
		long t0 = System.nanoTime();
		Cache<Integer, byte[]> cache = new SimpleCache<Integer, byte[]>(cap);
		long t1 = System.nanoTime() - t0;
		for (int i = 0; i < cap; i++) {
			cache.add(i, data.clone());
		}
		long t2 = System.nanoTime() - t0 - t1;
		int inMem = 0;
		for (int i = 0; i < cap; i++) {
			if (cache.get(i) != null) {
				inMem++;
			}
		}
		long t3 = System.nanoTime() - t0 - t1 - t2;
		cache.clear();
		TestUtil.gcAndSleep(100);
		System.out.printf("JVM held on to %d out of %d cached elements => %dMiB\n", inMem, cap, inMem / 2);
		System.out.printf("times[init=%.3fms, filling %.1fGiB: %.3fms, counting live entries: %.3fms]\n", //
				t1 / 1000000f, cap / 2048f, t2 / 1000000f, t3 / 1000000f);
	}

	private static String itos(int i) {
		return Integer.toString(i);
	}
}
