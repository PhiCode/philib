/*
 * Copyright (c) 2006-2009 Philipp Meinen <philipp@bind.ch>
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

package ch.bind.philib.io;

import static junit.framework.Assert.assertEquals;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EndianConverterTest {

	private static final int REPEATS = 2048;

	private Random rand;

	private byte[] buf;

	@Before
	public void setup() {
		rand = new Random();
		buf = new byte[4096];
	}

	@After
	public void cleanup() {
		rand = null;
		buf = null;
	}

	@Test
	public void randomInt64() {
		long v = rand.nextLong();
		testInt64(v);
		for (int i = 0; i < REPEATS; i++) {
			v = rand.nextLong();
			testInt64(v, i);
		}
	}

	@Test
	public void randomInt32() {
		int v = rand.nextInt();
		testInt32(v);
		for (int i = 0; i < REPEATS; i++) {
			v = rand.nextInt();
			testInt32(v, i);
		}
	}

	@Test
	public void randomInt16() {
		int v = rand.nextInt(65536);
		testInt16(v);
		for (int i = 0; i < REPEATS; i++) {
			v = rand.nextInt(65536);
			testInt16(v, i);
		}
	}

	@Test
	public void randomInt8() {
		int v = rand.nextInt(256);
		testInt8(v);
		for (int i = 0; i < REPEATS; i++) {
			v = rand.nextInt(256);
			testInt8(v, i);
		}
	}

	private void testInt64(final long v) {
		testInt64BE(v);
		testInt64LE(v);
	}

	private void testInt64(final long v, int off) {
		testInt64BE(v, off);
		testInt64LE(v, off);
	}

	private void testInt64BE(final long v) {
		EndianConverter.encodeInt64BE(v, buf);
		long dec = EndianConverter.decodeInt64BE(buf);
		assertEquals(v, dec);
	}

	private void testInt64BE(final long v, final int off) {
		EndianConverter.encodeInt64BE(v, buf, off);
		long dec = EndianConverter.decodeInt64BE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt64LE(final long v) {
		EndianConverter.encodeInt64LE(v, buf);
		long dec = EndianConverter.decodeInt64LE(buf);
		assertEquals(v, dec);
	}

	private void testInt64LE(final long v, final int off) {
		EndianConverter.encodeInt64LE(v, buf, off);
		long dec = EndianConverter.decodeInt64LE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt32(final int v) {
		testInt32BE(v);
		testInt32LE(v);
	}

	private void testInt32(final int v, int off) {
		testInt32BE(v, off);
		testInt32LE(v, off);
	}

	private void testInt32BE(final int v) {
		EndianConverter.encodeInt32BE(v, buf);
		int dec = EndianConverter.decodeInt32BE(buf);
		assertEquals(v, dec);
	}

	private void testInt32BE(final int v, final int off) {
		EndianConverter.encodeInt32BE(v, buf, off);
		int dec = EndianConverter.decodeInt32BE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt32LE(final int v) {
		EndianConverter.encodeInt32LE(v, buf);
		int dec = EndianConverter.decodeInt32LE(buf);
		assertEquals(v, dec);
	}

	private void testInt32LE(final int v, final int off) {
		EndianConverter.encodeInt32LE(v, buf, off);
		int dec = EndianConverter.decodeInt32LE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt16(final int v) {
		testInt16BE(v);
		testInt16LE(v);
	}

	private void testInt16(final int v, int off) {
		testInt16BE(v, off);
		testInt16LE(v, off);
	}

	private void testInt16BE(final int v) {
		EndianConverter.encodeInt16BE(v, buf);
		int dec = EndianConverter.decodeInt16BE(buf);
		assertEquals(v, dec);
	}

	private void testInt16BE(final int v, final int off) {
		EndianConverter.encodeInt16BE(v, buf, off);
		int dec = EndianConverter.decodeInt16BE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt16LE(final int v) {
		EndianConverter.encodeInt16LE(v, buf);
		int dec = EndianConverter.decodeInt16LE(buf);
		assertEquals(v, dec);
	}

	private void testInt16LE(final int v, final int off) {
		EndianConverter.encodeInt16LE(v, buf, off);
		int dec = EndianConverter.decodeInt16LE(buf, off);
		assertEquals(v, dec);
	}

	private void testInt8(final int v) {
		EndianConverter.encodeInt8(v, buf);
		int dec = EndianConverter.decodeInt8(buf);
		assertEquals(v, dec);
	}

	private void testInt8(final int v, final int off) {
		EndianConverter.encodeInt8(v, buf, off);
		int dec = EndianConverter.decodeInt8(buf, off);
		assertEquals(v, dec);
	}
}
