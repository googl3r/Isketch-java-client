package is.vue.components.game;

import is.common.UpdateArguments;
import is.model.ModelFacade;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;


public class DrawPane extends JPanel {

	CubicCurve2D cub=new CubicCurve2D.Double();

	/**
	 * Nécéssaire pour le query state du graphique
	 */
	private ModelFacade model;
    private Image image;
    private Graphics2D g2d;
	private JPanel buttonPanel, finderpanel;
	private JTextField message;
	private JLabel guesstitle;
	private JButton upSize, downSize,red, blue, green, black, orange, yellow, violet, rose, lightblue, darkgreen, gray, sendguess, cheat, passround;
	
	
	private JLabel brushs = new JLabel("1");
	private JCheckBox checkcurve;

    public JCheckBox getCheckcurve() {
		return checkcurve;
	}

	public void setCheckcurve(JCheckBox checkcurve) {
		this.checkcurve = checkcurve;
	}

	private int brushSize = 1;
	@SuppressWarnings("unused")
	private GridBagConstraints gbc;

	private int elapsedtime;

	private Timer timer;

	private int drawertimeout = 90;




	public DrawPane(ModelFacade model) {
		super();

		this.model = model;

		//setLayout(new GridBagLayout());

		setMinimumSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(500, 500));
		

		
		gbc = new GridBagConstraints();
		
