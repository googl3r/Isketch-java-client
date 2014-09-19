package is.run;

import is.vue.components.game.LogPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;


public class ServeurTest {

	InetAddress adr;
	ServerSocket sock;
	JFrame jf;
	JButton send;
	LogPane pane;
	JTextField command;
	JScrollPane jsp;
	JPanel jp;

	PrintWriter out;
	BufferedReader in;

	public ServeurTest() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		jf = new JFrame("Serveur test");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jp = new JPanel();
		pane = new LogPane();

		send = new JButton("Lancer le serveur");
		command = new JTextField();

		ActionListener al;
		send.addActionListener(al = new ActionListener() {
			boolean isStarted = false;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isStarted) {
					send.setEnabled(false);
					send.setText("Envoyer");
					jf.pack();
					new SwingWorker<Void, Void>() {
						public Void doInBackground() {
							startServer();
							return null;
						};
					}.execute();
					isStarted = true;
				} else {
					synchronized (this) {
						out.println(command.getText());
						appendLine("Commande envoyee : " + command.getText());
						command.setText("");
					}
				}
			}
		});

		command.addActionListener(al);

		jp.setPreferredSize(new Dimension(400, 400));
		jp.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 1;
		jp.add(pane, gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 0;
		gbc.gridy++;
		jp.add(command, gbc);
		gbc.weightx = 0;
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		jp.add(send, gbc);

		jf.setContentPane(jp);

		jf.setVisible(true);
		jf.pack();
	}

	// ouverture socket + attente de lecture
	public synchronized void startServer() {
		try {
			sock = new ServerSocket(2013);
			appendLine("Serveur a l'ecoute sur le port : "
					+ sock.getLocalPort());

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		while (true) {
			appendLine("En attente d'un client");
			try (Socket client = sock.accept()) {
				send.setEnabled(true);
				appendLine("Client connecte");

				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(client.getOutputStream())), true);
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));

				// Lecture continue des messages du client
				String str;
				synchronized (this) {
					while ((str = in.readLine()) != null) {
						appendLine("Commande recue : " + str);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						out.flush();
					}
				}
				out.close();
				in.close();
				appendLine("Client deconnecte");
			} catch (IOException e) {
				appendLine("Client deconnecte");
				send.setEnabled(false);
			}
		}
	}

	void appendLine(String str) {
		pane.ajouterMessage("", str, Color.black);
	}

	public static void main(String[] args) {
		new ServeurTest();
	}
}