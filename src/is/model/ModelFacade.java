package is.model;

import is.common.UpdateArguments;
import is.model.logic.Chat;
import is.model.logic.Draw;
import is.model.logic.Guess;
import is.model.logic.Players;

import java.util.List;
import java.util.Observable;


public class ModelFacade extends Observable {

	private Draw draw;
	private Players players;
	private Chat chat;
	private Guess guess;
	private int round;

	public ModelFacade() {
		this.chat = new Chat(this);
		this.draw = new Draw(this);
		this.players = new Players();
		this.guess = new Guess(this);
		this.round = 1;
	}

	public void notifyView(UpdateArguments e) {
		setChanged();
		notifyObservers(e);
	}

	// Getters : State Query de la vue

	public Draw getDraw() {
		return this.draw;
	}

	public Players getPlayers() {
		return this.players;
	}

	public Chat getChat() {
		return this.chat;
	}

	public Guess getGuess() {
		return this.guess;
	}

	public void gotWelcomed(String login) {
		this.players.setMyPseudo(login);
		notifyView(UpdateArguments.CONN_SUCCESS);
	}

	public void gotWelcomedAsSpectator() {
		gotWelcomed("spectateur");
		draw.setSpectatorMode();
	}

	
	public void updateScore(List<String> arguments){
		this.getPlayers().updateScore(arguments);
		notifyView(UpdateArguments.SCORE_UPDATE);
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}


}
