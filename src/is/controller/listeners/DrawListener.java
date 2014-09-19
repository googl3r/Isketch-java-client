package is.controller.listeners;

import is.controller.Controller;
import is.controller.net.ERequest;
import is.controller.net.Request;
import is.model.ModelFacade;
import is.vue.ViewFacade;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;


public class DrawListener implements MouseListener, MouseMotionListener, ActionListener {

	private ModelFacade model;
	private Controller controller;
	private ViewFacade view;
    private Point firstpoint, secondpoint, thirdpoint;
    
	private List<String> actions;

		public DrawListener(ModelFacade model, ViewFacade view,
			Controller controller) {
		this.controller = controller;
		this.model = model;
		this.view = view;
		this.actions = new ArrayList<>();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (model.getDraw().isDrawer()){			
			if(model.getDraw().isCurve()){
				if (firstpoint == null){
					firstpoint = e.getPoint();
					actions.add(""+(int)e.getPoint().getX());
					actions.add(""+(int)e.getPoint().getY());
				}  else {
					thirdpoint = e.getPoint();
					actions.add(""+(int)e.getPoint().getX());
					actions.add(""+(int)e.getPoint().getY());
				}
			} else {				
				firstpoint = e.getPoint();
				actions.add(""+(int)e.getPoint().getX());
				actions.add(""+(int)e.getPoint().getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (model.getDraw().isDrawer()){
			if (model.getDraw().isCurve()){
				if (secondpoint==null){
					secondpoint= e.getPoint();
					actions.add(""+(int)e.getPoint().getX());
					actions.add(""+(int)e.getPoint().getY());
				} else {					
					actions.add(""+(int)e.getPoint().getX());
					actions.add(""+(int)e.getPoint().getY());
					controller.makeRequest(new Request(ERequest.SET_COURBE, actions));
					view.getGame().getDraw().drawcurve(firstpoint, secondpoint, thirdpoint, e.getPoint());
					actions.clear();
					pointsnull();
				}
			} else {				
				actions.add(""+(int)e.getPoint().getX());
				actions.add(""+(int)e.getPoint().getY());
				controller.makeRequest(new Request(ERequest.SET_LINE, actions));
				view.getGame().getDraw().drawline(firstpoint, e.getPoint());
				actions.clear();
				firstpoint = null;
			}
		}
	}

	private void pointsnull() {
		firstpoint = null;
		secondpoint = null;
		thirdpoint = null;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (e.getSource() == view.getGame().getDraw().getGreen()) {
			makePaintColor(view.getGame().getDraw().getGreen());
		} else if (e.getSource() == view.getGame().getDraw().getBlue()) {
			makePaintColor(view.getGame().getDraw().getBlue());
		} else if (e.getSource() == view.getGame().getDraw().getOrange()) {
			makePaintColor(view.getGame().getDraw().getOrange());
		} else if (e.getSource() == view.getGame().getDraw().getRed()) {
			makePaintColor(view.getGame().getDraw().getRed());
		} else if (e.getSource() == view.getGame().getDraw().getBlack()) {
			makePaintColor(view.getGame().getDraw().getBlack());
		} else if (e.getSource() == view.getGame().getDraw().getLightblue()) {
			makePaintColor(view.getGame().getDraw().getLightblue());
		} else if (e.getSource() == view.getGame().getDraw().getYellow()) {
			makePaintColor(view.getGame().getDraw().getYellow());
		} else if (e.getSource() == view.getGame().getDraw().getDarkgreen()) {
			makePaintColor(view.getGame().getDraw().getDarkgreen());
		} else if (e.getSource() == view.getGame().getDraw().getGray()) {
			makePaintColor(view.getGame().getDraw().getGray());
		} else if (e.getSource() == view.getGame().getDraw().getViolet()) {
			makePaintColor(view.getGame().getDraw().getViolet());
		} else if (e.getSource() == view.getGame().getDraw().getRose()) {
			makePaintColor(view.getGame().getDraw().getRose());
		} else if(e.getSource() == view.getGame().getDraw().getSendGuess()){
			String msgToSend = view.getGame().getDraw().getSaisie().getText().trim();
			if (!msgToSend.isEmpty()) {
				this.controller.makeRequest(new Request(ERequest.GUESS, Arrays
						.asList(msgToSend)));
				view.getGame().getDraw().getSaisie().setText("");
			}
		} else if(e.getSource() == view.getGame().getDraw().getSaisie()){
			String msgToSend = view.getGame().getDraw().getSaisie().getText().trim();
			if (!msgToSend.isEmpty()) {
				this.controller.makeRequest(new Request(ERequest.GUESS, Arrays
						.asList(msgToSend)));
				view.getGame().getDraw().getSaisie().setText("");
			}
		} else if(e.getSource() == view.getGame().getDraw().getCheat()){
			String drawer = model.getPlayers().getDrawer();
			this.controller.makeRequest(new Request(ERequest.CHEAT, Arrays
					.asList(drawer)));
			view.getGame().getDraw().getCheat().setEnabled(false);
		} else if(e.getSource() == view.getGame().getDraw().getPassround()){
			this.controller.makeRequest(new Request(ERequest.PASS, Collections
					.<String> emptyList()));
			view.getGame().getDraw().getPassround().setEnabled(false);
		}
		if (s.equals("+")) {
			view.getGame().getDraw().increaseBrushSize();
			actions.add(""+view.getGame().getDraw().getBrushSize());
			controller.makeRequest(new Request(ERequest.SET_SIZE, actions));
			actions.clear();
		} else if (s.equals("-")) {
			view.getGame().getDraw().decreaseBrushSize();
			actions.add(""+view.getGame().getDraw().getBrushSize());
			controller.makeRequest(new Request(ERequest.SET_SIZE, actions));
			actions.clear();
		} else if (s.equals("COURBE")){
			if (view.getGame().getDraw().getCheckcurve().isSelected()){				
				model.getDraw().setCurve(true);
			} else {
				model.getDraw().setCurve(false);
			}
		}
	}


	private void makePaintColor(JButton bu) {
		// TODO Auto-generated method stub
		System.out.println(bu.getBackground().toString());
		view.getGame().getDraw().setPaintColor(bu.getBackground());
		actions.add(""+bu.getBackground().getRed());
		actions.add(""+bu.getBackground().getGreen());
		actions.add(""+bu.getBackground().getBlue());
		controller.makeRequest(new Request(ERequest.SET_COLOR, actions));
		actions.clear();
	}

}
