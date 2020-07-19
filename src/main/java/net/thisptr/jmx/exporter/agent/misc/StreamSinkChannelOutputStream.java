package net.thisptr.jmx.exporter.agent.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.xnio.channels.StreamSinkChannel;

public class StreamSinkChannelOutputStream extends OutputStream {
	private final StreamSinkChannel channel;
	private final ByteBuffer buf;

	public StreamSinkChannelOutputStream(final ByteBuffer buf, final StreamSinkChannel channel) {
		this.buf = buf;
		this.channel = channel;
	}

	@Override
	public void write(final int b) throws IOException {
		if (!buf.hasRemaining())
			flush();
		buf.put((byte) b);
	}

	@Override
	public void flush() throws IOException {
		buf.flip();
		while (true) {
			channel.write(buf); // Let's first try without awaitWritable() which is somewhat CPU intensive.
			if (!buf.hasRemaining())
				break;
			channel.awaitWritable();
		}
		buf.flip();
	}

	@Override
	public void write(final byte[] bytes, int offset, int length) throws IOException {
		while (true) {
			int remaining = buf.remaining();
			if (length > remaining) {
				buf.put(bytes, offset, remaining);
				offset += remaining;
				length -= remaining;
				flush();
			} else {
				buf.put(bytes, offset, length);
				return;
			}
		}
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}