		addComponents();
	}

	public JButton getSendGuess(){
		return sendguess;
	}
	public JButton getCheat() {
		return cheat;
	}
	public JTextField getSaisie() {
		return message;
	}
	
	public JButton getPassround(){
		return passround;
	}
	
	private void addComponents() {
		finderpanel = new JPanel();
		message = new JTextField();
		sendguess = new JButton("Envoyer");
		guesstitle = new JLabel("Devinez : ");
		cheat = new JButton("Triche!");
	    message.setPreferredSize( new Dimension( 200, 24 ) );
	    finderpanel.add(guesstitle);
		finderpanel.add(message);
		finderpanel.add(sendguess);
		finderpanel.add(cheat);
		finderpanel.setVisible(false);
		
		this.add(finderpanel, BorderLayout.NORTH);
		
		buttonPanel = new JPanel();
		black = addButton(Color.BLACK, getClass().getResource("/images/noir.jpg"));
		blue = addButton(Color.BLUE, getClass().getResource("/images/bleu.jpg"));
		green = addButton(Color.GREEN, getClass().getResource("/images/vert.jpg"));
		red = addButton(Color.RED, getClass().getResource("/images/rouge.jpg"));
		orange = addButton(Color.ORANGE, getClass().getResource("/images/orange.jpg"));
		yellow = addButton(new Color(255, 255,0), getClass().getResource("/images/jaune.jpg"));
		violet = addButton(new Color(102, 45, 145), getClass().getResource("/images/violet.jpg"));
		rose = addButton(new Color(237, 30, 121), getClass().getResource("/images/rose.jpg"));
		lightblue = addButton(new Color(41, 171, 226), getClass().getResource("/images/bleuclair.jpg"));
		darkgreen = addButton(new Color(0, 104, 55), getClass().getResource("/images/vertfonce.jpg"));
		gray = addButton(new Color(128, 128, 128), getClass().getResource("/images/gris.jpg"));
		upSize = addButton(null, null);
		upSize.setText("+");
		downSize = addButton(null, null);
		downSize.setText("-");
		brushs.setText("" + brushSize);
		buttonPanel.add(brushs);
		checkcurve = new JCheckBox("Courbe");
		checkcurve.setActionCommand("COURBE");
		passround  = new JButton("Je passe!");
		buttonPanel.add(checkcurve);
		buttonPanel.add(passround);
		buttonPanel.setVisible(false);
		this.add(buttonPanel, BorderLayout.NORTH);
	}
	
	public void showDrawer(){
		finderpanel.setVisible(false);
		buttonPanel.setVisible(true);
		passround.setEnabled(true);
		model.notifyView(UpdateArguments.ROUND_BEGIN);
	}
	
	public void showFinder(){
		buttonPanel.setVisible(false);
		finderpanel.setVisible(true);
		cheat.setEnabled(true);
	}
	
	private JButton addButton(final Color color, URL url) {
		JButton button = new JButton();
		if (url != null)
			button.setIcon(new ImageIcon(url));
		button.setBackground(new Color(230, 240, 250));
		button.setBorder(BorderFactory.createEtchedBorder());
		if (color != null) {
			button.setForeground(Color.BLACK);
			button.setBackground(color);
		}
		buttonPanel.add(button);
		return (button);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        // initialises the image with the first paint
        // or checks the image size with the current panelsize
        if (image == null || image.getWidth(this) < getSize().width || image.getHeight(this) < getSize().height) {
            resetImage();
        }
        Rectangle r = g.getClipBounds();
        g.drawImage(image, r.x, r.y, r.width + r.x, r.height + r.y, r.x, r.y, r.width + r.x, r.height + r.y, null);
		model.getDraw().draw(g2d);
	}
	
	public void resetImage() {
        Image saveImage = image;
        Graphics2D saveG2d = g2d;
        image = createImage(getWidth(), getHeight());
        g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.black);
        if (saveG2d != null) {
            g2d.setColor(saveG2d.getColor());
            g2d.drawImage(saveImage, 0, 0, this);
        }
    }
 
    public Graphics2D getG2d() {
        return g2d;
    }
 
    public int getBrushSize() {
        return brushSize;
    }
    public void setPaintColor(final Color color) {
        g2d.setColor(color);
    }
 
    public void clearPaint() {
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        repaint();
        g2d.setColor(Color.black);
    }
 
    public void increaseBrushSize() {
        brushSize += 1;
        g2d.setStroke(new BasicStroke(brushSize));
        brushs.setText(""+brushSize);
    }
 
    public void decreaseBrushSize() {
        brushSize -= 1;
        if (brushSize <= 0) {
            brushSize = 1;
        }
        g2d.setStroke(new BasicStroke(brushSize));
        brushs.setText(""+brushSize);
    }
    
    public void setColor(String r, String v, String b){
    	g2d.setColor(new Color(Integer.parseInt(r), Integer.parseInt(v), Integer.parseInt(b)));
    }
    
    public void setColor(Color c){
    	g2d.setColor(c);
    }
    
    public void setBrushSize(int size){
    	brushSize = size;
    	g2d.setStroke(new BasicStroke(brushSize));
        brushs.setText(""+brushSize);
    }
    
    public void drawline(final Point start, final Point end){
		int x = start.x - (brushSize / 2) + 1;
		int y = start.y - (brushSize / 2) + 1;
		int x2 = end.x - (brushSize / 2) + 1;
		int y2 = end.y - (brushSize / 2) + 1;
		this.getG2d().drawLine(x, y, x2, y2);
		this.repaint();
    }

    public void drawcurve(Point a, Point b, Point c, Point d) {
    	cub.setCurve(a, b, c, d);
    	this.getG2d().draw(cub);
		this.repaint();
	}
    
    public JButton getYellow() {
		return yellow;
	}

	public JButton getViolet() {
		return violet;
	}

	public JButton getRose() {
		return rose;
	}

	public JButton getLightblue() {
		return lightblue;
	}

	public JButton getDarkgreen() {
		return darkgreen;
	}

	public JButton getGray() {
		return gray;
	}
	public JButton getUpSize() {
		return upSize;
	}

	public JButton getDownSize() {
		return downSize;
	}
	public JButton getRed() {
		return red;
	}
	public JButton getBlue() {
		return blue;
	}
	public JButton getGreen() {
		return green;
	}

	public JButton getBlack() {
		return black;
	}

	public JButton getOrange() {
		return orange;
	}

	public void clearAction() {
		finderpanel.setVisible(false);
		buttonPanel.setVisible(false);
	}
	
	public void beginTimer(){
		System.out.println("begion timer");
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (elapsedtime == drawertimeout) {                	
                	getPassround().doClick();
                	timer.stop();
                	elapsedtime = 0;
                } else {
                	getPassround().setText("Je passe "+(drawertimeout-elapsedtime)+"s");
                	elapsedtime++;
                }
            }
            };
        timer = new Timer( 1000, taskPerformer);
        
        timer.start();

	}
	
	public void stopTimer(){
		timer.stop();
		elapsedtime = 0;
	}
	
	private static final long serialVersionUID = 1L;

}
