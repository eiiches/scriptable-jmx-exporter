package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import jdk.jfr.consumer.RecordedEvent;
import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;

public interface FlightRecorderEventHandler extends FlightRecorderEventHandlerScript {
    void execute(final RecordedEvent event);
}
