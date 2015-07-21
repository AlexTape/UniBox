package de.unibox.client.thread.worker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import de.unibox.client.api.ClientProvider;
import de.unibox.client.events.DatabaseEvent;
import de.unibox.client.thread.implementation.ThreadTaskImpl;

/**
 * The Class RunnableDatabaseAgent.
 */
public class RunnableDatabaseAgent extends ThreadTaskImpl {

	/** The enabled. */
	public boolean enabled = true;

	/**
	 * Gets the response as string.
	 *
	 * @param thisInputStream
	 *            the this input stream
	 * @return the response as string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String getResponseAsString(final InputStream thisInputStream)
			throws IOException {
		final StringBuilder returnThis = new StringBuilder("");
		final Reader in = new BufferedReader(new InputStreamReader(
				thisInputStream, "UTF-8"));
		for (int c; (c = in.read()) >= 0; returnThis.append(c)) {
			;
		}
		return returnThis.toString();
	}

	/**
	 * Handle.
	 *
	 * @param query
	 *            the query
	 */
	private void handle(final DatabaseEvent query) {

		try {
			ThreadTaskImpl.log.debug(this.getClass().getSimpleName()
					+ " preparing query:" + query);

			String result = null;

			try {

				switch (query.getRequestType()) {
				case METHOD_POST:
					result = this.requestPost(query.getParameter());
					break;
				case METHOD_GET:
					result = this.requestGet(query.getParameter());
					break;
				default:
					throw new IOException("UNKNOWN_REQUEST_TYPE");
				}

			} catch (final IOException e) {
				ThreadTaskImpl.log.warn(RunnableDatabaseAgent.class
						.getSimpleName()
						+ ": Fatal error due HTTP transaction.");
				if (e instanceof ConnectException) {
					ThreadTaskImpl.log.info(RunnableDatabaseAgent.class
							.getSimpleName()
							+ ": maybe server offline? "
							+ this.url);
				}
				e.printStackTrace();
			}

			ThreadTaskImpl.log.debug(RunnableDatabaseAgent.class
					.getSimpleName() + ": Result=" + result);

		} finally {
			ThreadTaskImpl.log.debug(RunnableDatabaseAgent.class
					.getSimpleName() + ": done.");
		}
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public synchronized final boolean isEnabled() {
		return this.enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unibox.client.thread.implementation.ThreadTaskImpl#process()
	 */
	@Override
	protected void process() {

		try {
			ThreadTaskImpl.log.debug(RunnableMessageMediator.class
					.getSimpleName() + ": running..");
			while (this.enabled) {

				ThreadTaskImpl.log.debug(RunnableDatabaseAgent.class
						.getSimpleName() + ": next iteration..");

				this.handle(ClientProvider.getOutgoingDatabaseEvents().take());
			}
		} catch (final InterruptedException e) {
			ThreadTaskImpl.log
					.debug(RunnableDatabaseAgent.class.getSimpleName()
							+ ": Not able to get next outgoing database event from qeue.");
			e.printStackTrace();
		} finally {
			ThreadTaskImpl.log.debug(RunnableMessageMediator.class
					.getSimpleName() + ": done.");
		}
	}

	/**
	 * Request get.
	 *
	 * @param content
	 *            the content
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private final String requestGet(final String content) throws IOException {

		final String databaseURL = this.url + ClientProvider.getDatabaseURL();
		this.urlObject = new URL(databaseURL + "?" + content);

		ThreadTaskImpl.log
				.info(RunnableDatabaseAgent.class.getSimpleName()
						+ ": Try to etablish connection to "
						+ this.urlObject.getPath());

		this.connection = (HttpURLConnection) this.urlObject.openConnection();
		this.connection.setRequestMethod("GET");
		this.connection.setUseCaches(false);
		this.connection
				.addRequestProperty("Cookie", ClientProvider.getCookie());
		this.connection.setRequestProperty("Content-Type", "text/html");

		return this.getResponseAsString(this.connection.getInputStream());
	}

	/**
	 * Request post.
	 *
	 * @param content
	 *            the content
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private final String requestPost(final String content) throws IOException {

		final String databaseURL = this.url + ClientProvider.getDatabaseURL();
		this.urlObject = new URL(databaseURL);

		ThreadTaskImpl.log
				.info(RunnableDatabaseAgent.class.getSimpleName()
						+ ": Try to etablish connection to "
						+ this.urlObject.getPath());

		final byte[] postData = content.getBytes(Charset.forName("UTF-8"));
		final int postDataLength = postData.length;

		this.connection = (HttpURLConnection) this.urlObject.openConnection();
		this.connection.setRequestMethod("POST");
		this.connection.setDoInput(true);
		this.connection.setDoOutput(true);
		this.connection.setUseCaches(false);
		this.connection
				.addRequestProperty("Cookie", ClientProvider.getCookie());
		this.connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		this.connection.setRequestProperty("charset", "utf-8");
		this.connection.setRequestProperty("Content-Length",
				Integer.toString(postDataLength));

		try (DataOutputStream wr = new DataOutputStream(
				this.connection.getOutputStream())) {

			wr.write(postData);

		} catch (final Exception e) {
			ThreadTaskImpl.log.warn(RunnableDatabaseAgent.class.getSimpleName()
					+ ": Could not write parameters");
			e.printStackTrace();
		}

		return this.getResponseAsString(this.connection.getInputStream());
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled
	 *            the new enabled
	 */
	public synchronized final void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}
}
