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

package ch.bind.philib.net.tcp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.FileSystem;

import org.testng.annotations.Test;

import ch.bind.philib.io.SafeCloseUtil;
import ch.bind.philib.net.Connection;
import ch.bind.philib.net.NetServer;
import ch.bind.philib.net.Session;
import ch.bind.philib.net.SessionFactory;
import ch.bind.philib.net.SocketAddresses;
import ch.bind.philib.net.context.NetContext;
import ch.bind.philib.net.context.SimpleNetContext;
import ch.bind.philib.net.session.DevNullSession;

/**
 * TODO
 * 
 * @author Philipp Meinen
 */
public class TcpConnectionTest {

	@Test(timeOut = 60000, priority = 0)
	public void connectAndDisconnect() throws Exception {
		NetContext context = new SimpleNetContext();
		SocketAddress addr = SocketAddresses.localhost(1234);
		DevNullSessionFactory serverSessionFactory = new DevNullSessionFactory();
		DevNullSessionFactory clientSessionFactory = new DevNullSessionFactory();
		NetServer netServer = TcpNetFactory.INSTANCE.openServer(context, addr, serverSessionFactory);
		Session clientS = TcpNetFactory.INSTANCE.openClient(context, addr, clientSessionFactory);

		// give some time for the client and server-side of the connection to establish proper fusion power
		Thread.sleep(50);

		DevNullSession server = serverSessionFactory.session;
		DevNullSession client = clientSessionFactory.session;
		assertNotNull(server);
		assertNotNull(client);
		assertTrue(client == clientS);
		assertTrue(context.isOpen());
		assertTrue(netServer.isOpen());
		assertTrue(server.getConnection().isOpen());
		assertTrue(client.getConnection().isOpen());
		assertTrue(client.getConnection().isConnected());
		assertTrue(server.getServiceState().isOpen());
		assertTrue(client.getServiceState().isOpen());

		// this should close the server as well as the client
		context.close();

		assertFalse(context.isOpen());
		assertFalse(netServer.isOpen());
		assertFalse(server.getConnection().isOpen());
		assertFalse(client.getConnection().isOpen());
		assertFalse(client.getConnection().isConnected());
		assertTrue(server.getServiceState().isClosed());
		assertTrue(client.getServiceState().isClosed());
	}

	@Test(timeOut = 60000, priority = 10)
	public void sendManyZeros() throws Exception {
		NetContext context = new SimpleNetContext();
		SocketAddress addr = SocketAddresses.localhost(1234);
		DevNullSessionFactory serverSessionFactory = new DevNullSessionFactory();
		DevNullSessionFactory clientSessionFactory = new DevNullSessionFactory();
		NetServer netServer = TcpNetFactory.INSTANCE.openServer(context, addr, serverSessionFactory);
		Session clientS = TcpNetFactory.INSTANCE.openClient(context, addr, clientSessionFactory);

		// give some time for the client and server-side of the connection to establish proper fusion power
		Thread.sleep(50);

		DevNullSession server = serverSessionFactory.session;
		DevNullSession client = clientSessionFactory.session;

		int size = 1024 * 1024 * 1024;
		// just a whole lot of zeros to copy around
		ByteBuffer mappedBuffer = ByteBuffer.allocateDirect(1024 * 1024 * 1024);
		assertEquals(mappedBuffer.remaining(), size);

		sendSync(client.getConnection(), server.getConnection(), mappedBuffer);

		mappedBuffer.rewind();
		assertEquals(mappedBuffer.remaining(), size);

		sendSync(server.getConnection(), client.getConnection(), mappedBuffer);

		context.close();
	}

	private void sendSync(Connection from, Connection to, ByteBuffer data) throws Exception {
		long fromRx = from.getRx();
		long fromTx = from.getTx();
		long toRx = to.getRx();
		long toTx = to.getTx();

		int size = data.remaining();
		long tStart = System.nanoTime();
		from.sendSync(data);
		assertEquals(data.remaining(), 0);

		long tEndWrite = System.nanoTime();
		assertEquals(from.getRx(), fromRx); // no change
		assertEquals(from.getTx(), fromTx + size);

		while (to.getRx() < (toRx + size)) {
			Thread.yield();
		}
		assertEquals(to.getRx(), (toRx + size));
		assertEquals(to.getTx(), toTx); // no change

		long tEndReceive = System.nanoTime();
		System.out.printf("write took %.3fms, write+receive took %.3fms%n", //
				(tEndWrite - tStart) / 1000000f, (tEndReceive - tStart) / 1000000f);
	}

	private static final class DevNullSessionFactory implements SessionFactory {

		volatile DevNullSession session;

		@Override
		public synchronized Session createSession(Connection connection) {
			assertNull(session);
			session = new DevNullSession(connection);
			return session;
		}
	}
}
