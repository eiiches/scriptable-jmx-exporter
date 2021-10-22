package net.thisptr.jmx.exporter.tools;

public class Pause {
	public static void main(final String[] args) throws Exception {
		System.out.println("Press Ctrl-C to exit.");
		while (true) {
			Thread.sleep(10000L);
		}
	}
}
