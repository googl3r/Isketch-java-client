package is.controller;

import is.common.UpdateArguments;
import is.controller.net.ClientSocket;
import is.controller.net.ERequest;
import is.controller.net.Request;
import is.controller.net.Response;
import is.model.ModelFacade;
import is.run.Main;
import is.vue.ViewFacade;

import java.awt.Color;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

class CommandDispatcher extends SwingWorker<Void, Response> {

	private ModelFacade model;
	private Controller controller;
	private ViewFacade view;
	private String pseudo, host, pass;
	private boolean isRegister;
	private boolean isCleanDisconnect;
	private boolean isSpectator;
	private int port;

	public CommandDispatcher(ModelFacade model, Controller controller,
			ViewFacade view, String host, int port, String pseudo) {
		this.model = model;
		this.controller = controller;
		this.host = host;
		this.pseudo = pseudo;
		this.view = view;
		this.port = port;
		this.isCleanDisconnect = false;
	}

	public CommandDispatcher(ModelFacade model, Controller controller,
			ViewFacade view, String host) {
		this.model = model;
		this.controller = controller;
		this.view = view;
		this.host = host;
		this.pass = null;
		this.pseudo = null;
		this.isCleanDisconnect = false;
		this.isSpectator = true;
		this.port = 2013;
	}
	public CommandDispatcher(ModelFacade model, Controller controller,
			ViewFacade view, String host, int port) {
		this.model = model;
		this.controller = controller;
		this.view = view;
		this.host = host;
		this.pass = null;
		this.pseudo = null;
		this.isCleanDisconnect = false;
		this.isSpectator = true;
		this.port = 2013;
	}

	public CommandDispatcher(ModelFacade model, Controller controller,
			ViewFacade view, String host, int port, String pseudo, String pass,
			boolean isRegister) {
		this.model = model;
		this.controller = controller;
		this.view = view;
		this.host = host;
		this.pseudo = pseudo;
		this.pass = pass;
		this.isRegister = isRegister;
		this.isCleanDisconnect = false;
		this.isSpectator = false;
		this.port = 2013;
	}

	@Override
	protected Void doInBackground() {
		ClientSocket socket = null;

		model.notifyView(UpdateArguments.CONN_INIT);

		InetAddress addr = null;
		try {
			addr = Inet4Address.getByName(host);
		} catch (UnknownHostException e) {
			model.notifyView(UpdateArguments.RESOLV_FAILED);
			return null;
		}

		try {
			// Spectator
			if (pass == null && pseudo == null) {
				socket = new ClientSocket(addr, port);
			} else
			// Connect
			if (pass == null) {
				socket = new ClientSocket(addr, port, pseudo);
			}
			// Register / login
			else {
				socket = new ClientSocket(addr, port,  pseudo, pass, this.isRegister);
			}
		} catch (IOException e1) {
			model.notifyView(UpdateArguments.CONN_FAILED);
			return null;
		}

		controller.setSocket(socket);

		return startReadingLoop(socket);
	}

