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
                "INSERT INTO `unibox`.`Player` (`AdminRights`, `Name`, `Password`) VALUES (?, ?, ?);");
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

    public final Integer getAdminRights() {
        return this.adminRights;
    }

    public final String getName() {
        return this.name;
    }

    public final String getPassword() {
        return this.password;
    }

    public final void setAdminRights(final Integer adminRights) {
        this.adminRights = adminRights;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final void setPassword(final String password) {
        this.password = password;
    }

}
