package is.vue.components;

import is.model.ModelFacade;
import is.vue.components.game.ChatPane;
import is.vue.components.game.DrawPane;
import is.vue.components.game.GuessPane;
import is.vue.components.game.LogPane;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;


public class GamePane extends JPanel {

	private DrawPane draw;
	private ChatPane chat;
	private LogPane log;
	private GuessPane guess;

	private GridBagConstraints gbc;

	public GamePane(ModelFacade model) {
		super();

		setLayout(new GridBagLayout());

		setMinimumSize(new Dimension(300, 300));

		draw = new DrawPane(model);
		log = new LogPane();
		chat = new ChatPane();
		guess = new GuessPane();

		gbc = new GridBagConstraints();

		addComponents();
	}
	

	private void addComponents() {
		gbc.insets = new Insets(15, 15, 15, 15);

		gbc.weightx = gbc.weighty = 2;
		gbc.gridheight = 1;
		gbc.gridx = gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		add(draw, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = gbc.weighty = 1;
		gbc.gridy++;
		add(log, gbc);
		
		gbc.weightx = gbc.weighty = 2;
		gbc.gridheight = 1;
		gbc.gridx = 1; 
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		add(chat, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = gbc.weighty = 1;
		gbc.gridy++;
		add(guess, gbc);
	}




	public LogPane getLog() {
		return log;
	}

	public DrawPane getDraw() {
		return draw;
	}

	public ChatPane getChat() {
		return chat;
	}
	
	public GuessPane getGuess() {
		return guess;
	}

	@Override
	public void paintComponents(Graphics g) {

		super.paintComponents(g);
	}

	private static final long serialVersionUID = 1L;

}
