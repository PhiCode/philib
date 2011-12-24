package ch.bind.philib.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class SocketAddresses {

	private SocketAddresses() {
	}

	public static InetSocketAddress wildcard(int port) {
		try {
			return fromIp("0.0.0.0", port);
		} catch (UnknownHostException e) {
			throw new RuntimeException("wildcard hostname not found: " + e.getMessage(), e);
		}
	}

	public static InetSocketAddress fromIp(String address, int port) throws UnknownHostException {
		InetAddress inetAddr = Inet4Address.getByName(address);
		return new InetSocketAddress(inetAddr, port);
	}
}
