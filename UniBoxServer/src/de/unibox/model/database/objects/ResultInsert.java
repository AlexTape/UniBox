package de.unibox.model.database.objects;

import java.sql.SQLException;

import de.unibox.model.database.DatabaseAction;
import de.unibox.model.database.DatabaseQuery;

/**
 * The Class ResultInsert.
 */
public class ResultInsert extends DatabaseAction<Integer> {

	/** The game id. */
	private Integer gameID = null;

	/** The player id. */
	private Integer playerID = null;

	/** The scoring. */
	private Integer scoring = null;

	/**
	 * Instantiates a new result insert.
	 *
	 * @param thisGameID
	 *            the this game id
	 * @param thisPlayerID
	 *            the this player id
	 * @param thisScoring
	 *            the this scoring
	 */
	public ResultInsert(final Integer thisGameID, final Integer thisPlayerID,
			final Integer thisScoring) {
		super(
				"INSERT INTO result (GameID, PlayerID, Scoring) VALUES (?, ?, ?);");
		this.gameID = thisGameID;
		this.playerID = thisPlayerID;
		this.scoring = thisScoring;
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
		super.getStatement().setInt(1, this.gameID);
		super.getStatement().setInt(2, this.playerID);
		super.getStatement().setInt(3, this.scoring);
		return super.executeUpdate();
	}

	/**
	 * Gets the game id.
	 *
	 * @return the game id
	 */
	public final Integer getGameID() {
		return this.gameID;
	}

	/**
	 * Gets the player id.
	 *
	 * @return the player id
	 */
	public final Integer getPlayerID() {
		return this.playerID;
	}

	/**
	 * Gets the scoring.
	 *
	 * @return the scoring
	 */
	public final Integer getScoring() {
		return this.scoring;
	}

	/**
	 * Sets the game id.
	 *
	 * @param gameID
	 *            the new game id
	 */
	public final void setGameID(final Integer gameID) {
		this.gameID = gameID;
	}

	/**
	 * Sets the player id.
	 *
	 * @param playerID
	 *            the new player id
	 */
	public final void setPlayerID(final Integer playerID) {
		this.playerID = playerID;
	}

	/**
	 * Sets the scoring.
	 *
	 * @param scoring
	 *            the new scoring
	 */
	public final void setScoring(final Integer scoring) {
		this.scoring = scoring;
	}

}
