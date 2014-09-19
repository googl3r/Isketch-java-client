package is.model.logic;

import is.common.UpdateArguments;
import is.model.ModelFacade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;


public class Draw {
	private ModelFacade observable;

	public final static int width = 500, height = 500;
	private Color couleur;
	private int size;
	private Point a, b, c, d;

	private boolean isDrawer;
	private boolean isCurve;

	private boolean isSpectatorMode;

	private String mot;


	// field drone

	public Draw(ModelFacade modelFacade) {
		this.observable = modelFacade;
		this.isDrawer = false;
		this.isCurve = false;
		this.isSpectatorMode = false;
		this.a = new Point();
		this.b = new Point();
		this.couleur = Color.black;
		this.size = 1;
	}

	


	public void makeline(String x, String y, String x1, String y1, String r, String g, String b, int size){
		this.setA(new Point(Integer.parseInt(x), Integer.parseInt(y)));
		this.setB(new Point(Integer.parseInt(x1), Integer.parseInt(y1)));
		this.setCouleur(new Color(Integer.parseInt(r), Integer.parseInt(g),Integer.parseInt(b)));
		this.setSize(size);
		observable.notifyView(UpdateArguments.DRAW_LINE);
	}
	
	public void makecurve(String x, String y, String x1,
			String y1, String x2, String y2, String x3,
			String y3, String r, String g, String b,
			int size) {
		this.setA(new Point(Integer.parseInt(x), Integer.parseInt(y)));
		this.setB(new Point(Integer.parseInt(x1), Integer.parseInt(y1)));
		this.setC(new Point(Integer.parseInt(x2), Integer.parseInt(y2)));
		this.setD(new Point(Integer.parseInt(x3), Integer.parseInt(y3)));
		this.setCouleur(new Color(Integer.parseInt(r), Integer.parseInt(g),Integer.parseInt(b)));
		this.setSize(size);
		observable.notifyView(UpdateArguments.DRAW_CURVE);
	}
	
	private void drawGrid(Graphics2D g2) {

	}

	public void draw(Graphics2D g2) {
		drawGrid(g2);
	}
	
	public void line(String x, String y, String x1, String y1, String r, String v, String b, String size){
		
	}

	public void setSpectatorMode() {
		this.isSpectatorMode = true;
	}

	public boolean isSpectatorMode() {
		return isSpectatorMode;
	}


	
	public boolean isDrawer() {
		return isDrawer;
	}

	public void setDrawer(boolean isDrawer) {
		this.isDrawer = isDrawer;
	}

	public Color getCouleur() {
		return couleur;
	}

	public void setCouleur(Color couleur) {
		this.couleur = couleur;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Point getB() {
		return b;
	}

	public void setB(Point b) {
		this.b = b;
	}

	public Point getA() {
		return a;
	}

	public void setA(Point a) {
		this.a = a;
	}

	public String getMot() {
		return mot;
	}
	
	public void setMot(String mot) {
		this.mot = mot;
	}

	public Point getD() {
		return d;
	}

	public void setD(Point d) {
		this.d = d;
	}

	public Point getC() {
		return c;
	}

	public void setC(Point c) {
		this.c = c;
	}

	public boolean isCurve() {
		return isCurve;
	}

	public void setCurve(boolean isCurve) {
		this.isCurve = isCurve;
	}

}
