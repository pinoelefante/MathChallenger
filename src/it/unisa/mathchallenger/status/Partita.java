package it.unisa.mathchallenger.status;

import java.util.ArrayList;

public class Partita {
	private Account utente_sfidato;
	private ArrayList<Domanda> domande;
	private int id_partita;
	private int stato_partita;
	
	public Partita(){
		domande=new ArrayList<Domanda>(6);
	}
	public Account getUtenteSfidato(){
		return utente_sfidato;
	}
	public void setUtenteSfidato(Account a){
		utente_sfidato=a;
	}
	public void aggiungiDomanda(Domanda d){
		domande.add(d);
	}
	public Domanda getDomanda(int i){
		return domande.get(i);
	}
	public int getNumDomande(){
		return domande.size();
	}
	public int getIDPartita(){
		return id_partita;
	}
	public void setIDPartita(int i){
		id_partita=i;
	}
	public int getStatoPartita(){
		return stato_partita;
	}
	public void setStatoPartita(int s){
		stato_partita=s;
	}
}