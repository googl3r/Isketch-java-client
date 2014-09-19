package is.vue.components.game;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ChatPane extends JPanel {

	private JLabel[] players;
	private JScrollPane scroll;
	private JTextArea conversation;
	private JTextField message;
	private JButton sendButton;

	/**
	 * 
	 * 
	 * @param playersName
	 */

	public ChatPane() {
		super();

		setBorder(new TitledBorder("Chat"));
		setPreferredSize(new Dimension(300, 300));

		this.players = new JLabel[10];

		for (int i = 0; i < 10; i++) {
			this.players[i] = new JLabel("");
		}

		setLayout(new GridBagLayout());

		conversation = new JTextArea();
		conversation.setMinimumSize(new Dimension(0, 50));
		scroll = new JScrollPane(conversation);
		conversation.setEditable(false);

		message = new JTextField();

		sendButton = new JButton("Envoyer");

		addComponents();
	}

	private void addComponents() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (int i = 0; i < players.length; i++) {
			add(players[i], gbc);
			gbc.gridy++;
		}

		gbc.weighty = ++gbc.weightx;
		gbc.fill = GridBagConstraints.BOTH;
		add(scroll, gbc);

		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;

		gbc.weighty = 0;
		add(message, gbc);

		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		add(sendButton, gbc);
	}

	public JButton getSendButton() {
		return sendButton;
	}
	
	public void updatePlayersLabel(List<String> allPlayers,
			List<Integer> allScore, List<String> allRole) {
		int i = 0;
		for (String n : allPlayers){	
			String s = "";
			if (allScore.get(i)<2){
				s = "pt ";
			} else {
				s = "pts ";
			}
			players[i].setText(n+" : "+allScore.get(i)+s+" ("+allRole.get(i)+").");
			i++;
		}
	}

	public void ajoutMessage(String message) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		conversation.append(df.format(new Date()) + " " + message);
	}

	public JTextField getSaisie() {
		return message;
	}

	private static final long serialVersionUID = 1L;

	

}
