package de.unibox.core.network.object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.unibox.core.external.Base64Coder;

/**
 * The Class CommunicatorMessage provides a blueprint of all transmitted
 * messages.
 */
public class CommunicatorMessage implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4946036413758099605L;

	/**
	 * Md5.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	public static String md5(String input) {

		// is there any pasword that is worth the salt?? ;-)
		final String salt = "$1s#th3r3@nyp455w0rd%th4t&1s^w0r1h-th3~s4lt*??";
		String md5 = null;
		if (null == input) {
			return null;
		} else {
			// salt it
			input = salt + input;
		}

		try {
			// create MessageDigest object for MD5
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			// update input string in message digest
			digest.update(input.getBytes(), 0, input.length());
			// converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);
		} catch (final NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		return md5;
	}

	/** The checksum. */
	private String checksum;

	/** The debug client side. */
	private final boolean DEBUG_CLIENT_SIDE = false;

	/** The message. */
	private String message;

	/** The name. */
	private String name;

	/** The type. */
	private CommunicatorMessageType type;

	/**
	 * Instantiates a new communicator message.
	 *
	 * @param type
	 *            the type
	 * @param identifier
	 *            the identifier
	 * @param message
	 *            the message
	 */
	public CommunicatorMessage(final CommunicatorMessageType type,
			final String identifier, final String message) {
		super();
		this.type = type;
		this.name = identifier;
		this.message = message;
		this.checksum = CommunicatorMessage.md5(this.toString());
	}

	/**
	 * Escape html.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	protected String escapeHtml(final String input) {
		final StringBuffer buffer = new StringBuffer(input.length());

		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			switch (c) {
			case '\b':
				buffer.append("\\b");
				break;
			case '\f':
				buffer.append("\\f");
				break;
			case '\n':
				buffer.append("<br />");
				break;
			case '\r':
				// ignore
				break;
			case '\t':
				buffer.append("\\t");
				break;
			case '\'':
				buffer.append("\\'");
				break;
			case '\"':
				buffer.append("\\\"");
				break;
			case '\\':
				buffer.append("\\\\");
				break;
			case '<':
				buffer.append("&lt;");
				break;
			case '>':
				buffer.append("&gt;");
				break;
			case '&':
				buffer.append("&amp;");
				break;
			default:
				buffer.append(c);
			}
		}

		return buffer.toString();
	}

	/**
	 * From string.
	 *
	 * @param s
	 *            the s
	 * @return the object
	 */
	public Object fromString(final String s) {
		final byte[] data = Base64Coder.decode(s);
		ObjectInputStream ois;
		Object o = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			o = ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * Gets the checksum.
	 *
	 * @return the checksum
	 */
	public String getChecksum() {
		return this.checksum;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.escapeHtml(this.message);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.escapeHtml(this.name);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public CommunicatorMessageType getType() {
		return this.type;
	}

	/**
	 * Sets the checksum.
	 *
	 * @param checksum
	 *            the new checksum
	 */
	public void setChecksum(final String checksum) {
		this.checksum = checksum;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *            the new message
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(final CommunicatorMessageType type) {
		this.type = type;
	}

	/**
	 * To java script.
	 *
	 * @return the string
	 */
	public String toJavaScript() {
		String returnThis = "";
		if (this.type == CommunicatorMessageType.JS_COMMAND) {
			returnThis = "<script type='text/javascript'>" + this.getMessage()
					+ "</script>";
		} else if (this.type == CommunicatorMessageType.PING) {
			returnThis = this.getMessage();
		} else {
			if (this.DEBUG_CLIENT_SIDE) {
				returnThis = "<script type='text/javascript'>window.parent.app.message({name:\""
						+ this.getName()
						+ "\",message:\""
						+ this.getMessage()
						+ "\"});</script>";

			} else {
				returnThis = "<script type='text/javascript'>window.parent.app.message({name:\""
						+ this.getName()
						+ "\",message:\""
						+ this.getMessage()
						+ "\"});window.parent.app.listening=true;var e=document.getElementsByTagName(\"script\");"
						+ "for(i=e.length-1;i>=0;i--){e[i].parentNode.removeChild(e[i]);}"
						+ "</script>";
			}
		}
		return returnThis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CommunicatorMessage [checksum=" + this.checksum + ", message="
				+ this.message + ", name=" + this.name + "]";
	}

}