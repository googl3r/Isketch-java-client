package is.vue;

import is.common.UpdateArguments;
import is.model.ModelFacade;
import is.vue.components.ConnectionPane;
import is.vue.components.GamePane;
import is.vue.components.MainFrame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;
import javax.swing.Timer;


public class UpdateWorker extends SwingWorker<Void, Void> {

	private ModelFacade model;
	private MainFrame mf;
	private ConnectionPane conn;
	private GamePane game;
	private Object argument;
	private Timer timer;
	private int elapsedtime;

	public UpdateWorker(ModelFacade model, MainFrame mf, ConnectionPane conn,
			GamePane game, Object argument) {
		this.model = model;
		this.mf = mf;
		this.conn = conn;
		this.game = game;
		this.argument = argument;
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (argument != null && argument instanceof UpdateArguments) {
			switch ((UpdateArguments) argument) {
			case RESOLV_FAILED:

				conn.getConnect().setEnabled(true);
				conn.getRegister()
						.setEnabled(conn.getCheck_pass().isSelected());
				conn.showInfo("Impossible de résoudre l'adresse");
				break;
			case CONN_INIT:
				conn.getConnect().setEnabled(false);
				conn.getRegister().setEnabled(false);
				conn.showInfo("Connexion en cours");
				break;
			case CONN_FAILED:
				conn.getConnect().setEnabled(true);
				conn.getRegister()
						.setEnabled(conn.getCheck_pass().isSelected());
				conn.showInfo("Connexion impossible\n"
						+ "Vérifiez que le serveur est actif");
				break;
			case CONN_SUCCESS:
				connectionSuccess();
				break;
			case LOGIN_FAILED:
				conn.getConnect().setEnabled(true);
				conn.getRegister().setEnabled(true);
				conn.showInfo("Mot de passe incorrect");
				break;
			case REGISTER_FAILED:
				conn.getConnect().setEnabled(true);
				conn.getRegister().setEnabled(true);
				conn.showInfo("Impossible de s'enregistrer\nCe pseudo est déjà pris");
				break;
			case CHAT_UPDATE:
				game.getChat().ajoutMessage(model.getChat().getMessage());
				break;
			case GUESS_UPDATE:
				game.getGuess().ajouterProposition(model.getGuess().getGuesser(), model.getGuess().getMessage(), Color.black);
				break;
			case SCORE_UPDATE:
				game.getDraw().stopTimer();
				updatePlayers();
				break;
			case DRAW_LINE:
				game.getDraw().setBrushSize(model.getDraw().getSize());
				game.getDraw().setColor(model.getDraw().getCouleur());
				game.getDraw().drawline(model.getDraw().getA(), model.getDraw().getB());
				break;
			case DRAW_CURVE:
				game.getDraw().setBrushSize(model.getDraw().getSize());
				game.getDraw().setColor(model.getDraw().getCouleur());
				game.getDraw().drawcurve(model.getDraw().getA(), model.getDraw().getB(),model.getDraw().getC(), model.getDraw().getD());
				break;
			case NEW_ROUND_DRAWER:
				game.getGuess().ajouterProposition("Serveur", "Nouveau mot", Color.magenta);
				game.getLog().ajouterMessage("Serveur",
						"Vous etes le dessinateur, a vous de dessiner " + model.getDraw().getMot()+".", Color.red);
				game.getDraw().showDrawer();
				break;
			case NEW_ROUND_FINDER:
				game.getGuess().ajouterProposition("Serveur", "Nouveau mot", Color.magenta);
				game.getLog().ajouterMessage("Serveur",
						"Vous etes un chercheur, a vous de prosposer", Color.red);
				game.getDraw().showFinder();
				break;
			case END_ROUND:
				if (model.getDraw().isDrawer()){					
					game.getDraw().stopTimer();
				}
				game.getDraw().clearAction();
			case NEW_PLAYER:
				updatePlayers();
				break;
			case PLAYERS_UPDATE:
				updatePlayers();
				break;
			case ROUND_BEGIN:
				beginTimer();
				break;
			default:
				break;
			}
		}
		return null;
	}

	private void beginTimer() {
		game.getDraw().beginTimer();
	}
	
	

	private void updatePlayers() {
		game.getChat().updatePlayersLabel(model.getPlayers().getAllPlayers(), model.getPlayers().getAllScore(), model.getPlayers().getAllRole());
	}

	private void connectionSuccess() {
		mf.setFrameContentPane(game);
		mf.pack();
		game.getLog().ajouterMessage("Serveur",
				"Bienvenue " + model.getPlayers().getMyPseudo(), Color.GREEN);
	}
}
