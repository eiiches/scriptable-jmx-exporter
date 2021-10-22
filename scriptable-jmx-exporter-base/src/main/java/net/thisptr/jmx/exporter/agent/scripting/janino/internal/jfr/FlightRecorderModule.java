package net.thisptr.jmx.exporter.agent.scripting.janino.internal.jfr;

import net.thisptr.jmx.exporter.agent.config.Config;
import net.thisptr.jmx.exporter.agent.scripting.FlightRecorderEventHandlerScript;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.util.List;

public interface FlightRecorderModule {
    FlightRecorderEventHandlerScript compile(ScriptEvaluator se, String script) throws CompileException;

    static FlightRecorderModule getInstance() {
        return FlightRecorderModuleImpl.getInstance();
    }

    FlightRecorder create(List<Config.FlightRecorderEventRule> rules);

    interface FlightRecorder extends AutoCloseable {
        void start();
    }
}
