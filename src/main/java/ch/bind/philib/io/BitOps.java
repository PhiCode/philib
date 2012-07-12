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

package ch.bind.philib.io;

public final class BitOps {

	private BitOps() {}

	public static final int findLowestSetBitIdx64(final long v) {
		if (v == 0) {
			// no bits are set
			return -1;
		}
		long mask = 0x00000000FFFFFFFFL;
		int shift = 0;
		if ((v & mask) == 0) {
			shift += 32;
		}
		mask = (0xFFFFL << shift);
		if ((v & mask) == 0) {
			shift += 16;
		}
		mask = (0xFFL << shift);
		if ((v & mask) == 0) {
			shift += 8;
		}
		mask = (0xFL << shift);
		if ((v & mask) == 0) {
			shift += 4;
		}
		mask = (0x3L << shift);
		if ((v & mask) == 0) {
			shift += 2;
		}
		mask = (0x1L << shift);
		if ((v & mask) == 0) {
			shift += 1;
		}
		return shift;
	}

	public static final boolean checkMask(int bitset, int mask) {
		return (bitset & mask) == mask;
	}

	// public static final int MASK32[] = { 0x0, 0x1, 0x3, 0x7, 0xF, 0x10, 0x20,
	// 0x40, 0x80, 0x10
	//
	// };
	//
	// public static final void main(String[] a) {
	// long mask = 1L;
	// for (long i = 1; i < 64; i++) {
	// System.out.printf("%2d => 0x%16H%n", i, (Long)mask);
	// mask = ((mask << 1L) | 1L);
	// }
	// }

	public static final int rotl32(final int value, final int r) {
		assert (r >= 0 && r <= 32);
		return (value << r) | (value >>> (32 - r));
	}

	public static final long rotl64(final long value, final int r) {
		assert (r >= 0 && r <= 64);
		return (value << r) | (value >>> (64 - r));
	}

	public static final int rotr32(final int value, final int r) {
		assert (r >= 0 && r <= 32);
		return (value >>> r) | (value << (32 - r));
	}

	public static final long rotr64(final long value, final int r) {
		assert (r >= 0 && r <= 64);
		return (value >>> r) | (value << (64 - r));
	}
}
