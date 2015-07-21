package de.unibox.client.thread.implementation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class ThreadFactoryImpl.
 */
public abstract class ThreadFactoryImpl {

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/** The backing thread factory. */
	private ThreadFactory backingThreadFactory = null;

	/** The daemon. */
	private boolean daemon = false;

	/** The name prefix. */
	private String namePrefix = null;

	/** The priority. */
	private int priority = Thread.NORM_PRIORITY;

	/** The uncaught exception handler. */
	private UncaughtExceptionHandler uncaughtExceptionHandler = null;

	/**
	 * Builds the.
	 *
	 * @return the thread factory
	 */
	public ThreadFactory build() {
		return this.build(this);
	}

	/**
	 * Builds the.
	 *
	 * @param builder
	 *            the builder
	 * @return the thread factory
	 */
	protected ThreadFactory build(final ThreadFactoryImpl builder) {
		final String namePrefix = builder.namePrefix;
		final Boolean daemon = builder.daemon;
		final Integer priority = builder.priority;
		final UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
		final ThreadFactory backingThreadFactory = (builder.backingThreadFactory != null) ? builder.backingThreadFactory
				: Executors.defaultThreadFactory();

		final AtomicLong count = new AtomicLong(0);

		return new ThreadFactory() {
			@Override
			public Thread newThread(final Runnable runnable) {
				final Thread thread = backingThreadFactory.newThread(runnable);
				if (namePrefix != null) {
					thread.setName(namePrefix + "-" + count.getAndIncrement());
				}
				if (daemon != null) {
					thread.setDaemon(daemon);
				}
				if (priority != null) {
					thread.setPriority(priority);
				}
				if (uncaughtExceptionHandler != null) {
					thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
				}
				return thread;
			}
		};
	}

	/**
	 * Sets the daemon.
	 *
	 * @param daemon
	 *            the daemon
	 * @return the thread factory impl
	 */
	public ThreadFactoryImpl setDaemon(final boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	/**
	 * Sets the name prefix.
	 *
	 * @param namePrefix
	 *            the name prefix
	 * @return the thread factory impl
	 */
	public ThreadFactoryImpl setNamePrefix(final String namePrefix) {
		if (namePrefix == null) {
			throw new NullPointerException();
		}
		this.namePrefix = namePrefix;
		return this;
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority
	 *            the priority
	 * @return the thread factory impl
	 */
	public ThreadFactoryImpl setPriority(final int priority) {
		if (priority < Thread.MIN_PRIORITY) {
			throw new IllegalArgumentException(String.format(
					"Thread priority (%s) must be >= %s", priority,
					Thread.MIN_PRIORITY));
		}

		if (priority > Thread.MAX_PRIORITY) {
			throw new IllegalArgumentException(String.format(
					"Thread priority (%s) must be <= %s", priority,
					Thread.MAX_PRIORITY));
		}

		this.priority = priority;
		return this;
	}

	/**
	 * Sets the thread factory.
	 *
	 * @param backingThreadFactory
	 *            the backing thread factory
	 * @return the thread factory impl
	 */
	public ThreadFactoryImpl setThreadFactory(
			final ThreadFactory backingThreadFactory) {
		if (null == this.uncaughtExceptionHandler) {
			throw new NullPointerException(
					"BackingThreadFactory cannot be null");
		}
		this.backingThreadFactory = backingThreadFactory;
		return this;
	}

	/**
	 * Sets the uncaught exception handler.
	 *
	 * @param uncaughtExceptionHandler
	 *            the uncaught exception handler
	 * @return the thread factory impl
	 */
	public ThreadFactoryImpl setUncaughtExceptionHandler(
			final UncaughtExceptionHandler uncaughtExceptionHandler) {
		if (null == uncaughtExceptionHandler) {
			throw new NullPointerException(
					"UncaughtExceptionHandler cannot be null");
		}
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		return this;
	}
}
