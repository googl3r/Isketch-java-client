package is.vue;

import is.model.ModelFacade;
import is.vue.components.ConnectionPane;
import is.vue.components.GamePane;
import is.vue.components.MainFrame;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ViewFacade implements Observer {

	private ModelFacade model;

	private MainFrame mf;
	private ConnectionPane conn;
	private GamePane game;
	private boolean isReady;

	public ViewFacade(ModelFacade model) {
		this.model = model;

		model.addObserver(this);

		Runnable r = new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		};

		SwingUtilities.invokeLater(r);
	}

	private synchronized void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to load System's LooknFeel");
		}catch (InstantiationException in) {
            System.err.println("Unable to load System's LooknFeel");
        }catch (IllegalAccessException il) {
            System.err.println("Unable to load System's LooknFeel");

        }catch (UnsupportedLookAndFeelException un) {
            System.err.println("Unable to load System's LooknFeel");

        }

		mf = new MainFrame();
		conn = new ConnectionPane();
		game = new GamePane(model);

		mf.setFrameContentPane(conn);

		this.isReady = true;
		this.notifyAll();
	}

	public MainFrame getMf() {
		return mf;
	}

	@Override
	public void update(Observable o, Object arg) {
		new UpdateWorker(model, mf, conn, game, arg).execute();
	}

	public ConnectionPane getConnexionPane() {
		return conn;
	}

	public GamePane getGame() {
		return game;
	}

	public boolean isReady() {
		return isReady;
	}

}
