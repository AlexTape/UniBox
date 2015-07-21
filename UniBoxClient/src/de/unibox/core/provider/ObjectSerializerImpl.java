package de.unibox.core.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.unibox.core.external.Base64Coder;

/**
 * The Class ObjectSerializerImpl.
 */
public class ObjectSerializerImpl {

	/**
	 * Object to string.
	 *
	 * @param object
	 *            the object
	 * @return the string
	 */
	public static synchronized String objectToString(final Serializable object) {
		String encoded = null;

		try {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			encoded = new String(Base64Coder.encode(byteArrayOutputStream
					.toByteArray()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return encoded;
	}

	/**
	 * String to object.
	 *
	 * @param <E>
	 *            the element type
	 * @param string
	 *            the string
	 * @param clazz
	 *            the clazz
	 * @return the e
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <E extends Serializable> E stringToObject(
			final String string, final Class<E> clazz) {
		final byte[] bytes = Base64Coder.decode(string);
		E object = null;
		try {
			final ObjectInputStream objectInputStream = new ObjectInputStream(
					new ByteArrayInputStream(bytes));
			object = (E) objectInputStream.readObject();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final ClassCastException e) {
			e.printStackTrace();
		}
		return object;
	}

}
