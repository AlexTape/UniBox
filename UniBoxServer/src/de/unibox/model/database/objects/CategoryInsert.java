package de.unibox.model.database.objects;

import java.sql.SQLException;

import de.unibox.model.database.DatabaseAction;
import de.unibox.model.database.DatabaseQuery;

/**
 * The Class CategoryInsert.
 */
public class CategoryInsert extends DatabaseAction<Integer> {

	/** The game title. */
	private String gameTitle = null;

	/** The number of players. */
	private Integer numberOfPlayers = null;

	/**
	 * Instantiates a new category insert.
	 *
	 * @param thisGameTitle
	 *            the this gameTitle
	 * @param thisNumberOfPlayers
	 *            the this number of players
	 */
	public CategoryInsert(final String thisGameTitle,
			final int thisNumberOfPlayers) {
		super(
				"INSERT INTO category (GameTitle, NumberOfPlayers) VALUES (?, ?);");
		this.gameTitle = thisGameTitle;
		this.numberOfPlayers = thisNumberOfPlayers;
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
		super.getStatement().setString(1, this.gameTitle);
		super.getStatement().setInt(2, this.numberOfPlayers);
		return super.executeUpdate();
	}

	/**
	 * Gets the game title.
	 *
	 * @return the game title
	 */
	public final String getGameTitle() {
		return this.gameTitle;
	}

	/**
	 * Gets the number of players.
	 *
	 * @return the number of players
	 */
	public final Integer getNumberOfPlayers() {
		return this.numberOfPlayers;
	}

	/**
	 * Sets the game title.
	 *
	 * @param gameTitle
	 *            the new game title
	 */
	public final void setGameTitle(final String gameTitle) {
		this.gameTitle = gameTitle;
	}

	/**
	 * Sets the number of players.
	 *
	 * @param numberOfPlayers
	 *            the new number of players
	 */
	public final void setNumberOfPlayers(final Integer numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

}