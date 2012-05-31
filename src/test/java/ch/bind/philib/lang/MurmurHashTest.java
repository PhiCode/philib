package ch.bind.philib.lang;

import java.util.Random;

public class MurmurHashTest {

	private static final Random r = new Random();

	public static void main(String[] args) {
		for (int i = 0; i < 4; i++) {
			testSpeed2(1);
			testSpeed2(2);
			testSpeed2(3);
			testSpeed2(4);
			testSpeed3(1);
			testSpeed3(2);
			testSpeed3(3);
			testSpeed3(4);
		}
		int size = 8;
		for (int i = 0; i < 14; i++) {
			testSpeed2(size);
			size *= 2;
		}
		size = 8;
		for (int i = 0; i < 14; i++) {
			testSpeed3(size);
			size *= 2;
		}
	}

	private static void testSpeed2(final int size) {
		byte[] b = new byte[size];
		r.nextBytes(b);
		long total = 0;
		long sum = 0;
		long tStart = System.nanoTime();
		final long process = 1 << 30;
		final long bucket = size * 8;
		while ((total + bucket) < process) {
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			sum += MurmurHash.murmur2(b);
			total += bucket;
		}
		while (total < process) {
			sum += MurmurHash.murmur2(b);
			total += size;
		}
		long time = System.nanoTime() - tStart;
		double mbPerSec = total / (time / 1000000000f) / (1024f * 1024f);
		double nsPerByte = ((double) time) / ((double) total);
		System.out.printf("murmur2 size=%5d total=%6d sum=%20d %5.3fms %3.3fmb/sec %.3fns/byte%n", size, total, sum, (time / 1000000f), mbPerSec,
				nsPerByte);
	}

	private static void testSpeed3(final int size) {
		byte[] b = new byte[size];
		r.nextBytes(b);
		long total = 0;
		long sum = 0;
		long tStart = System.nanoTime();
		final long process = 1 << 30;
		final long bucket = size * 8;
		while ((total + bucket) < process) {
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			sum += MurmurHash.murmur3(b);
			total += bucket;
		}
		while (total < process) {
			sum += MurmurHash.murmur3(b);
			total += size;
		}
		long time = System.nanoTime() - tStart;
		double mbPerSec = total / (time / 1000000000f) / (1024f * 1024f);
		double nsPerByte = ((double) time) / ((double) total);
		System.out.printf("murmur3 size=%5d total=%6d sum=%20d %5.3fms %3.3fmb/sec %.3fns/byte%n", size, total, sum, (time / 1000000f), mbPerSec,
				nsPerByte);
	}
}
