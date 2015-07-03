package de.unibox.client.thread.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import de.unibox.client.api.ClientProvider;
import de.unibox.client.thread.implementation.ThreadTaskImpl;
import de.unibox.core.network.object.CommunicatorMessage;
import de.unibox.core.provider.ObjectSerializerImpl;

/**
 * The listener interface for receiving runnableComet events. The class that is
 * interested in processing a runnableComet event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addRunnableCometListener<code> method. When
 * the runnableComet event occurs, that object's appropriate
 * method is invoked.
 * 
 * Incoming Messages will be handled here.
 *
 * @see java.util.concurrent.BlockingQueue
 */
public class RunnableCometListener extends ThreadTaskImpl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unibox.client.thread.implementation.ThreadTaskImpl#process()
	 */
	@Override
	protected void process() {

		URL url;
		HttpURLConnection connection;

		try {

			final String urlString = ClientProvider.getUrl()
					+ ClientProvider.getCometURL() + "?0";

			ThreadTaskImpl.log.info("RunnableCometListener is connecting to: "
					+ urlString);

			url = new URL(urlString);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setUseCaches(false);
			connection.addRequestProperty("Cookie", ClientProvider.getCookie());

			// to circumvent timeout issue, i decided to check the online state
			// after 30 seconds. if the webservice gets offline, the clients
			// will receive a
			// SocketTimeoutException.

			// Reconnect if server did not respond in 5 seconds
			connection.setConnectTimeout(5000);
			// Reconnect handle if nothing received in the last 30 seconds
			connection.setReadTimeout(5000);

			for (final Entry<String, List<String>> header : connection
					.getHeaderFields().entrySet()) {
				ThreadTaskImpl.log.debug("ResponseHeader: " + header.getKey()
						+ "=" + header.getValue());
			}

			BufferedReader areader = null;

			try {
				areader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			} catch (final SocketTimeoutException e) {
				throw new IOException("Server offline?");
			} catch (final IOException e) {
				// do nothing, this case will be present, if the server gets
				// online after downtime. not covered atm.
			}

			try {
				for (String line; (line = areader.readLine()) != null;) {
					final CommunicatorMessage message = ObjectSerializerImpl
							.stringToObject(line, CommunicatorMessage.class);
					ThreadTaskImpl.log.info(RunnableMessageSender.class
							.getSimpleName() + "recieving: " + message);
					ClientProvider.recieveMessage(message);
				}
			} catch (final SocketTimeoutException e) {
				// just rebooting the litener instance after fired
				// setReadTimeout(),
				// setConnectTimeout()
				ThreadTaskImpl.log.info(RunnableMessageSender.class
						.getSimpleName() + ": Rebooting listener instance..!");
				this.run();
			}
		} catch (final IOException e) {
			ThreadTaskImpl.log.warn(RunnableMessageSender.class.getSimpleName()
					+ ": Fatal error due HTTP transaction.");
			if (e instanceof ConnectException) {
				ThreadTaskImpl.log.info(RunnableMessageSender.class
						.getSimpleName()
						+ ": maybe server offline? "
						+ this.url);
			}
			e.printStackTrace();
		} finally {
			ThreadTaskImpl.log.info(RunnableMessageSender.class.getSimpleName()
					+ ": Listener shutdown!");
		}
	}

}
