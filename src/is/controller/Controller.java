package is.controller;

import is.controller.listeners.ChatListener;
import is.controller.listeners.ConnectListener;
import is.controller.listeners.FrameListener;
import is.controller.listeners.DrawListener;
import is.controller.net.ClientSocket;
import is.controller.net.Request;
import is.model.ModelFacade;
import is.vue.ViewFacade;

import java.io.IOException;


public class Controller {

	private final ModelFacade model;
	private final ViewFacade view;
	private ClientSocket socket;
	private String pseudo;

	public Controller(ModelFacade mod, ViewFacade view) {
		this.model = mod;
		this.view = view;
		this.setPseudo("");

		// Attente si la vue n'est pas encore pr�te
		synchronized (view) {
			try {
				if (!view.isReady())
					view.wait();
			} catch (InterruptedException e) {
			}
		}
		addListeners();
	}

	private void addListeners() {
		// Connexion
		// Boutons de la fenetre de connexion
		ConnectListener cl = new ConnectListener(view, this);

		view.getConnexionPane().getServerComponent().addActionListener(cl);
		view.getConnexionPane().getLoginComponent().addActionListener(cl);
		view.getConnexionPane().getCheck_pass().addActionListener(cl);
		view.getConnexionPane().getCheck_spect().addActionListener(cl);
		view.getConnexionPane().getConnect().addActionListener(cl);
		view.getConnexionPane().getRegister().addActionListener(cl);
		view.getConnexionPane().getPort().addActionListener(cl);

		// GamePane
		// Chat
		ChatListener chatl = new ChatListener(this, view);
		view.getGame().getChat().getSendButton().addActionListener(chatl);
		view.getGame().getChat().getSaisie().addActionListener(chatl);

		// GameGrid
		DrawListener dl = new DrawListener(model, view, this);

		view.getGame().getDraw().addMouseListener(dl);
		view.getGame().getDraw().addMouseMotionListener(dl);
		view.getGame().getDraw().getBlue().addActionListener(dl);
		view.getGame().getDraw().getRed().addActionListener(dl);
		view.getGame().getDraw().getGreen().addActionListener(dl);
		view.getGame().getDraw().getOrange().addActionListener(dl);
		view.getGame().getDraw().getBlack().addActionListener(dl);
		view.getGame().getDraw().getYellow().addActionListener(dl);
		view.getGame().getDraw().getViolet().addActionListener(dl);
		view.getGame().getDraw().getRose().addActionListener(dl);
		view.getGame().getDraw().getLightblue().addActionListener(dl);
		view.getGame().getDraw().getDarkgreen().addActionListener(dl);
		view.getGame().getDraw().getGray().addActionListener(dl);
		view.getGame().getDraw().getUpSize().addActionListener(dl);
		view.getGame().getDraw().getDownSize().addActionListener(dl);
		view.getGame().getDraw().getSendGuess().addActionListener(dl);
		view.getGame().getDraw().getSaisie().addActionListener(dl);
		view.getGame().getDraw().getCheckcurve().addActionListener(dl);
		view.getGame().getDraw().getCheat().addActionListener(dl);
		view.getGame().getDraw().getPassround().addActionListener(dl);


		// Frame
		view.getMf().addWindowListener(new FrameListener(this));
	}

	// visib package
	void setSocket(ClientSocket socket) {
		this.socket = socket;
	}

	public void establishConnexion(final String pseudo, String host, int port) {
		new CommandDispatcher(model, this, view, host, port, pseudo).execute();
	}

	/**
	 * mode spectateur
	 */
	public void establishConnexion(String host, int port) {
		new CommandDispatcher(model, this, view, host, port).execute();
	}

	/**
	 * Login / Register
	 */
	public void establishConnexion(String pseudo, String pass, String host, int port,
			boolean isRegister) {
		new CommandDispatcher(model, this, view, host, port, pseudo, pass, isRegister)
				.execute();
	}

	// Appelé uniquement par le connectListener
	public void abortConnection() {
		closeConnection();
	}

	public void makeRequest(Request r) {
		if (socket != null)
			try {
				socket.makeRequest(r);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Dans le cas où la connexion n'est pas initialisée
		}
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

}
