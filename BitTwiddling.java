public final class BitTwiddling {

	private BitTwiddling() {}

	/**
	 * population count
	 * 
	 * naive solution, iterate over the integer and
	 * incrememt count if least significant bit is set.
	 * 
	 * O(n) where n is the size of the integer in bit.
	 */
	private static int iterated(final long v) {

		assert (v > 0);

		long value = v;

		int count = 0;

		while (value != 0) {
			count += value & 1;
			value >>= 1;
		}

		return count;
	}

	private static int sparse(final long v) {

		assert (v > 0);

		long value = v;

		int count = 0;

		while (value != 0) {
			count++;
			value &= (value - 1);
		}

		return count;
	}

	private static int dense(final long v) {

		assert (v > 0);

		long value = v;

		int count = 64;

		value ^= 0xffffffffffffffffL;

		while (value != 0) {
			count--;
			value &= (value - 1);
		}

		return count;
	}

	private static final int NUMBER_8_BIT_VALUES = 1 << 8;

	private static final byte[] PRECOMPUTED_8BIT_POPCOUNT = new byte[NUMBER_8_BIT_VALUES];

	static {
		for (int i = 0; i < NUMBER_8_BIT_VALUES; ++i) {
			PRECOMPUTED_8BIT_POPCOUNT[i] = (byte) iterated(i);
		}
	}

	private static int precompute8 (final long v) {

		final long bitMask = 0x00000000000000ff;

		long value = v;

		int count = 0;

		for (int i = 0; i < 8; ++i) {
			int index = (int)(value & bitMask);
			count += PRECOMPUTED_8BIT_POPCOUNT[index];
			value >>>= 8;
		}

		return count;
	}

	private static final int NUMBER_16_BIT_VALUES = 1 << 16;

	private static final byte[] PRECOMPUTED_16_BIT_POPCOUNT = new byte[NUMBER_16_BIT_VALUES];

	static {
		for (int i = 0; i < NUMBER_16_BIT_VALUES; ++i) {
			PRECOMPUTED_16_BIT_POPCOUNT[i] = (byte) iterated(i);
		}
	}

	private static int precompute16 (final long v) {

		final long bitMask = 0x000000000000ffff;

		long value = v;

		int count = 0;

		for (int i = 0; i < 4; ++i) {
			int index = (int)(value & bitMask);
			count += PRECOMPUTED_16_BIT_POPCOUNT[index];
			value >>>= 16;
		}

		return count;
	}


	private static final long[] FOLDING_BIT_MASK = {
		0x5555555555555555L,
		0x3333333333333333L,
		0x0f0f0f0f0f0f0f0fL,
		0x00ff00ff00ff00ffL,
		0x0000ffff0000ffffL,
		0x00000000ffffffffL
	};

	private static int fold(final long v) {

		long value = v;

		for (int i = 0; i < 6; i++) {
			value = (value & FOLDING_BIT_MASK[i]) + ((value >>> (1 << i)) & FOLDING_BIT_MASK[i]);
		}

		return (int) value;
	}

	/**
	 * reverse an integer in place
	 * 
	 * switch halves of the integer, then repeat the same
	 * in each half until we finish by switching one bit
	 * wide parts.
	 * 
	 * Example:
	 * 
	 * 0: 0a0b       0c0d
	 * 1: 0c  0d  0a   0b
	 * 2: 0 d 0 c 0 b 0 a
	 * 3: d 0 c 0 b 0 a 0
	 * 
	 * O(log n), where n is the number of bits in the integer.
	 */
	private static long reverse(final long v) {

		long value = v;

		value = (value >>> 32) + (value << 32);

		value = ((value & 0xffff0000ffff0000L) >>> 16) + ((value & 0x0000ffff0000ffffL) << 16);

		value = ((value & 0xff00ff00ff00ff00L) >>> 8) + ((value & 0x00ff00ff00ff00ffL) << 8);

		value = ((value & 0xf0f0f0f0f0f0f0f0L) >>> 4) + ((value & 0x0f0f0f0f0f0f0f0fL) << 4);

		value = ((value & 0xccccccccccccccccL) >>> 2) + ((value & 0x3333333333333333L) << 2);

		value = ((value & 0xaaaaaaaaaaaaaaaaL) >>> 1) + ((value & 0x5555555555555555L) << 1);

		return value;
	}

	public static void main(String... args) {
		System.out.printf("%64s\n", Long.toBinaryString(0x3333333333333333L));

		System.out.printf("%64s\n", Long.toBinaryString(0xccccccccccccccccL));

		System.out.printf("%64s\n", Long.toBinaryString(0x5555555555555555L));

		System.out.printf("%64s\n", Long.toBinaryString(0xaaaaaaaaaaaaaaaaL));
	}
}
