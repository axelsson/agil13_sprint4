package kth.game.othello.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kth.game.othello.Direction;
import kth.game.othello.board.Board;
import kth.game.othello.board.Node;
import kth.game.othello.player.Player;

/**
 * The responsibility of this class is to handle of the scores of the players.
 */
public class BasicScore extends Observable implements Score, Observer {

	private final List<ScoreItem> playerScores = new ArrayList<>();
	private final Board board;

	public BasicScore(List<Player> players, Board board) {
		for (Player player : players) {
			playerScores.add(new ScoreItem(player.getId(), 0));
		}
		this.board = board;
	}

	void incrementPoints(String playerId, int amount) {
		changePoints(playerId, 1*amount);
	}

	void decrementPoints(String playerId, int amount) {
		changePoints(playerId, -1*amount);
	}

	private void changePoints(String playerId, int points) {
		ScoreItem oldScoreItem = getScoreItem(playerId);
		if (playerScores.remove(oldScoreItem)) {
			playerScores.add(new ScoreItem(oldScoreItem.getPlayerId(), oldScoreItem.getScore() + points));
		}
	}

	/**
	 * Parses a board to set the initial score.
	 * 
	 * @param board
	 *            The board to parse nodes from.
	 */
	public void setInitialScore() {
		for (Node node : board.getNodes()) {
			if (node.getOccupantPlayerId() != null) {
				int amount = nodeOnBoundary (node) ? 2 : 1;
				incrementPoints(node.getOccupantPlayerId(), amount);
			}
		}
	}

	private boolean nodeOnBoundary(Node node) {
		for (Direction direction : Direction.values()){
			if (!board.hasNode(
					node.getXCoordinate() + direction.getXDirection(), 
					node.getYCoordinate() + direction.getYDirection())){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ScoreItem> getPlayersScore() {
		return playerScores;
	}

	@Override
	public int getPoints(String playerId) {
		ScoreItem scoreItem = getScoreItem(playerId);
		if (scoreItem != null)
			return scoreItem.getScore();
		else
			return 0;
	}

	private ScoreItem getScoreItem(String playerId) {
		for (ScoreItem scoreItem : playerScores) {
			if (scoreItem.getPlayerId().equals(playerId)) {
				return scoreItem;
			}
		}
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!(o instanceof Node))
			return;
		Node node = (Node) o;
		List<String> playerIds = new ArrayList<>();
		int amount = nodeOnBoundary (node) ? 2 : 1;
		incrementPoints(node.getOccupantPlayerId(), amount);
		playerIds.add(node.getOccupantPlayerId());
		if (arg instanceof String) {
			String previousPlayerId = (String) arg;
			decrementPoints(previousPlayerId, amount);
			playerIds.add(previousPlayerId);
		}
		setChanged();
		notifyObservers(playerIds);
	}
}
