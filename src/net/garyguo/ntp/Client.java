package net.garyguo.ntp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;

public class Client {

	public static long synchronize(String server) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(2000);

		// Build NTP Packet
		Packet ntpPacket = new Packet();
		long t0 = System.currentTimeMillis();
		ntpPacket.setTransmitTimestamp(new Packet.Timestamp(t0));

		// Convert packet to datagram packet
		byte[] ntpRawPacket = ntpPacket.asByteArray();
		DatagramPacket packet = new DatagramPacket(ntpRawPacket,
				ntpRawPacket.length, InetAddress.getByName(server), 123);

		// Send and wait for response
		socket.send(packet);
		socket.receive(packet);

		// Extract timestamps from packet
		long t3 = System.currentTimeMillis();
		Packet recvPacket = new Packet(ByteBuffer.wrap(packet.getData()));
		long t1 = recvPacket.getReceiveTimestamp().getTimeMillis();
		long t2 = recvPacket.getTransmitTimestamp().getTimeMillis();

		socket.close();

		return (t1 + t2 - t0 - t3 + 1) / 2;
	}

	public static void main(String[] args) throws IOException {
		long diff = synchronize("ntp0.cl.cam.ac.uk");
		System.out.println(new Date(System.currentTimeMillis() + diff));
	}

}
