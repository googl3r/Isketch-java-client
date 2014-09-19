package is.controller.listeners;

import is.controller.Controller;
import is.vue.ViewFacade;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;


public class ConnectListener implements ActionListener {

	private ViewFacade vue;
	private Controller control;

	public ConnectListener(ViewFacade vue, Controller controller) {
		this.vue = vue;
		this.control = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "PASSWORD":
			JCheckBox pcb = vue.getConnexionPane().getCheck_pass();
			vue.getConnexionPane().getPass().setEnabled(pcb.isSelected());
			vue.getConnexionPane().getRegister().setEnabled(pcb.isSelected());
			break;
		case "SPECTATOR":
			JCheckBox scb = vue.getConnexionPane().getCheck_spect();
			pcb = vue.getConnexionPane().getCheck_pass();

			vue.getConnexionPane().getLoginComponent()
					.setEnabled(!scb.isSelected());
			pcb.setEnabled(!scb.isSelected());
			vue.getConnexionPane().getPass()
					.setEnabled(!scb.isSelected() && pcb.isSelected());
			vue.getConnexionPane().getRegister()
					.setEnabled(!scb.isSelected() && pcb.isSelected());
			break;
		default:
			String host = vue.getConnexionPane().getHost().trim();
			if (!verif(host, "Choisissez un serveur"))
				break;

			scb = vue.getConnexionPane().getCheck_spect();
			String pseudo = vue.getConnexionPane().getLogin().trim();
			int port = Integer.parseInt(vue.getConnexionPane().getPort().getText());
			control.setPseudo(pseudo);

			if (!scb.isSelected()
					&& !verif(pseudo, "Choisissez un pseudo non-vide"))
				break;

			pcb = vue.getConnexionPane().getCheck_pass();
			String pass = vue.getConnexionPane().getPass().getText().trim();

			if (!scb.isSelected() && pcb.isSelected()
					&& !verif(pass, "Choisissez un mot de passe"))
				break;

			// register
			if (e.getActionCommand() == "REGISTER")
				control.establishConnexion(pseudo, pass, host, port, true);
			else
			// login
			if (pcb.isSelected())
				control.establishConnexion(pseudo, pass, host, port, false);
			else
			// spectator
			if (scb.isSelected())
				control.establishConnexion(host, port);
			// normal
			else
				control.establishConnexion(pseudo, host, port);
			break;
		}
	}

	private boolean verif(String text, String message) {
		if (text.isEmpty()) {
			vue.getConnexionPane().showInfo(message);
			return false;
		}
		return true;
	}
}
