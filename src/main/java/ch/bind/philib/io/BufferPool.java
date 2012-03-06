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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class BufferPool implements ObjPoolType<byte[]> {
//public final class BufferPool extends ObjectPool<byte[]> {

	public static final int DEFAULT_BUFFER_SIZE = 8192;

	// 1 mb of buffers with the default buffer size of 8k
	public static final int DEFAULT_NUM_BUFFERS = 128;

	private final int bufSize;
	
	private final ObjectPool<byte[]> pool;

	private BufferPool() {
		this(DEFAULT_BUFFER_SIZE, DEFAULT_NUM_BUFFERS);
	}

	private BufferPool(int bufferSize) {
		this(bufferSize, DEFAULT_NUM_BUFFERS);
	}

	private BufferPool(int bufferSize, int maxBuffers) {
//		super(maxBuffers);
		this.bufSize = bufferSize;
	}
	
	public BufferPool createPoolö(int bufferSize, int maxBuffers) {
		ObjectPool<byte[]> pool = new ObjectPool<byte[]>();
		return new BufferPool(bufferSize)
	}

	private final AtomicLong creates = new AtomicLong();

	@Override
	public byte[] create() {
		creates.incrementAndGet();
		return new byte[bufSize];
	}

	@Override
	protected void destroy(byte[] e) {
	}

	public long getNumCreates() {
		return creates.get();
	}

	@Override
	public void release(byte[] buf) {
		// discard buffers which do not have the right size
		if (buf != null && buf.length == bufSize) {
			super.release(buf);
		}
	}
}
