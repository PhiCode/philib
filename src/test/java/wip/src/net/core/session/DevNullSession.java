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
package wip.src.net.core.session;

import java.io.IOException;
import java.nio.ByteBuffer;

import wip.src.net.core.Connection;
import wip.src.net.core.Events;
import wip.src.net.core.Session;

import ch.bind.philib.lang.ServiceState;

/**
 * TODO
 * 
 * @author Philipp Meinen
 */

public class DevNullSession implements Session {

	private final Connection connection;

	private final ServiceState serviceState = new ServiceState();

	public DevNullSession(Connection connection) {
		this.connection = connection;
		serviceState.setOpen();
	}

	@Override
	public Events receive(Connection conn, ByteBuffer data) throws IOException {
		// "consume" all data
		return Events.RECEIVE;
	}

	@Override
	public void closed(Connection conn) {
		serviceState.setClosed();
	}

	@Override
	public Events sendable(Connection conn) {
		// there will never be anything to write
		return Events.RECEIVE;
	}

	@Override
	public boolean handleTimeout() {
		return false;
	}

	public Connection getConnection() {
		return connection;
	}

	public ServiceState getServiceState() {
		return serviceState;
	}
}
