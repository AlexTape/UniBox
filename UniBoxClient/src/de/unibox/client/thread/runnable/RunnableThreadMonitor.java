package de.unibox.client.thread.runnable;

import java.util.concurrent.ThreadPoolExecutor;

//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class RunnableThreadMonitor.
 */
public class RunnableThreadMonitor implements Runnable {

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/** The executor. */
	private final ThreadPoolExecutor executor;

	/** The interval. */
	private final int interval = 10000; // 10 seconds

	/** The run. */
	private boolean run = true;

	/** The seconds. */
	private final int seconds;

	/**
	 * Instantiates a new runnable thread monitor.
	 *
	 * @param executor
	 *            the executor
	 * @param delay
	 *            the delay
	 */
	public RunnableThreadMonitor(final ThreadPoolExecutor executor,
			final int delay) {
		this.executor = executor;
		this.seconds = delay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (this.run) {
			RunnableThreadMonitor.log
					.debug(String.format(
							RunnableThreadMonitor.class.getSimpleName()
									+ ": Pool: [%d/%d] Active: %d, Completed: %d, Tasks: %d, isShutdown: %s, isTerminated: %s",
							this.executor.getPoolSize(),
							this.executor.getCorePoolSize(),
							this.executor.getActiveCount(),
							this.executor.getCompletedTaskCount(),
							this.executor.getTaskCount(),
							this.executor.isShutdown(),
							this.executor.isTerminated()));
			try {
				Thread.sleep(this.seconds * this.interval);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Shutdown.
	 */
	public void shutdown() {
		this.run = false;
	}
}