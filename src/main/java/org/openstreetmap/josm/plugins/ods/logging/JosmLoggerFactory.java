package org.openstreetmap.josm.plugins.ods.logging;

import java.util.logging.Logger;

import org.geotools.util.logging.LoggerFactory;
import org.openstreetmap.josm.tools.Logging;


/**
 * A factory for loggers that redirect all Java logging events to the Josm logs.
 */
public class JosmLoggerFactory extends LoggerFactory<Logger> {
    /**
     * The unique instance of this factory.
     */
    private static JosmLoggerFactory factory;

    /**
     * Constructs a default factory.
     *
     * @throws NoClassDefFoundError if Apache's {@code Log} class was not found on the classpath.
     */
    protected JosmLoggerFactory() throws NoClassDefFoundError {
        super(Logger.class);
    }

    /**
     * Returns the unique instance of this factory.
     *
     * @throws NoClassDefFoundError if Apache's {@code Log} class was not found on the classpath.
     */
    public static synchronized JosmLoggerFactory getInstance() throws NoClassDefFoundError {
        if (factory == null) {
            factory = new JosmLoggerFactory();
        }
        return factory;
    }

    /**
     * Returns the implementation to use for the logger of the specified name,
     * or {@code null} if the logger would delegates to Java logging anyway.
     */
    @Override
    protected Logger getImplementation(final String name) {
        return Logging.getLogger();
    }

    /**
     * Wraps the specified {@linkplain #getImplementation implementation} in a Java logger.
     */
    @Override
    protected Logger wrap(String name, Logger implementation) {
        return implementation;
    }

    /**
     * Returns the {@linkplain #getImplementation implementation} wrapped by the specified logger,
     * or {@code null} if none.
     */
    @Override
    protected Logger unwrap(final Logger logger) {
        return logger;
    }
}
