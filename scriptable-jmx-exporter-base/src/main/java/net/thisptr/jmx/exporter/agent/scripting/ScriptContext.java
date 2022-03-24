package net.thisptr.jmx.exporter.agent.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.ByteStreams;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import net.thisptr.jmx.exporter.agent.registry.Registry;

public class ScriptContext {
	private static class StaticBytecodeClassLoader extends ClassLoader {
		private final Map<String, byte[]> bytecodes = new HashMap<String, byte[]>();

		private static final byte[] registryClassBytes;
		static {
			try (final InputStream is = StaticBytecodeClassLoader.class.getClassLoader().getResourceAsStream(Registry.class.getName().replace('.', '/') + ".class")) {
				registryClassBytes = ByteStreams.toByteArray(is);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		public StaticBytecodeClassLoader(final ClassLoader parent) {
			super(parent);
			this.bytecodes.put(Registry.class.getName(), registryClassBytes);
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			if (Registry.class.getName().equals(name)) {
				synchronized (getClassLoadingLock(name)) {
					Class<?> c = findLoadedClass(name);
					if (c == null)
						c = findClass(name);
					if (resolve)
						resolveClass(c);
					return c;
				}
			}
			return super.loadClass(name, resolve);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			final byte[] bytecode = bytecodes.get(name);
			if (bytecode == null)
				throw new ClassNotFoundException(name);
			return defineClass(name, bytecode, 0, bytecode.length);
		}
	}

	private final StaticBytecodeClassLoader classLoader;
	private final List<String> topLevelClassNames = new ArrayList<>();

	public ScriptContext(final ClassLoader parentClassLoader) {
		this.classLoader = new StaticBytecodeClassLoader(parentClassLoader);
	}

	public void addDeclarations(final String topLevelClassName, final Map<String, byte[]> classBytes) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// since top level class names are unique, there won't be any duplicate classes
		this.classLoader.bytecodes.putAll(classBytes);
		this.topLevelClassNames.add(topLevelClassName);

		final Class<?> clazz = this.classLoader.loadClass(topLevelClassName);
		clazz.getConstructor().newInstance(); // force <clinit> now to fail fast and avoid later errors; TODO: same to nested static classes

		// TODO: validate no non-static member is defined
	}

	public List<String> declarationClassNames() {
		return topLevelClassNames;
	}

	public ClassLoader declarationClassLoader() {
		return this.classLoader;
	}

	public PrometheusMeterRegistry getRegistry() {
		try {
			final Class<?> clazz = this.classLoader.loadClass(Registry.class.getName());
			return (PrometheusMeterRegistry) clazz.getDeclaredMethod("getInstance").invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
