package de.unibox.client.thread;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.unibox.client.thread.implementation.RejectedExecutionHandlerImpl;
import de.unibox.client.thread.implementation.ThreadTaskImpl;
import de.unibox.client.thread.runnable.RunnableThreadMonitor;
//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class ThreadEngine.
 */
public class ThreadEngine {

	/** The instance. */
	private static ThreadEngine instance;

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/**
	 * Gets the single instance of ThreadEngine.
	 *
	 * @return single instance of ThreadEngine
	 */
	public static ThreadEngine getInstance() {
		if (ThreadEngine.instance == null) {
			ThreadEngine.instance = new ThreadEngine();
		}
		return ThreadEngine.instance;
	}

	/** The core pool size. */
	private final int corePoolSize = 8;

	/** The executor pool. */
	protected ThreadPoolExecutor executorPool;

	/** The factory. */
	protected ThreadFactory factory;

	/** The futures. */
	public Collection<Future<?>> futures;

	/** The is monitor. */
	protected boolean isMonitor = false;

	/** The keep alive time. */
	private final int keepAliveTime = 10;

	/** The maximum pool size. */
	private final int maximumPoolSize = 10;

	/** The monitor. */
	protected RunnableThreadMonitor monitor;

	/** The rejection handler. */
	protected RejectedExecutionHandlerImpl rejectionHandler;

	/**
	 * Instantiates a new thread engine.
	 */
	private ThreadEngine() {
		this.rejectionHandler = new RejectedExecutionHandlerImpl();
		this.factory = new ConcreteThreadFactory().build();
		this.futures = new LinkedList<Future<?>>();
		this.boot();
	}

	/**
	 * Block till done.
	 */
	public void blockTillDone() {
		ThreadEngine.log.debug(this.getClass().getSimpleName()
				+ ": blockTillDone()");
		for (final Future<?> future : this.getFutures()) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				ThreadEngine.log.error(this.getClass().getSimpleName()
						+ ": blockTillDone() failed!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Boot.
	 */
	protected void boot() {
		ThreadEngine.log.debug(this.getClass().getSimpleName() + ": boot()");
		this.executorPool = new ThreadPoolExecutor(this.corePoolSize,
				this.maximumPoolSize, this.keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(6), this.factory,
				this.rejectionHandler);
	}

	/**
	 * Disable monitoring.
	 */
	private void disableMonitoring() {
		ThreadEngine.log.debug(this.getClass().getSimpleName()
				+ ": disableMonitoring()");
		if (this.isMonitor) {
			this.monitor.shutdown();
		} else {
			ThreadEngine.log.debug(this.getClass().getSimpleName()
					+ ": monitor not running");
		}
	}

	/**
	 * Enable monitoring.
	 */
	private void enableMonitoring() {
		ThreadEngine.log.debug(this.getClass().getSimpleName()
				+ ": enableMonitoring()");
		this.monitor = new RunnableThreadMonitor(this.executorPool, 3);
		final Thread monitorThread = new Thread(this.monitor);
		monitorThread.setDaemon(true);
		monitorThread.start();
		this.isMonitor = true;
	}

	/**
	 * Gets the futures.
	 *
	 * @return the futures
	 */
	public Collection<Future<?>> getFutures() {
		return this.futures;
	}

	/**
	 * Checks if is monitor.
	 *
	 * @return true, if is monitor
	 */
	public boolean isMonitor() {
		return this.isMonitor;
	}

	/**
	 * Run.
	 *
	 * @param myRunnable
	 *            the my runnable
	 */
	public void run(final ThreadTaskImpl myRunnable) {
		this.futures.add(this.executorPool.submit(myRunnable));
	}

	/**
	 * Sets the monitor.
	 *
	 * @param isMonitor
	 *            the new monitor
	 */
	public void setMonitor(final boolean isMonitor) {
		this.isMonitor = isMonitor;
		if (this.isMonitor == true) {
			this.enableMonitoring();
		} else {
			this.disableMonitoring();
		}

	}

	/**
	 * Shutdown.
	 */
	protected void shutdown() {
		ThreadEngine.log
				.debug(this.getClass().getSimpleName() + ": shutdown()");
		try {
			if (this.executorPool.getActiveCount() > 0) {
				ThreadEngine.log.debug(this.getClass().getSimpleName() + ": "
						+ this.executorPool.getActiveCount()
						+ " Tasks still running.. waiting..");
				this.executorPool.awaitTermination(30, TimeUnit.SECONDS);
			}
			this.executorPool.shutdown();
			this.disableMonitoring();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}