	private Void startReadingLoop(ClientSocket socket) {
		try {
			// On suppose que le spectator ne reçoit pas de connected
			if (isSpectator)
				welcomeProcess();
			if (Main.DEBUG)
				System.out.println("DEBUG : Connexion etablie - En attente");
			Response r;
			while (true) {
				try {
					r = socket.receiveResponse();
					if (Main.DEBUG)
						System.out.println("DEBUG : Response reçue = " + r);
					publish(r);
				} catch (ParseException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			if (isCleanDisconnect) {
				this.isCleanDisconnect = false;
				return null;
			} else {
				if (Main.DEBUG)
					System.err.println("Error : connexion lost");
				JOptionPane.showMessageDialog(null,
						"La connexion a été interrompue", "Fatal error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		return null;
	}

	protected void process(List<Response> chunks) {
		for (Response r : chunks)
			dispatch(r);
	}

	private void dispatch(Response r) {
		switch (r.getCommand()) {
		case ACCESSDENIED:
			model.notifyView(this.isRegister ? UpdateArguments.REGISTER_FAILED
					: UpdateArguments.LOGIN_FAILED);
			this.isCleanDisconnect = true;
			controller.closeConnection();
			break;
		case WELCOME:
			model.getPlayers().setAll();
			if (isSpectator)
				welcomeProcess();
			else
				welcomeProcess(r.getArguments().get(0));
			break;
		case CONNECTED:
			addplayer(r.getArguments());
			//view.getGame().getLog().ajouterMessage("Serveur", "Joueur : "+r.getArguments().get(0)+" viens de se connecter", Color.blue);
			break;
		case NEW_ROUND:
			view.getGame().getDraw().clearPaint();
			choiceType(r.getArguments());
			break;
		case LINE:
			processdrawline(r.getArguments());
			break;
		case GUESSED:
			model.getGuess().ajoutMessage(r.getArguments().get(0), r.getArguments().get(1));
			break;
		case WORD_FOUND:
			model.getPlayers().updateRole(r.getArguments().get(0), "a deviner");
			if (model.getPlayers().getMyPseudo().equalsIgnoreCase(r.getArguments().get(0))) {				
				view.getGame().getLog().ajouterMessage("Serveur",
						"Bravo, vous avez deviné!!",
						Color.BLUE);
				model.notifyView(UpdateArguments.END_ROUND);
			} else {
				view.getGame().getLog().ajouterMessage("Serveur",
						r.getArguments().get(0)+" viens de trouver!",
						Color.BLUE);
			}
			model.notifyView(UpdateArguments.PLAYERS_UPDATE);
			break;
		case WORD_FOUND_TIMEOUT:
			if (model.getDraw().isDrawer()){				
				view.getGame().getDraw().stopTimer();
			}
			view.getGame().getLog().ajouterMessage("Serveur",
					"Un joueur viens de trouver il reste " + r.getArguments().get(0)+ " secondes pour deviner!!",
					Color.RED);
			break;
		case END_ROUND:
			model.notifyView(UpdateArguments.END_ROUND);
			if (r.getArguments().get(0).equals("NO_ONE")){
				view.getGame()
				.getLog()
				.ajouterMessage("Serveur", 
						"Le round viens de se finir, Personne n'a gagner ce round, le mot était "+ r.getArguments().get(1)+"!", Color.magenta);
			} else {				
				view.getGame()
				.getLog()
				.ajouterMessage("Serveur", 
						"Le round viens de se finir, le mot était "+ r.getArguments().get(1)+" , le vainqueur de ce round est "+r.getArguments().get(0)+"!", Color.magenta);
			}
			model.setRound(model.getRound()+1);
			break;
		case SCORE_ROUND:
			model.updateScore(r.getArguments());
			model.getPlayers().updateRoles("en attente");
			view.getGame()
			.getLog().ajouterMessage("Serveur", "Les scores sont maintenant à jour", Color.blue);
			break;
		case EXITED:
			model.getPlayers().updateRole(r.getArguments().get(0), "parti");
			model.notifyView(UpdateArguments.PLAYERS_UPDATE);
			view.getGame().getLog().ajouterMessage("Serveur", r.getArguments().get(0)+" ne joue plus!!", Color.magenta);
			break;
		case COURBE:
			processdrawcourbe(r.getArguments());
			break;
		case LISTEN:
			processHeyListen(r.getArguments());
			break;
		default:
			break;
		}
	}


	private void addplayer(List<String> arguments) {
		model.getPlayers().addPlayer(arguments.get(0));
		model.notifyView(UpdateArguments.NEW_PLAYER);
	}

	private void choiceType(List<String> arguments) {
		if (model.getRound()==1){			
			view.getGame().getLog().ajouterMessage("Serveur", "Vous jouez contre "+model.getPlayers().everyoneButMeToString(), Color.orange);
		}
		model.getPlayers().updateRoles("chercheur");
		model.getPlayers().updateRole(arguments.get(1), "dessinateur");
		model.notifyView(UpdateArguments.PLAYERS_UPDATE);
		if (arguments.get(1).equalsIgnoreCase(model.getPlayers().getMyPseudo())) {
			model.getDraw().setDrawer(true);
			try {				
				model.getDraw().setMot(arguments.get(2));
				model.notifyView(UpdateArguments.NEW_ROUND_DRAWER);
			} catch (Exception e) {
				System.out.println("Probleme je suis le dessinateur et je n'ai "
						+ "pas de mot a dessiner, si c'est un autre la commande "
						+ "PASS ne marchera pas");
				view.getGame().getLog().ajouterMessage("Systeme", "y'a un probleme j'envoie un PASS", Color.red);
				controller.makeRequest(new Request(ERequest.PASS, Collections
						.<String> emptyList()));
				model.notifyView(UpdateArguments.NEW_ROUND_FINDER);
			}
			
		} else {
			model.getDraw().setDrawer(false);
			model.notifyView(UpdateArguments.NEW_ROUND_FINDER);
		}

	}

	private void processHeyListen(List<String> arguments) {
		model.getChat().ajoutMessage(arguments.get(0), arguments.get(1));
	}
	
	private void processdrawline(List<String> arguments){
		model.getDraw().makeline(arguments.get(0), arguments.get(1), arguments.get(2), arguments.get(3), arguments.get(4), arguments.get(5), arguments.get(6), Integer.parseInt(arguments.get(7)));
	}

	private void processdrawcourbe(List<String> arguments) {
		model.getDraw().makecurve(arguments.get(0), arguments.get(1), arguments.get(2), arguments.get(3), arguments.get(4), arguments.get(5), arguments.get(6),arguments.get(7),arguments.get(8),arguments.get(9),arguments.get(10), Integer.parseInt(arguments.get(11)));
		
	}
	
	private void welcomeProcess() {
		model.gotWelcomedAsSpectator();
	}

	private void welcomeProcess(String pseudo) {
		model.gotWelcomed(pseudo);
	}


}
