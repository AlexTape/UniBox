package de.unibox.client.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.scene.Node;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

import de.unibox.client.events.CommunicationEvent;
import de.unibox.client.events.DatabaseEvent;
import de.unibox.client.events.RequestType;
import de.unibox.client.thread.ThreadEngine;
import de.unibox.client.thread.worker.RunnableCometListener;
import de.unibox.client.thread.worker.RunnableDatabaseAgent;
import de.unibox.client.thread.worker.RunnableLoginAgent;
import de.unibox.client.thread.worker.RunnableMessageMediator;
import de.unibox.client.thread.worker.RunnableMessageSender;
import de.unibox.core.network.object.CommunicatorMessage;
import de.unibox.core.network.object.MessageType;
import de.unibox.core.provider.Helper;

/**
 * The Class ClientProvider. This Class act as a static Interface for Java
 * Clients to provide intuitive access to the backend API.
 */
public class ClientProvider {

    /** The comet url. */
    private static String cometURL = "/Communicator/Serial";

    /** The cookie. */
    private static String cookie = null;

    /** The database url. */
    private static String databaseURL = "/Database";

    /**
     * The incoming messages. All incoming messages will arrive in that queue
     * before they get picked up by a worker thread.
     */
    private static BlockingQueue<CommunicatorMessage> incomingMessages = new LinkedBlockingQueue<CommunicatorMessage>();

    /** The log. */
    private static Logger log = Logger.getLogger("UniBoxLogger");

    /**
     * The outgoing database events. All outgoing database events will arrive in
     * that queue before they get picked up by a worker thread.
     */
    private static BlockingQueue<DatabaseEvent> outgoingDatabaseEvents = new LinkedBlockingQueue<DatabaseEvent>();

    /**
     * The outgoing messages. All outgoing messages will arrive in that queue
     * before they get picked up by a worker thread.
     */
    private static BlockingQueue<CommunicatorMessage> outgoingMessages = new LinkedBlockingQueue<CommunicatorMessage>();

    /** The password. */
    private static String password = null;

    /** The default port. */
    private static int port = 8080;

    /** The reciever url. */
    private static String recieverURL = "/Communicator/Serial";

    /** The url. */
    private static String url = null;

    /** The username. */
    private static String username = null;

