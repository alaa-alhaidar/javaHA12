package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class CrazyhouseGame extends Game implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	// TODO: insert additional game data here

	/************************
	 * constructors
	 ***********************/

	public CrazyhouseGame() {
		super();

		// TODO: initialize internal model if necessary
	}

	public String getType() {
		return "crazyhouse";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = whitePlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (whiteGaveUp())
				gameInfo = "white gave up";
			else if (didWhiteDraw() && !didBlackDraw())
				gameInfo = "white called draw";
			else if (!didWhiteDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "white won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isWhiteNext() ? "w" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) {
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/*
	 * ****************************************** Helpful stuff
	 */

	/**
	 *
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at
		// some artificial state.
		// It can be assumed that the state supplied is a regular board that can be
		// reached during a game.
		// TODO: implement

	}

	@Override
	public String getBoard() {
		// TODO: implement

		// replace with real implementation
		return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/";
	}

	// Startzustand "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/"

	// "b3-c4" = [Urspung]-[Ziel]
	// "r-c4" r it means the Figure comes from the reserve, small letter means black
	// Figure
	// first String part is the Figure and position, c4 is the target to go in
	// column c, row 4

	/*
	 * cArr index 0= Ursprung Figure, cArr index 1= Ursprung Figure Position, cArr
	 * index 2= Ziel x Spalte, cArr index 3= Ziel y Reihe,
	 */
	@Override
	public boolean tryMove(String moveString, Player player) {
		char[] cArr = splitStr(moveString);
		if (player.getGame().isPlayersTurn(player)) {

			return true;
		}

		return false;
	}

	// "b3-c4" = [Urspung]-[Ziel]
	// "r-c4" 
	public char[] splitStr(String s) {
		char[] urspungZiel = null;
		if (s == null) {
			return null;
		}

		String tempArr[] = s.split("-");

		if (tempArr.length == 4) {
			urspungZiel = new char[4];

			urspungZiel[0] = tempArr[0].charAt(0);
			urspungZiel[1] = tempArr[0].charAt(1);
			urspungZiel[2] = tempArr[1].charAt(0);
			urspungZiel[3] = tempArr[1].charAt(1);

			System.out.println(urspungZiel[0] + ", " + urspungZiel[1]);
			System.out.println(urspungZiel[2] + ", " + urspungZiel[3]);
		} else {
//comment
			urspungZiel = new char[3];

			urspungZiel[0] = tempArr[0].charAt(0);
			urspungZiel[1] = tempArr[1].charAt(0);
			urspungZiel[2] = tempArr[1].charAt(1);
			
			System.out.println(urspungZiel[0]);
			System.out.println(urspungZiel[1] + ", " + urspungZiel[2]);
		}
		return urspungZiel;
	}

	// unit-test
	public static void main(String[] args) {

		CrazyhouseGame c = new CrazyhouseGame();
		c.splitStr("b-c4");

	}

}
