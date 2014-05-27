package it.unisa.mathchallenger;

import java.io.IOException;
import java.util.ArrayList;

import it.unisa.mathchallenger.communication.Communication;
import it.unisa.mathchallenger.communication.CommunicationMessageCreator;
import it.unisa.mathchallenger.communication.CommunicationParser;
import it.unisa.mathchallenger.communication.Messaggio;
import it.unisa.mathchallenger.eccezioni.ConnectionException;
import it.unisa.mathchallenger.eccezioni.LoginException;
import it.unisa.mathchallenger.status.Domanda;
import it.unisa.mathchallenger.status.Partita;
import it.unisa.mathchallenger.status.Status;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class GiocaPartitaActivity extends Activity {
	private Communication	comm;
	private final static int DURATA_DOMANDA   = 10;
	private Partita		  partita;
	private int			  domanda_corrente = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gioca_partita);
		comm = Communication.getInstance();
		int id = getIntent().getIntExtra("id_partita", 0);
		if (id > 0) {
			Messaggio m = CommunicationMessageCreator.getInstance().createGetDomande(id);
			try {
				comm.send(m);
				ArrayList<Domanda> list = CommunicationParser.getInstance().parseGetDomande(m);
				partita = Status.getInstance().getPartitaByID(id);
				for (int i = 0; i < list.size(); i++) {
					Domanda dom = list.get(i);
					partita.aggiungiDomanda(dom);
				}
				scriviDomanda();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		View view = (View) findViewById(R.id.container);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			view.setBackgroundResource(R.drawable.prova2hdhorizontal);
		}
		else {
			view.setBackgroundResource(R.drawable.prova2hd);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gioca_partita, menu);
		return true;
	}

	public void onBackPressed() {

	};

	private Thread tempo = null;

	private void scriviDomanda() {
		if (domanda_corrente < partita.getNumDomande()) {
			Domanda d = partita.getDomanda(domanda_corrente);
			//Button domanda = (Button) findViewById(R.id.gioca_partita_domanda);
			Button risp1 = (Button) findViewById(R.id.gioca_partita_risp1);
			Button risp2 = (Button) findViewById(R.id.gioca_partita_risp2);
			Button risp3 = (Button) findViewById(R.id.gioca_partita_risp3);
			Button risp4 = (Button) findViewById(R.id.gioca_partita_risp4);
			//domanda.setText(d.getDomanda());
			String dom = d.getDomanda();
			LinearLayout contdom = (LinearLayout) findViewById(R.id.contdomanda);
			contdom.removeAllViews();
			for(int i=0;i<dom.length();i++){				
				switch(dom.charAt(i)){
					case '0': 
						ImageView q0 = new ImageView(this);
						q0.setImageResource(R.drawable.q0);
						contdom.addView(q0);
						break;
					case '1': 
						ImageView q1 = new ImageView(this);
						q1.setImageResource(R.drawable.q1);
						contdom.addView(q1);
						break;
					case '2': 
						ImageView q2 = new ImageView(this);
						q2.setImageResource(R.drawable.q2);
						contdom.addView(q2);
						break;
					case '3': 
						ImageView q3 = new ImageView(this);
						q3.setImageResource(R.drawable.q3);
						contdom.addView(q3);
						break;
					case '4': 
						ImageView q4 = new ImageView(this);
						q4.setImageResource(R.drawable.q4);
						contdom.addView(q4);
						break;
					case '5': 
						ImageView q5 = new ImageView(this);
						q5.setImageResource(R.drawable.q5);
						contdom.addView(q5);
						break;
					case '6': 
						ImageView q6 = new ImageView(this);
						q6.setImageResource(R.drawable.q6);
						contdom.addView(q6);
						break;
					case '7': 
						ImageView q7 = new ImageView(this);
						q7.setImageResource(R.drawable.q7);
						contdom.addView(q7);
						break;
					case '8': 
						ImageView q8 = new ImageView(this);
						q8.setImageResource(R.drawable.q8);
						contdom.addView(q8);
						break;
					case '9': 
						ImageView q9 = new ImageView(this);
						q9.setImageResource(R.drawable.q9);
						contdom.addView(q9);
						break;
					case '+': 
						ImageView q10 = new ImageView(this);
						q10.setImageResource(R.drawable.q10);
						contdom.addView(q10);
						break;
					case '-': 
						ImageView q11 = new ImageView(this);
						q11.setImageResource(R.drawable.q11);
						contdom.addView(q11);
						break;
					case ':': 
						ImageView q12 = new ImageView(this);
						q12.setImageResource(R.drawable.q12);
						contdom.addView(q12);
						break;
					case 'x': 
						ImageView q13 = new ImageView(this);
						q13.setImageResource(R.drawable.q13);
						contdom.addView(q13);
						break;
					case '(': 
						ImageView q14 = new ImageView(this);
						q14.setImageResource(R.drawable.q14);
						contdom.addView(q14);
						break;
					case ')': 
						ImageView q15 = new ImageView(this);
						q15.setImageResource(R.drawable.q15);
						contdom.addView(q15);
						break;
				
				}
			}
			String r1 = (d.getRisposta(0) + "").endsWith(".0") ? (d.getRisposta(0) + "").substring(0, (d.getRisposta(0) + "").length() - 2) : d.getRisposta(0) + "";
			risp1.setText(r1);
			risp1.setTextColor(Color.WHITE);
			String r2 = (d.getRisposta(1) + "").endsWith(".0") ? (d.getRisposta(1) + "").substring(0, (d.getRisposta(1) + "").length() - 2) : d.getRisposta(1) + "";
			risp2.setText(r2);
			risp2.setTextColor(Color.WHITE);
			String r3 = (d.getRisposta(2) + "").endsWith(".0") ? (d.getRisposta(2) + "").substring(0, (d.getRisposta(2) + "").length() - 2) : d.getRisposta(2) + "";
			risp3.setText(r3);
			risp3.setTextColor(Color.WHITE);
			String r4 = (d.getRisposta(3) + "").endsWith(".0") ? (d.getRisposta(3) + "").substring(0, (d.getRisposta(3) + "").length() - 2) : d.getRisposta(3) + "";
			risp4.setText(r4);
			risp4.setTextColor(Color.WHITE);

			risp1.setOnClickListener(new clickRisposta(d, d.getRisposta(0)));
			risp2.setOnClickListener(new clickRisposta(d, d.getRisposta(1)));
			risp3.setOnClickListener(new clickRisposta(d, d.getRisposta(2)));
			risp4.setOnClickListener(new clickRisposta(d, d.getRisposta(3)));
			final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
			runOnUiThread(new Runnable() {
				public void run() {
					bar.setMax(DURATA_DOMANDA);
				}
			});

			tempo = new timer_partita(bar);
			tempo.start();
		}
		else {
			Messaggio mess = CommunicationMessageCreator.getInstance().createRisposte(partita);
			try {
				comm.send(mess);
			}
			catch (IOException | LoginException | ConnectionException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(getApplicationContext(), VisualizzaPartitaActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("id_partita", partita.getIDPartita());
			startActivity(intent);
		}
	}

	class clickRisposta implements Button.OnClickListener {
		Domanda domanda;
		float   risposta;

		public clickRisposta(Domanda d, float risposta) {
			domanda = d;
			this.risposta = risposta;
		}

		public void onClick(View v) {
			tempo.interrupt();
			domanda.setRispostaUtente(this.risposta);
			domanda_corrente++;
			scriviDomanda();
		}
	}

	class timer_partita extends Thread {
		private ProgressBar progressbar;

		public timer_partita(ProgressBar bar) {
			progressbar = bar;
		}

		public void run() {
			int time = DURATA_DOMANDA;
			while (time > 0) {
				try {
					sleep(1000L);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				time--;
				progressUpdater pu = new progressUpdater(progressbar, time);
				runOnUiThread(pu);
			}
			// TODO assegna risposta sbagliata
			runOnUiThread(new Runnable() {
				public void run() {
					domanda_corrente++;
					scriviDomanda();
				}
			});
		}
	}

	class progressUpdater implements Runnable {
		int		 value;
		ProgressBar p_bar;

		public progressUpdater(ProgressBar bar, int val) {
			value = val;
			p_bar = bar;
		}

		public void run() {
			p_bar.setProgress(value);
		}
	}
}
