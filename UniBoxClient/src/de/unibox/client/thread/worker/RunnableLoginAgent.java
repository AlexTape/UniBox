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
import java.util.List;
import java.util.Map.Entry;

import de.unibox.client.api.ClientProvider;
import de.unibox.client.thread.implementation.ThreadTaskImpl;

/**
 * The Class RunnableLoginAgent.
 */
public class RunnableLoginAgent extends ThreadTaskImpl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unibox.client.thread.implementation.ThreadTaskImpl#process()
	 */
	@Override
	protected void process() {

		try {

			try {
				this.urlObject = new URL(this.url
						+ ClientProvider.getCometURL());
			} catch (final MalformedURLException e) {
				ThreadTaskImpl.log.warn(RunnableLoginAgent.class
						.getSimpleName() + ": invalid URL detected.");
				e.printStackTrace();
			}

			ThreadTaskImpl.log.info(RunnableLoginAgent.class.getSimpleName()
					+ ": Try to etablish connection to "
					+ this.urlObject.getPath());

			String body = null;
			try {
				body = "nick=" + URLEncoder.encode(this.username, "UTF-8")
						+ "&action=connect&" + "password=" + this.password;
			} catch (final UnsupportedEncodingException e) {
				ThreadTaskImpl.log.warn(RunnableLoginAgent.class
						.getSimpleName() + ": Unsupported charset required.");
				e.printStackTrace();
			}

			try {

				this.connection = (HttpURLConnection) this.urlObject
						.openConnection();

				this.connection.setRequestMethod("POST");
				this.connection.setDoInput(true);
				this.connection.setDoOutput(true);
				this.connection.setUseCaches(false);
				this.connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				this.connection.setRequestProperty("Content-Length",
						String.valueOf(body.length()));

				try {
					final OutputStream outStream = this.connection
							.getOutputStream();
					this.writer = new OutputStreamWriter(outStream);
					this.writer.write(body);
					this.writer.flush();
				} catch (final IllegalArgumentException e) {
					throw new IOException(
							"Invalid URL: "
									+ this.url
									+ " - refine ClientProvider.setUrl(\"http://url:8080/UniBox\");");
				} catch (final ConnectException e) {
					throw new IOException("Server offline?");
				}

				for (final Entry<String, List<String>> header : this.connection
						.getHeaderFields().entrySet()) {
					ThreadTaskImpl.log.debug("ResponseHeader: "
							+ header.getKey() + "=" + header.getValue());
				}

				final BufferedReader areader = new BufferedReader(
						new InputStreamReader(this.connection.getInputStream(),
								"UTF-8"));

				for (String line; (line = areader.readLine()) != null;) {
					if (line.equals("success")) {
						ThreadTaskImpl.log.info(RunnableLoginAgent.class
								.getSimpleName()
								+ ": connection etablished. Grab cookie..");
						final List<String> cookies = this.connection
								.getHeaderFields().get("Set-Cookie");
						ThreadTaskImpl.log.info(RunnableLoginAgent.class
								.getSimpleName()
								+ ": Save cookie to ClientState..");
						ClientProvider.setCookie(cookies.get(0));
					} else {
						ThreadTaskImpl.log
								.warn(RunnableLoginAgent.class.getSimpleName()
										+ ": No valid response. maybe wrong credentials?");
						throw new IOException("Bad response: " + line);
					}
				}

				this.writer.close();

			} catch (final IOException e) {
				ThreadTaskImpl.log.error(RunnableLoginAgent.class
						.getSimpleName()
						+ ": Fatal error due HTTP transaction.");
				if (e instanceof ConnectException) {
					ThreadTaskImpl.log.warn(RunnableLoginAgent.class
							.getSimpleName()
							+ ": maybe server offline? "
							+ this.url);
				}
				e.printStackTrace();
			}
		} catch (final Exception e) {
			ThreadTaskImpl.log.warn(RunnableLoginAgent.class.getSimpleName()
					+ ": Authentication failed!");
			e.printStackTrace();
		} finally {
			ThreadTaskImpl.log.debug(RunnableLoginAgent.class.getSimpleName()
					+ ": done.");
		}
	}
}
