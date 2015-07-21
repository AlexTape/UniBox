package de.unibox.client.thread.implementation;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import de.unibox.client.api.ClientProvider;
//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class ThreadTaskImpl.
 */
public abstract class ThreadTaskImpl implements Runnable {

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/** The class name. */
	protected String className;

	/** The connection. */
	protected HttpURLConnection connection = null;

	/** The password. */
	protected final String password = ClientProvider.getPassword();

	/** The thread. */
	protected Thread thread;

	/** The url. */
	protected final String url = ClientProvider.getUrl();

	/** The url object. */
	protected URL urlObject = null;

	/** The username. */
	protected final String username = ClientProvider.getUsername();

	/** The writer. */
	protected OutputStreamWriter writer = null;

	/**
	 * Instantiates a new thread task impl.
	 */
	public ThreadTaskImpl() {
		this.className = this.getClass().getSimpleName();
	}

	/**
	 * Instantiates a new thread task impl.
	 *
	 * @param simpleClassName
	 *            the simple class name
	 */
	public ThreadTaskImpl(final String simpleClassName) {
		this.className = simpleClassName;
	}

	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	protected String getClassName() {
		return this.className;
	}

	/**
	 * Gets the thread.
	 *
	 * @return the thread
	 */
	public Thread getThread() {
		return this.thread;
	}

	/**
	 * Process.
	 */
	protected abstract void process();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ThreadTaskImpl.log.debug("linking");
		this.thread = Thread.currentThread();

		ThreadTaskImpl.log.debug(Thread.currentThread().getName() + ": "
				+ this.className + " starting..");

		this.process();

		ThreadTaskImpl.log.debug(Thread.currentThread().getName() + ": "
				+ this.className + " done..");
	}

	/**
	 * Sets the class name.
	 *
	 * @param className
	 *            the new class name
	 */
	protected void setClassName(final String className) {
		this.className = className;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.className;
	}
}