package de.unibox.http.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unibox.config.InternalConfig;
import de.unibox.core.network.object.CommunicatorMessage;
import de.unibox.core.network.object.CommunicatorMessage.MessageType;
import de.unibox.http.servlet.comet.Communicator;
import de.unibox.http.servlet.type.ProtectedHttpServlet;
import de.unibox.model.game.Game;
import de.unibox.model.game.GamePool;
import de.unibox.model.user.AbstractUser;

/**
 * The Class DashboardHandler.
 */
@WebServlet("/Game")
public class GameHandler extends ProtectedHttpServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3278754155030900080L;

    /**
     * Instantiates a new dashboard handler.
     */
    public GameHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {

        final PrintWriter out = response.getWriter();

        String action = null;
        int gameId = 0;
        Game game = null;

        try {
            action = request.getParameter("action");

            if (!action.equals("whichGame")) {
                gameId = Integer.parseInt(request.getParameter("gameId"));
                game = GamePool.getInstance().getGame(gameId);

                if (null == game) {
                    throw new Exception("Could not find game with id: "
                            + gameId);
                }
            }

        } catch (final Exception e) {

            this.log.debug(this.getClass().getSimpleName()
                    + ": could not retrieve relevant game");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("invalid request\n");
            e.printStackTrace();

        } finally {

            if (InternalConfig.LOG_GAMEPOOL) {
                if (game == null) {
                    this.log.debug(this.getClass().getSimpleName()
                            + ": switching " + super.thisUser.getName()
                            + " with " + action);
                } else {
                    this.log.debug(this.getClass().getSimpleName()
                            + ": switching " + super.thisUser.getName()
                            + " with " + action + " for " + game);
                }
            }

            String errorMessage = "unknown_error";
            Integer gameid = null;

            boolean done = false;
            switch (action) {
            case "joinGame":
                GamePool.getInstance();
                final Game prevGame = GamePool.getInstance().getGameByPlayer(
                        super.thisUser);
                if (prevGame == null) {
                    done = game.addPlayer(super.thisUser);
                    this.sendUpdateBroadcast(super.thisUser);
                } else if (prevGame != game) {
                    prevGame.removePlayer(super.thisUser);
                    done = game.addPlayer(super.thisUser);
                    this.sendUpdateBroadcast(super.thisUser);
                } else {
                    errorMessage = "skipped:already_joined";
                }
                break;
            case "leaveGame":
                done = game.removePlayer(super.thisUser);
                this.sendUpdateBroadcast(super.thisUser);
                break;
            case "whichGame":
                game = GamePool.getInstance().getGameByPlayer(super.thisUser);
                if (game != null) {
                    gameid = game.getGameId();
                } else {
                    gameid = null;
                }
                done = true;
                break;
            default:
                break;
            }

            if (done) {
                response.setStatus(HttpServletResponse.SC_OK);
                if (gameid == null) {
                    out.print("success");
                } else {
                    out.print("gameid:" + gameid);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(errorMessage);
            }

            if (InternalConfig.LOG_GAMEPOOL) {
                if (game != null) {
                    this.log.debug(this.getClass().getSimpleName() + ": "
                            + game);
                }
            }
        }

        out.flush();
        out.close();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        this.doGet(request, response);
    }

    /**
     * Send update broadcast.
     *
     * @param user
     *            the user
     */
    private void sendUpdateBroadcast(final AbstractUser user) {
        // send update game table broadcast
        Communicator.getMessagequeue().add(
                new CommunicatorMessage(MessageType.JS_Command, user.getName(),
                        "window.parent.app.updateGameTable();"));
    }

}