    /**
     * This method binds the INCOMING_MESSAGE event to a given node.
     *
     * @param primaryStage
     *            the primary stage
     * @param messageHandler
     *            the message handler
     */
    public static void bind(final Stage primaryStage,
            final IncomingMessageHandler messageHandler) {
        try {
            final Stage stage = primaryStage;
            final Node node = stage.getScene().getRoot();
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": binding " + primaryStage);
            node.addEventHandler(CommunicationEvent.INCOMING_MESSAGE,
                    messageHandler);
            CommunicationEvent.setTarget(node);
        } catch (final Exception e) {
            ClientProvider.log.warn(ClientProvider.class.getSimpleName()
                    + ": bind() failed..");
            e.printStackTrace();
        }
    }

    /**
     * This method is starting the worker threads to connect to the backend.
     */
    public static void connect() {
        try {

            ThreadEngine.getInstance().run(new RunnableMessageSender());
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": RunnableMessageSender started..");

            ThreadEngine.getInstance().run(new RunnableMessageMediator());
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": RunnableMessageMediator started..");

            ThreadEngine.getInstance().run(new RunnableCometListener());
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": RunnableCometListener started..");

            ThreadEngine.getInstance().run(new RunnableDatabaseAgent());
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": RunnableDatabaseAgent started..");

        } catch (final Exception e) {
            ClientProvider.log.error(ClientProvider.class.getSimpleName()
                    + ": Could not initialize or run ThreadEngine..");
            e.printStackTrace();
        }
    }

    /**
     * Gets the comet url.
     *
     * @return the comet url
     */
    public static synchronized final String getCometURL() {
        return ClientProvider.cometURL;
    }

    /**
     * Gets the cookie.
     *
     * @return the cookie
     */
    public static String getCookie() {
        return ClientProvider.cookie;
    }

    /**
     * Gets the database url.
     *
     * @return the database url
     */
    public static String getDatabaseURL() {
        return ClientProvider.databaseURL;
    }

    /**
     * Gets the incoming messages.
     *
     * @return the incoming messages
     */
    private static final BlockingQueue<CommunicatorMessage> getIncomingMessages() {
        return ClientProvider.incomingMessages;
    }

    /**
     * Gets the next incoming message.
     *
     * @return the next incoming message
     * @throws InterruptedException
     *             the interrupted exception
     */
    public static CommunicatorMessage getNextIncomingMessage()
            throws InterruptedException {
        ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                + ": take() incoming message..");
        return ClientProvider.getIncomingMessages().take();
    }

    /**
     * Gets the next outgoing message.
     *
     * @return the next outgoing message
     * @throws InterruptedException
     *             the interrupted exception
     */
    public static CommunicatorMessage getNextOutgoingMessage()
            throws InterruptedException {
        ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                + ": take() outgoing message..");
        return ClientProvider.getOutgoingMessages().take();
    }

    /**
     * Gets the outgoing database events.
     *
     * @return the outgoing database events
     */
    public static BlockingQueue<DatabaseEvent> getOutgoingDatabaseEvents() {
        return ClientProvider.outgoingDatabaseEvents;
    }

    /**
     * Gets the outgoing messages.
     *
     * @return the outgoing messages
     */
    private static final BlockingQueue<CommunicatorMessage> getOutgoingMessages() {
        return ClientProvider.outgoingMessages;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public static final String getPassword() {
        return ClientProvider.password;
    }

    /**
     * Gets the reciever url.
     *
     * @return the reciever url
     */
    public static final String getRecieverURL() {
        return ClientProvider.recieverURL;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public static final String getUrl() {
        return ClientProvider.url;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public static final String getUsername() {
        return ClientProvider.username;
    }

    /**
     * This method is starting the login agent to retrieve a valid cookie for
     * further authorized requests.
     */
    public static void login() {
        try {
            ThreadEngine.getInstance().setMonitor(true);
            ThreadEngine.getInstance().run(new RunnableLoginAgent());
            ThreadEngine.getInstance().blockTillDone();
        } catch (final Exception e) {
            ClientProvider.log.error(ClientProvider.class.getSimpleName()
                    + ": Could not initialize or run ThreadEngine..");
            e.printStackTrace();
        }
    }

    /**
     * Recieve message.
     *
     * @param message
     *            the message
     */
    public static void recieveMessage(final CommunicatorMessage message) {
        ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                + ": received message: " + message);
        ClientProvider.getIncomingMessages().add(message);
    }

    /**
     * Report draw result.
     */
    public static void reportDrawResult() {
        ClientProvider.outgoingDatabaseEvents.add(new DatabaseEvent(
                RequestType.METHOD_POST, "action=createResult&status=draw"));
    }

    /**
     * Report lose result.
     */
    public static void reportLoseResult() {
        ClientProvider.outgoingDatabaseEvents.add(new DatabaseEvent(
                RequestType.METHOD_POST, "action=createResult&status=lose"));
    }

    /**
     * Report win result.
     */
    public static void reportWinResult() {
        ClientProvider.outgoingDatabaseEvents.add(new DatabaseEvent(
                RequestType.METHOD_POST, "action=createResult&status=win"));
    }

    /**
     * Send chat message.
     *
     * @param message
     *            the message
     */
    public static void sendChatMessage(final String message) {
        final CommunicatorMessage cMessage = new CommunicatorMessage(
                MessageType.CHAT, ClientProvider.getUsername(),
                Helper.encodeBase64(message));
        ClientProvider.sendCustomMessage(cMessage);
    }

    /**
     * Send custom message.
     *
     * @param message
     *            the message
     */
    public static void sendCustomMessage(final CommunicatorMessage message) {
        ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                + ": sending message: " + message);
        ClientProvider.getOutgoingMessages().add(message);
    }

    /**
     * Send error message.
     *
     * @param message
     *            the message
     */
    public static void sendErrorMessage(final String message) {
        final CommunicatorMessage cMessage = new CommunicatorMessage(
                MessageType.ERROR, ClientProvider.getUsername(),
                Helper.encodeBase64(message));
        ClientProvider.sendCustomMessage(cMessage);
    }

    /**
     * Send game message.
     *
     * @param message
     *            the message
     */
    public static void sendGameMessage(final String message) {
        final CommunicatorMessage cMessage = new CommunicatorMessage(
                MessageType.GAME, ClientProvider.getUsername(),
                Helper.encodeBase64(message));
        ClientProvider.sendCustomMessage(cMessage);
    }

    /**
     * Send system message.
     *
     * @param message
     *            the message
     */
    public static void sendSystemMessage(final String message) {
        final CommunicatorMessage cMessage = new CommunicatorMessage(
                MessageType.SYSTEM, ClientProvider.getUsername(),
                Helper.encodeBase64(message));
        ClientProvider.sendCustomMessage(cMessage);
    }

    /**
     * Sets the cookie.
     *
     * @param cookie
     *            the new cookie
     */
    public static void setCookie(final String cookie) {
        ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                + ": received cookie: " + cookie);
        ClientProvider.cookie = cookie;
    }

    /**
     * Sets the full url.
     *
     * @param url
     *            the new full url
     */
    public static final void setFullUrl(final String url) {
        ClientProvider.url = url;
    }

    /**
     * Sets the ip.
     *
     * @param ip
     *            the new ip
     */
    public static final void setIp(final String ip) {
        ClientProvider.url = "http://" + ip + ":" + ClientProvider.port
                + "/UniBox";
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public static final void setPassword(final String password) {
        try {
            ClientProvider.password = URLEncoder.encode(
                    Helper.encodeBase64(password), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            ClientProvider.log.debug(ClientProvider.class.getSimpleName()
                    + ": Could not able to password.");
            e.printStackTrace();
        }
    }

    /**
     * This method will ask for server ip, username and password.
     */
    public static final void setupScanner() {
        final Scanner s = new Scanner(System.in);
        System.out.print("ServerIP:");
        ClientProvider.setFullUrl("http://" + s.next() + ":"
                + ClientProvider.port + "/UniBox");
        System.out.print("Username:");
        ClientProvider.setUsername(s.next());
        System.out.print("Password:");
        ClientProvider.setPassword(s.next());
        s.close();
        System.out.println("ClientProvider booting..");
    }

    /**
     * Sets the username.
     *
     * @param username
     *            the new username
     */
    public static final void setUsername(final String username) {
        ClientProvider.username = username;
    }

}
