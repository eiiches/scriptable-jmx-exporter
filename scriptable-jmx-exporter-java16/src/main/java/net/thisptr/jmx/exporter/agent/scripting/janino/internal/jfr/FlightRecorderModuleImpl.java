package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import jdk.jfr.EventType;
import jdk.jfr.consumer.MetadataEvent;
import jdk.jfr.consumer.RecordedEvent;
import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.util.List;

/**
 * The actual implementation used when running on newer JVM (&gt;= Java 16).
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

        public FlightRecorderImpl(final List<Config.FlightRecorderEventRule> rules) {
            super(rules);
            recorder.onMetadata(this::onMetadata);
        }

        private void onMetadata(final MetadataEvent metadataEvent) {
            for (final EventType eventType : metadataEvent.getAddedEventTypes())
                configureEvent(eventType);
        }
    }

    @Override
    public FlightRecorder create(final List<Config.FlightRecorderEventRule> rules) {
        return new FlightRecorderImpl(rules);
    }
}
