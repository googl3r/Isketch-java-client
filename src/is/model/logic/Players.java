package is.model.logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Players {

	private String myPseudo;

	private List<String> allPlayers;
	private List<Integer> allScore;
	private List<String> allRole;
	
	public void setAllPlayers(List<String> allPlayers) {
		this.allPlayers = allPlayers;
		this.allScore = new ArrayList<Integer>();
		this.allRole = new ArrayList<String>();
	}
	
	public void setAll(){
		this.allPlayers = new ArrayList<String>();
		this.allScore = new ArrayList<Integer>();
		this.allRole = new ArrayList<String>();
	}

	public List<String> getAllPlayers() {
		return allPlayers;
	}

	public String getMyPseudo() {
		return myPseudo;
	}

	public void setMyPseudo(String myPseudo) {
		this.myPseudo = myPseudo;
	}

	public int getPlayerIndex(String s) {
		int res = 0;
		for (String p : allPlayers)
			if (p.equals(s))
				return res;
			else
				res++;
		System.err.println("Player not found");
		return -1;
	}
	
	public String everyoneButMeToString() {
		StringBuilder str = new StringBuilder();

		final int truncedSize = allPlayers.size() - 1;
		String[] names = new String[truncedSize];
		int i = 0;
		for (String n : allPlayers) {
			if (!n.equals(myPseudo))
				names[i++] = n;
		}

		str.append(names[0]);
		if (truncedSize == 2) {
			str.append(" et " + names[1]);
		} else if (truncedSize == 3) {
			str.append(", " + names[1] + " et " + names[2]);
		}

		return str.toString();
	}
	

	public String getDrawer() {
		String s = "";
		for (int i = 0; i<allRole.size(); i++){
			if (allRole.get(i).equals("dessinateur")){
				s = allPlayers.get(i);
			}
		}
		return s;
	}
	
	public void updateScore(List<String> scores){
		for (int i = 0; i<scores.size(); i=i+2){
			int p = this.getPlayerIndex(scores.get(i));
			if (p != -1){				
				allScore.set(p, Integer.parseInt(scores.get(i+1)));
			}
		}
	}
	
	public void updateRole(String pseudo, String special){
		int i = getPlayerIndex(pseudo);
		if (i != -1){			
			allRole.set(i,special);
		}
	}
	
	public void updateRoles(String status){
		for (int i = 0; i < allRole.size(); i++) {
			if (!allRole.get(i).equals("parti")){				
				allRole.set(i, status);
			}
		}
	}

	public int getMyIndex() {
		return getPlayerIndex(myPseudo);
	}

	public List<Integer> getAllScore() {
		return allScore;
	}
	
	public List<String> getAllRole() {
		return allRole;
	}

	public void setAllScore(List<Integer> allScore) {
		this.allScore = allScore;
	}

	public void addPlayer(String string) {
		this.allPlayers.add(string);
		this.allScore.add(0);
		this.allRole.add("en attente");
	}

}
