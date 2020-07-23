package net.thisptr.jmx.exporter.agent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class OutputStreamWritableByteChannel implements WritableByteChannel {
	private final OutputStream os;

	public OutputStreamWritableByteChannel(final OutputStream os) {
		this.os = os;
	}

	@Override
	public int write(final ByteBuffer buf) throws IOException {
		os.write(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
		final int written = buf.remaining();
		buf.position(buf.limit());
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
