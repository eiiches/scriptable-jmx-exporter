package net.thisptr.jmx.exporter.agent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ThreadLocalRandom;

public class OutputStreamWritableByteChannel implements WritableByteChannel {
	private final OutputStream os;

	public OutputStreamWritableByteChannel(final OutputStream os) {
		this.os = os;
	}

	@Override
	public int write(final ByteBuffer buf) throws IOException {
		final int written = ThreadLocalRandom.current().nextInt(buf.remaining() + 1);
		os.write(buf.array(), buf.arrayOffset() + buf.position(), written);
		buf.position(buf.position() + written);
		return written;
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		os.close();
	}
}
