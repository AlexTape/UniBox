package de.unibox.model.database;

/*
 * Added additional null checks when closing the ResultSet and Statements.
 *
 * Thanks to pihug12 and Grzegorz Oledzki at stackoverflow.com
 * http://stackoverflow.com/questions/5332149/jdbc-scriptrunner-java-lang-nullpointerexception?tab=active#tab-top
 */
/*
 * Modified: Use logWriter in print(Object), JavaDoc comments, correct Typo.
 */
/*
 * Modified by Pantelis Sopasakis <chvng@mail.ntua.gr> to take care of DELIMITER statements. This way you
 * can execute scripts that contain some TRIGGER creation code. New version using REGEXPs! Latest
 * modification: Cater for a NullPointerException while parsing. Date: Feb 16, 2011, 11:48 EET
 */
/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class from the iBATIS Apache
 * project. Only removed dependency on Resource class and a constructor
 */
/*
 * Copyright 2004 Clinton Begin Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Tool to run database scripts. This version of the script can be found at
 * https://gist.github.com/gists/831762/
 * 
 * EDITED: Replaced logWriter* with UniBox Logger.
 */
public class CustomScriptRunner {

    /** The Constant DEFAULT_DELIMITER. */
    private static final String DEFAULT_DELIMITER = ";";
    
    /** The Constant DELIMITER_LINE_REGEX. */
    private static final String DELIMITER_LINE_REGEX = "(?i)DELIMITER.+";
    
    /** The Constant DELIMITER_LINE_SPLIT_REGEX. */
    private static final String DELIMITER_LINE_SPLIT_REGEX = "(?i)DELIMITER";

    /** The connection. */
    private final Connection connection;
    
    /** The stop on error. */
    private final boolean stopOnError;
    
    /** The auto commit. */
    private final boolean autoCommit;
    
    /** The delimiter. */
    private String delimiter = DEFAULT_DELIMITER;
    
    /** The full line delimiter. */
    private boolean fullLineDelimiter = false;
    
    /** The log. */
    protected Logger log = Logger.getLogger("UniBoxLogger");

    /**
     * Default constructor.
     *
     * @param connection
     *            the connection
     * @param autoCommit
     *            the auto commit
     * @param stopOnError
     *            the stop on error
     */
    public CustomScriptRunner(Connection connection, boolean autoCommit,
            boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
    }

    /**
     * Sets the delimiter.
     *
     * @param delimiter
     *            the delimiter
     * @param fullLineDelimiter
     *            the full line delimiter
     */
    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter).
     *
     * @param reader
     *            - the source of the script
     * @throws IOException
     *             if there is an error reading from the Reader
     * @throws SQLException
     *             if any SQL errors occur
     */
    public void runScript(Reader reader) throws IOException, SQLException {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != autoCommit) {
                    connection.setAutoCommit(autoCommit);
                }
                runScript(connection, reader);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (IOException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in.
     *
     * @param conn
     *            - the connection to use for the script
     * @param reader
     *            - the source of the script
     * @throws IOException
     *             if there is an error reading from the Reader
     * @throws SQLException
     *             if any SQL errors occur
     */
    private void runScript(Connection conn, Reader reader) throws IOException,
            SQLException {
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("--")) {
                    this.log.debug(trimmedLine);
                } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("//")) {
                    // Do nothing
                } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {

                    Pattern pattern = Pattern.compile(DELIMITER_LINE_REGEX);
                    Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        setDelimiter(trimmedLine
                                .split(DELIMITER_LINE_SPLIT_REGEX)[1].trim(),
                                fullLineDelimiter);
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }

                    command.append(line.substring(0,
                            line.lastIndexOf(getDelimiter())));
                    command.append(" ");
                    Statement statement = conn.createStatement();

                    this.log.debug(command);

                    boolean hasResults = false;
                    if (stopOnError) {
                        hasResults = statement.execute(command.toString());
                    } else {
                        try {
                            statement.execute(command.toString());
                        } catch (SQLException e) {
                            e.fillInStackTrace();
                            this.log.debug("Error executing: " + command);
                            this.log.debug(e);
                        }
                    }

                    if (autoCommit && !conn.getAutoCommit()) {
                        conn.commit();
                    }

                    ResultSet rs = statement.getResultSet();
                    if (hasResults && rs != null) {
                        ResultSetMetaData md = rs.getMetaData();
                        int cols = md.getColumnCount();
                        for (int i = 0; i < cols; i++) {
                            String name = md.getColumnLabel(i);
                            this.log.debug(name + "\t");
                        }
                        this.log.debug("");
                        while (rs.next()) {
                            for (int i = 1; i <= cols; i++) {
                                String value = rs.getString(i);
                                this.log.debug(value + "\t");
                            }
                            this.log.debug("");
                        }
                    }

                    command = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Ignore to workaround a bug in Jakarta DBCP
                    }
                } else {
                    Pattern pattern = Pattern.compile(DELIMITER_LINE_REGEX);
                    Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        setDelimiter(trimmedLine
                                .split(DELIMITER_LINE_SPLIT_REGEX)[1].trim(),
                                fullLineDelimiter);
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }
                    command.append(line);
                    command.append(" ");
                }
            }
            if (!autoCommit) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
            this.log.debug("Error executing: " + command);
            this.log.debug(e);
            throw e;
        } catch (IOException e) {
            e.fillInStackTrace();
            this.log.debug("Error executing: " + command);
            this.log.debug(e);
            throw e;
        } finally {
            conn.rollback();
        }
    }

    private String getDelimiter() {
        return delimiter;
    }

}