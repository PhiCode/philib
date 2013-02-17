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

package ch.bind.philib.lang;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ThreadUtilTest {

	@Test
	public void normal() throws Exception {
		TestRunnable r = new TestRunnable(false, false);
		Runnable wrapped = new ThreadUtil.ForeverRunner(r);
		wrapped.run();
		assertEquals(r.numStarts, 1);
	}

	@Test
	public void exception() throws Exception {
		TestRunnable r = new TestRunnable(true, false);
		Runnable wrapped = new ThreadUtil.ForeverRunner(r);
		wrapped.run();
		assertEquals(r.numStarts, 2);
	}

	@Test
	public void error() throws Exception {
		TestRunnable r = new TestRunnable(false, true);
		Runnable wrapped = new ThreadUtil.ForeverRunner(r);
		wrapped.run();
		assertEquals(r.numStarts, 2);
	}

	@Test
	public void exceptionThenError() throws Exception {
		TestRunnable r = new TestRunnable(true, true);
		Runnable wrapped = new ThreadUtil.ForeverRunner(r);
		wrapped.run();
		assertEquals(r.numStarts, 3);
	}

	private static final class TestRunnable implements Runnable {

		private boolean throwException;

		private boolean throwError;

		private int numStarts;

		TestRunnable(boolean throwException, boolean throwError) {
			this.throwException = throwException;
			this.throwError = throwError;
		}

		@Override
		public void run() {
			numStarts++;
			if (throwException) {
				throwException = false;
				// forever-runner must restart this runnable
				throw new RuntimeException();
			}
			if (throwError) {
				throwError = false;
				// forever-runner must not restart this runnable
				throw new Error();
			}
		}
	}
}
