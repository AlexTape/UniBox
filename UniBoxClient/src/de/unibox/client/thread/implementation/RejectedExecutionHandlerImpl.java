package de.unibox.client.thread.implementation;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class RejectedExecutionHandlerImpl.
 */
public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.
	 * lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
	 */
	@Override
	public void rejectedExecution(final Runnable r,
			final ThreadPoolExecutor executor) {
		RejectedExecutionHandlerImpl.log.debug(this.getClass().getSimpleName()
				+ ": " + r.toString() + " is rejected");
	}

}