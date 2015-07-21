package de.unibox.client.api;

import javafx.event.EventHandler;
import de.unibox.client.events.CommunicationEvent;
//import org.apache.log4j.Logger;
import de.unibox.core.provider.Logger;

/**
 * The Class IncomingMessageHandler.
 */
public abstract class IncomingMessageHandler implements
		EventHandler<CommunicationEvent> {

	/** The log. */
	protected static Logger log = Logger.getLogger("UniBoxLogger");

	/** The reflection. */
	private final boolean reflection = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(final CommunicationEvent event) {

		final String user = event.getMessageObject().getName();
		final String msg = event.getMessageObject().getMessage();

		IncomingMessageHandler.log
				.debug("IncomingMessageHandler: Recieved Message [" + user
						+ ": " + msg + "]");

		if (this.reflection) {
			if (!user.equals(ClientProvider.getUsername())) {
				this.handle(user, msg);
			} else {
				IncomingMessageHandler.log.debug(this.getClass()
						.getSimpleName()
						+ ": REFLECTION is activated, prevent this message: "
						+ user + ": " + msg);

			}
		} else {
			this.handle(user, msg);
		}
	}

	/**
	 * Handle.
	 *
	 * @param user
	 *            the user
	 * @param msg
	 *            the msg
	 */
	public abstract void handle(String user, String msg);

}
