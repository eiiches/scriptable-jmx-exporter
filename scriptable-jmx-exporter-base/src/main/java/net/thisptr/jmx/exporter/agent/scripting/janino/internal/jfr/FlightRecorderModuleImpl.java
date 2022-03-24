package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.util.List;

/**
 * The default implementation used when running on older JVM (&lt; Java 14).
 */
public class FlightRecorderModuleImpl implements FlightRecorderModule {
    private static class DummyScript implements FlightRecorderEventHandlerScript {
    }

    @Override
    public FlightRecorderEventHandlerScript compile(final ScriptEvaluator se, final String script) throws CompileException {
        throw new RuntimeException("Flight Recorder Events feature is not supported on this Java VM. Use OpenJDK 14 or later.");
    }

    private static final FlightRecorderModuleImpl INSTANCE = new FlightRecorderModuleImpl();

    static FlightRecorderModule getInstance() {
        return INSTANCE;
    }

    private static class DummyFlightRecorder implements FlightRecorder {
        @Override
        public void start() {
            /* do nothing */
        }

        @Override
        public void close() {
            /* do nothing */
        }
    }

    @Override
    public FlightRecorder create(final List<Config.FlightRecorderEventRule> rules) {
        return new DummyFlightRecorder();
    }
}
