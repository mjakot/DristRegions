package dristmine.dristregions;

import java.util.UUID;

public class UUIDGenerator {
	public static class UUIDOverflowException extends Exception {
		public UUIDOverflowException() {
			super("Region uuid overflow");
		}
	}

	private UUID currentUUID;

	public UUIDGenerator(String lastUUID) throws UUIDOverflowException {
		if (lastUUID.equals("default"))
			currentUUID = new UUID(0L, 0L);
		else
			currentUUID = UUID.fromString(lastUUID);
	}

	public UUID next() throws UUIDOverflowException {
		long mostSignificantBits = currentUUID.getMostSignificantBits();
		long leastSignificantBits = currentUUID.getLeastSignificantBits();

		if (mostSignificantBits == Long.MAX_VALUE)
			throw new UUIDOverflowException();

		if (leastSignificantBits == Long.MAX_VALUE) {
			leastSignificantBits = 0;

			mostSignificantBits++;
		} else {
			leastSignificantBits++;
		}

		return currentUUID = new UUID(mostSignificantBits, leastSignificantBits);
	}

	public UUID previous() throws UUIDOverflowException {
		long mostSignificantBits = currentUUID.getMostSignificantBits();
		long leastSignificantBits = currentUUID.getLeastSignificantBits();

		if (mostSignificantBits == 0)
			throw new UUIDOverflowException();

		if (leastSignificantBits == 0) {
			leastSignificantBits = Long.MAX_VALUE;

			mostSignificantBits--;
		} else {
			leastSignificantBits--;
		}

		return currentUUID = new UUID(mostSignificantBits, leastSignificantBits);
	}
}
