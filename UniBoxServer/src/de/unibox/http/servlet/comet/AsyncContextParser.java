package de.unibox.http.servlet.comet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.unibox.config.InternalConfig;
import de.unibox.core.provider.Helper;
import de.unibox.core.provider.Reversed;

/**
 * The Class AsyncContextParser is able to identify and handle unused contexts.
 */
public class AsyncContextParser {

	/** The list. */
	public static List<AsyncContextParser> list = null;

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/**
	 * Inits the.
	 */
	public static void init() {
		if (InternalConfig.isLogAsyncSessions()) {
			AsyncContextParser.log.debug(AsyncContextParser.class
					.getSimpleName() + ": init()");
		}
		AsyncContextParser.list = new ArrayList<AsyncContextParser>();
	}

	/**
	 * Verify.
	 */
	public static void verify() {

		final HashMap<String, Long> asyncSessions = new HashMap<String, Long>();

		for (final AsyncContextParser comSession : Reversed
				.reversed(AsyncContextParser.list)) {

			final AsyncContext ac = comSession.getContext();

			final String id = comSession.getId();
			final Long time = comSession.getSession().getCreationTime();

			if (asyncSessions.containsKey(id)
					&& asyncSessions.get(id).equals(time)) {
				if (InternalConfig.isLogAsyncSessions()) {
					AsyncContextParser.log.debug(AsyncContextParser.class
							.getSimpleName()
							+ ": removing id="
							+ comSession.getId()
							+ ", creationTime="
							+ comSession.getCreationTimeString()
							+ ", ac="
							+ comSession.getContext());
				}
				Communicator.asyncContextQueue.remove(ac);
				ac.dispatch();
//				ac.complete();
			} else {
				asyncSessions.put(id, time);
			}
		}
	}

	/** The context. */
	private AsyncContext context = null;

	/** The creation time. */
	private long creationTime = 0;

	/** The creation time string. */
	private String creationTimeString = null;

	/** The id. */
	private String id = null;

	/** The session. */
	private HttpSession session = null;

	/**
	 * Instantiates a new async context parser.
	 *
	 * @param thisContext
	 *            the this context
	 */
	public AsyncContextParser(final AsyncContext thisContext) {
		super();
		this.context = thisContext;
		HttpServletRequest req = null;
		try {
			req = (HttpServletRequest) thisContext.getRequest();
		} catch (final IllegalStateException e) {
			if (InternalConfig.isLogAsyncSessions()) {
				e.printStackTrace();
			}
		}

		if (null != req) {
			this.session = req.getSession();
			this.id = this.session.getId();
			this.creationTime = this.session.getCreationTime();
			this.creationTimeString = Helper.longToDate(this.session
					.getCreationTime());
			if (InternalConfig.isLogAsyncSessions()) {
				AsyncContextParser.log.debug(AsyncContextParser.class
						.getSimpleName() + ": add Context: " + thisContext);
				AsyncContextParser.log.debug(AsyncContextParser.class
						.getSimpleName()
						+ ": adding id="
						+ this.id
						+ ", creationTime="
						+ this.creationTimeString
						+ ", ac="
						+ this.context);
			}
			AsyncContextParser.list.add(this);
		} else {
			if (InternalConfig.isLogAsyncSessions()) {
				AsyncContextParser.log.debug(AsyncContextParser.class
						.getSimpleName()
						+ ": illegal state for context detected: "
						+ thisContext);
			}
		}
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public AsyncContext getContext() {
		return this.context;
	}

	/**
	 * Gets the creation time.
	 *
	 * @return the creation time
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Gets the creation time string.
	 *
	 * @return the creation time string
	 */
	public String getCreationTimeString() {
		return this.creationTimeString;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public HttpSession getSession() {
		return this.session;
	}

}
