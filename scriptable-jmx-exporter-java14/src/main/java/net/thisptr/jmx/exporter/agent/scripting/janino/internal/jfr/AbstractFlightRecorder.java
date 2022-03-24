package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import jdk.jfr.EventSettings;
import jdk.jfr.EventType;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;
import net.thisptr.jmx.exporter.agent.config.Config;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractFlightRecorder implements FlightRecorderModule.FlightRecorder {

    private final Set<String> configuredEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final List<Config.FlightRecorderEventRule> rules;
    protected final RecordingStream recorder;

    public AbstractFlightRecorder(final List<Config.FlightRecorderEventRule> rules) {
        this.recorder = new RecordingStream();
        this.recorder.setReuse(true);
        this.rules = rules;
    }

    private Config.FlightRecorderEventRule findRuleForEvent(final EventType type) {
        for (final Config.FlightRecorderEventRule rule : rules)
            if (rule.name.matcher(type.getName()).matches())
                return rule;
        return null;
    }

    protected void configureEvent(final EventType eventType) {
        if (configuredEvents.contains(eventType.getName()))
            return; /* already configured */
        configuredEvents.add(eventType.getName());

        final Config.FlightRecorderEventRule rule = findRuleForEvent(eventType);
        if (rule == null)
            return; /* no matching rules */
        if (rule.skip)
            return;

        if (rule.enable) {
            final EventSettings settings = recorder.enable(eventType.getName());
            if (rule.stacktrace != null) {
                if (rule.stacktrace) {
                    settings.withStackTrace();
                } else {
                    settings.withoutStackTrace();
                }
            }
            if (rule.interval != null)
                settings.withPeriod(rule.interval);
            if (rule.threshold != null)
                settings.withThreshold(rule.threshold);
            rule.settings.forEach(settings::with);
        }
        recorder.onEvent(eventType.getName(), (RecordedEvent event) -> {
            ((FlightRecorderEventHandler) rule.handler).execute(event);
        });
    }

    @Override
    public void start() {
        for (final EventType eventType : jdk.jfr.FlightRecorder.getFlightRecorder().getEventTypes())
            configureEvent(eventType);
        this.recorder.startAsync();
    }

    @Override
    public void close() {
        this.recorder.close();
    }
}
