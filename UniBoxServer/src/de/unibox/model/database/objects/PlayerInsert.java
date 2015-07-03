package de.unibox.model.database.objects;

import java.sql.SQLException;

import de.unibox.model.database.DatabaseAction;
import de.unibox.model.database.DatabaseQuery;

/**
 * The Class PlayerInsert.
 */
public class PlayerInsert extends DatabaseAction<Integer> {

	/** The admin rights. */
	private Integer adminRights = null;

	/** The name. */
	private String name = null;

	/** The password. */
	private String password = null;

	/**
	 * Instantiates a new player insert.
	 *
	 * @param thisAdminRights
	 *            the this admin rights
	 * @param thisName
	 *            the this name
	 * @param thisPassword
	 *            the this password
	 */
	public PlayerInsert(final Integer thisAdminRights, final String thisName,
			final String thisPassword) {
		super(
				"INSERT INTO player (AdminRights, Name, Password) VALUES (?, ?, ?);");
		this.adminRights = thisAdminRights;
		this.name = thisName;
		this.password = thisPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unibox.model.database.DatabaseAction#attach(de.unibox.model.database
	 * .DatabaseQuery)
	 */
	@Override
	public void attach(final DatabaseQuery transaction) throws SQLException {
		super.attachUpdate(transaction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unibox.model.database.DatabaseAction#execute()
	 */
	@Override
	public Integer execute() throws SQLException {
		super.getStatement().setInt(1, this.adminRights);
		super.getStatement().setString(2, this.name);
		super.getStatement().setString(3, this.password);
		return super.executeUpdate();
	}

	/**
	 * Gets the admin rights.
	 *
	 * @return the admin rights
	 */
	public final Integer getAdminRights() {
		return this.adminRights;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public final String getPassword() {
		return this.password;
	}

	/**
	 * Sets the admin rights.
	 *
	 * @param adminRights
	 *            the new admin rights
	 */
	public final void setAdminRights(final Integer adminRights) {
		this.adminRights = adminRights;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            the new password
	 */
	public final void setPassword(final String password) {
		this.password = password;
	}

}
