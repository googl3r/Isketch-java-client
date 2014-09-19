package is.model.logic;

import is.common.UpdateArguments;
import is.model.ModelFacade;

public class Guess {
	private ModelFacade observable;
	private String message, guesser;

	public Guess(ModelFacade modelFacade) {
		this.observable = modelFacade;
		this.message = "";
		this.guesser = "";
	}

	public void ajoutMessage(String guesser, String message) {
		this.message = message;
		this.guesser = guesser;
		observable.notifyView(UpdateArguments.GUESS_UPDATE);
	}

	public String getMessage() {
		return this.message;
	}

	public String getGuesser() {
		return this.guesser;
	}

}
