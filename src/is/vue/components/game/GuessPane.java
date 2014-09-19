package is.vue.components.game;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GuessPane extends JScrollPane {

	private JTextPane textlog;
	private StyledDocument doc;
	
	
	public GuessPane() {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(new TitledBorder("Propositions"));

		textlog = new JTextPane();
		textlog.setEditable(false);

		doc = textlog.getStyledDocument();

		setViewportView(textlog);
		setMinimumSize(new Dimension(0, 100));
		setPreferredSize(getMinimumSize());

	}

	public void ajouterProposition(String origin, String info, Color c) {
		String str = " "+origin + " : " + info + "\n";

		Style s = textlog.addStyle(c.toString(), null);
		StyleConstants.setForeground(s, c);

		try {
			doc.insertString(doc.getLength(), str, s);
		} catch (BadLocationException e) {
		}

		textlog.setCaretPosition(doc.getLength() - 1);
	}


	private static final long serialVersionUID = 1L;
}
