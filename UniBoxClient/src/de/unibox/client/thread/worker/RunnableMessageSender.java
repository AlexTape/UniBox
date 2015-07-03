package de.unibox.client.thread.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import de.unibox.client.api.ClientProvider;
import de.unibox.client.thread.implementation.ThreadTaskImpl;
import de.unibox.core.network.object.CommunicatorMessage;
import de.unibox.core.provider.ObjectSerializerImpl;

/**
 * The Class RunnableMessageSender.
 */
public class RunnableMessageSender extends ThreadTaskImpl {

	// method:POST action=post message=...

	/** The enabled. */
	public boolean enabled = true;

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

				ThreadTaskImpl.log.debug(RunnableMessageSender.class
						.getSimpleName() + ": next iteration..");

				this.send(ClientProvider.getNextOutgoingMessage());
			}
		} catch (final InterruptedException e) {
			ThreadTaskImpl.log.debug(RunnableMessageSender.class
					.getSimpleName()
					+ ": Not able to get next outgoing message from qeue.");
			e.printStackTrace();
		} finally {
			ThreadTaskImpl.log.debug(RunnableMessageMediator.class
					.getSimpleName() + ": done.");
		}
	}

	/**
	 * Send.
	 *
	 * @param message
	 *            the message
	 */
	private void send(final CommunicatorMessage message) {

		final String recieverUrl = this.url + ClientProvider.getRecieverURL();

		try {
			ThreadTaskImpl.log.debug("RunnableMessageSender preparing message:"
					+ message);
			ThreadTaskImpl.log.debug("RunnableMessageSender receiver url: "
					+ recieverUrl);

			try {
				this.urlObject = new URL(recieverUrl);
			} catch (final MalformedURLException e) {
				ThreadTaskImpl.log.warn(RunnableMessageSender.class
						.getSimpleName() + ": invalid URL detected.");
				e.printStackTrace();
			}

			ThreadTaskImpl.log.debug(RunnableMessageSender.class
					.getSimpleName()
					+ ": Try to etablish connection to "
					+ this.urlObject.getPath());

			String body = null;

			try {
				body = "action=push&message="
						+ URLEncoder.encode(
								ObjectSerializerImpl.objectToString(message),
								"UTF-8");

			} catch (final UnsupportedEncodingException e1) {
				ThreadTaskImpl.log.error(RunnableMessageSender.class
						.getSimpleName()
						+ ": FATAL ERROR - charset issue in parameters.");
				e1.printStackTrace();
			}

			try {
				this.connection = (HttpURLConnection) this.urlObject
						.openConnection();

				this.connection.setRequestMethod("POST");
				this.connection.setDoInput(true);
				this.connection.setDoOutput(true);
				this.connection.setUseCaches(false);
				this.connection.addRequestProperty("Cookie",
						ClientProvider.getCookie());
				this.connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				this.connection.setRequestProperty("Content-Length",
						String.valueOf(body.length()));

				final OutputStream outStream = this.connection
						.getOutputStream();
				this.writer = new OutputStreamWriter(outStream);
				this.writer.write(body);
				this.writer.flush();
				this.writer.close();

				final BufferedReader areader = new BufferedReader(
						new InputStreamReader(this.connection.getInputStream()));

				for (String line; (line = areader.readLine()) != null;) {
					ThreadTaskImpl.log.debug(RunnableMessageSender.class
							.getSimpleName() + " Result: " + line);
					ClientProvider.recieveMessage(message);
				}
			} catch (final IOException e) {
				ThreadTaskImpl.log.error(RunnableMessageSender.class
						.getSimpleName()
						+ ": Fatal error due HTTP transaction.");
				if (e instanceof ConnectException) {
					ThreadTaskImpl.log.warn(RunnableMessageSender.class
							.getSimpleName()
							+ ": maybe server offline? "
							+ this.url);
				}
				e.printStackTrace();
			}
		} finally {
			ThreadTaskImpl.log.debug(RunnableMessageSender.class
					.getSimpleName() + ": done.");
		}
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
