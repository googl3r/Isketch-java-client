package is.controller.listeners;

import is.controller.Controller;
import is.controller.net.ERequest;
import is.controller.net.Request;
import is.vue.ViewFacade;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;


public class ChatListener implements ActionListener {

	private Controller controller;
	private ViewFacade vue;

	public ChatListener(Controller controller, ViewFacade vue) {
		this.controller = controller;
		this.vue = vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String msgToSend = vue.getGame().getChat().getSaisie().getText().trim();
		if (!msgToSend.isEmpty()) {
			this.controller.makeRequest(new Request(ERequest.TALK, Arrays
					.asList(msgToSend)));
			vue.getGame().getChat().getSaisie().setText("");
		}

	}

}
