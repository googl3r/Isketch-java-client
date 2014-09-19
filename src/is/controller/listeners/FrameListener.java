package is.controller.listeners;

import is.controller.Controller;
import is.controller.net.ERequest;
import is.controller.net.Request;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JOptionPane;


public class FrameListener extends WindowAdapter {

	private Controller control;

	public FrameListener(Controller control) {
		this.control = control;
	}

	@Override
	public void windowClosing(WindowEvent e) {

		if (JOptionPane.showConfirmDialog(null,
				"Voulez-vous vraiment quitter?", "Quitter la partie",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
			try {
				control.makeRequest(new Request(ERequest.EXIT, Arrays.asList(control.getPseudo())));
				control.abortConnection();
			} catch (Exception exc) {
			} finally {
				System.exit(0);
			}
		}

	}
}
