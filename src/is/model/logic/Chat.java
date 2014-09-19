package is.model.logic;

import is.common.UpdateArguments;
import is.model.ModelFacade;

public class Chat {
	private ModelFacade observable;
	private String message;

	public Chat(ModelFacade modelFacade) {
		this.observable = modelFacade;
		this.message = "";
	}

	public void ajoutMessage(String nomJoueur, String message) {
		if (nomJoueur.equals("(broadcast)")){
			this.message = "Serveur : " + message + "\n";
		} else {			
			this.message = nomJoueur + " : " + message + "\n";
		}
		observable.notifyView(UpdateArguments.CHAT_UPDATE);
	}

	public String getMessage() {
		return this.message;
	}
}
