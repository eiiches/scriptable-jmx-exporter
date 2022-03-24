package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import com.google.common.util.concurrent.Uninterruptibles;
import jdk.jfr.EventType;
import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.util.List;

/**
 * The actual implementation used when running on newer JVM (&gt;= Java 14).
 */
public class FlightRecorderModuleImpl implements FlightRecorderModule {

    @Override
    public FlightRecorderEventHandlerScript compile(final ScriptEvaluator se, final String script) throws CompileException {
        final FlightRecorderEventHandler compiledScript = se.createFastEvaluator(script, FlightRecorderEventHandler.class, new String[] { "event" });
        return compiledScript;
    }

    private static final FlightRecorderModuleImpl INSTANCE = new FlightRecorderModuleImpl();

    static FlightRecorderModule getInstance() {
        return INSTANCE;
    }

    private static class FlightRecorderImpl extends AbstractFlightRecorder {

        private final ConfigureThread configureThread = new ConfigureThread();

        private class ConfigureThread extends Thread implements AutoCloseable {
            private volatile boolean shutdownRequested = false;

            @Override
            public void run() {
                while (!shutdownRequested) {
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException e) {
                        break;
                    }
                    for (final EventType eventType : jdk.jfr.FlightRecorder.getFlightRecorder().getEventTypes())
                        configureEvent(eventType);
                }
            }

            @Override
            public void close() {
                shutdownRequested = true;
                interrupt();
                Uninterruptibles.joinUninterruptibly(this);
            }
        }

        public FlightRecorderImpl(final List<Config.FlightRecorderEventRule> rules) {
            super(rules);
        }

        @Override
        public void start() {
            super.start();
            configureThread.start();
        }

        @Override
        public void close() {
            configureThread.close();
            super.close();
        }
    }

    @Override
    public FlightRecorder create(final List<Config.FlightRecorderEventRule> rules) {
        return new FlightRecorderImpl(rules);
    }
}
