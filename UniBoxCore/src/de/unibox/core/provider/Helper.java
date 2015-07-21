package de.unibox.core.provider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.unibox.core.external.Base64Coder;

/**
 * The Class Helper.
 */
public final class Helper {

	/**
	 * Decode base64.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String decodeBase64(final String str) {
		return Base64Coder.decodeString(str);
	}

	/**
	 * Encode base64.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String encodeBase64(final String string) {
		return Base64Coder.encodeString(string);
	}

	/**
	 * Gets the after comma.
	 *
	 * @param digit
	 *            the digit
	 * @return the after comma
	 */
	public static int getAfterComma(final double digit) {
		return (int) (digit * 100) % 100;
	}

	/**
	 * Gets the before comma.
	 *
	 * @param d
	 *            the d
	 * @return the before comma
	 */
	public static int getBeforeComma(final double d) {
		return (int) Math.abs(d);
	}

	/**
	 * Checks if is integer.
	 *
	 * @param str
	 *            the str
	 * @return true, if is integer
	 */
	public static boolean isInteger(final String str) {
		if (str == null) {
			return false;
		}
		final int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			final char c = str.charAt(i);
			if ((c <= '/') || (c >= ':')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Long to date.
	 *
	 * @param millis
	 *            the millis
	 * @return the string
	 */
	public static String longToDate(final Long millis) {
		final Date date = new Date(millis);
		final DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		return formatter.format(date);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		System.out.println("UniBoxCore [release]");
	}

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

	/**
	 * Sha.
	 *
	 * @param content
	 *            the content
	 * @return the string
	 */
	public static String sha(final byte[] content) {
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA");
			final byte[] digest = md.digest(content);
			final BigInteger bi = new BigInteger(digest);
			return bi.toString(16);
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * Fit length.
	 *
	 * @param literal
	 *            the literal
	 * @return the string
	 */
	public String fitLength(String literal) {
		if (literal.length() > 22) {
			literal = literal.substring(0, 22) + "..";
		}
		return literal;
	}

}
