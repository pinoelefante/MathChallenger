package it.unisa.mathchallenger;

import java.io.IOException;
import java.util.ArrayList;

import it.unisa.mathchallenger.communication.Communication;
import it.unisa.mathchallenger.communication.CommunicationMessageCreator;
import it.unisa.mathchallenger.communication.CommunicationParser;
import it.unisa.mathchallenger.communication.Messaggio;
import it.unisa.mathchallenger.eccezioni.ConnectionException;
import it.unisa.mathchallenger.eccezioni.DettagliNonPresentiException;
import it.unisa.mathchallenger.eccezioni.LoginException;
import it.unisa.mathchallenger.status.Domanda;
import it.unisa.mathchallenger.status.Partita;
import it.unisa.mathchallenger.status.StatoPartita;
import it.unisa.mathchallenger.status.Status;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizzaPartitaActivity extends ActionBarActivity {
	private Communication comm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		comm = Communication.getInstance();
		setContentView(R.layout.activity_visualizza_partita);
		int idPartita = getIntent().getIntExtra("id_partita", 0);
		if (idPartita > 0) {
			Partita p = Status.getInstance().getPartitaByID(idPartita);
			if (p != null) {
				Messaggio m = CommunicationMessageCreator.getInstance().createGetDettagliPartita(idPartita);
				try {
					comm.send(m);
					StatoPartita stato = CommunicationParser.getInstance().parseGetDettaglioPartita(m);
					if (stato != null) {
						int oldStat = p.getStatoPartita();
						p.setDettagliPartita(stato);
						int newStat = p.getStatoPartita();
						if (oldStat != newStat)
							Status.getInstance().aggiornaPartita(p);
						disegna(p);
						if (p.getDettagliPartita() != null) {
							visualizzaRisposte(stato);
							aggiungiListenerShowDomande(p);
						}
						addListenerAddAmico(p);
					}
					else {
						Toast.makeText(getApplicationContext(), m.getErrorID() >= 0 ? getResources().getString(m.getErrorID()) : m.getErrorMessage(), Toast.LENGTH_LONG).show();
					}
				}
				catch (IOException | LoginException | ConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "bundle = null", Toast.LENGTH_LONG).show();
		}
		View view = (View) findViewById(R.id.containerVisualizzaPartita);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			view.setBackgroundResource(R.drawable.sfondo_landscape_no);
		}
		else {
			view.setBackgroundResource(R.drawable.sfondo_potrait_no);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		View view = (View) findViewById(R.id.containerVisualizzaPartita);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			view.setBackgroundResource(R.drawable.sfondo_landscape_no);
		}
		else {
			view.setBackgroundResource(R.drawable.sfondo_potrait_no);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visualizza_partita, menu);
		return true;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	private void disegna(final Partita p) {
		TextView tv_user_current = (TextView) findViewById(R.id.visualizzaUsernameProprioTV);
		tv_user_current.setText(Status.getInstance().getUtente().getUsername());
		TextView tv_avversario = (TextView) findViewById(R.id.VisualizzaUsernameAvversarioTV);
		tv_avversario.setText(p.getUtenteSfidato().getUsername());

		LinearLayout azione_container = (LinearLayout) findViewById(R.id.visualizza_azione_container);
		StatoPartita dett = p.getDettagliPartita();
		if (dett == null) {
			Toast.makeText(getApplicationContext(), "Dettagli partita = null", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (p.getStatoPartita()) {
			case Partita.CREATA:
			case Partita.INIZIATA:
				if (dett != null && !p.getDettagliPartita().isUtenteRisposto()) {
					float scale = getApplicationContext().getResources().getDisplayMetrics().density;
					int height = (int) (scale * 45 + 0.5f);
					Button b_gioca = new Button(getApplicationContext());
					LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
					dim.setMargins(0, 10, 0, 15);
					b_gioca.setLayoutParams(dim);
					b_gioca.setTextColor(Color.WHITE);
					b_gioca.setText(R.string.gioca);
					b_gioca.setBackgroundResource(R.drawable.button_style);
					b_gioca.setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							int cur_orientation = getResources().getConfiguration().orientation;
							if (cur_orientation == Configuration.ORIENTATION_LANDSCAPE) {
								new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(R.string.partita_avviso_landscape).setCancelable(false).setNegativeButton("OK", new OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(getApplicationContext(), GiocaPartitaActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										intent.putExtra("id_partita", p.getIDPartita());
										startActivity(intent);
									}
								}).show();
							}
							else {
								Intent intent = new Intent(getApplicationContext(), GiocaPartitaActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("id_partita", p.getIDPartita());
								startActivity(intent);
							}
						}
					});
					azione_container.addView(b_gioca);
				}
				else {
					TextView in_attesa = new TextView(getApplicationContext());
					in_attesa.setGravity(Gravity.CENTER);
					in_attesa.setTextColor(Color.BLACK);
					in_attesa.setText(R.string.in_attesa_dell_avversario);
					in_attesa.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					in_attesa.setTypeface(null, Typeface.BOLD);
					azione_container.addView(in_attesa);
				}
				break;
			case Partita.PAREGGIATA:
				TextView pareggiata = new TextView(getApplicationContext());
				pareggiata.setText(R.string.pareggiata);
				pareggiata.setGravity(Gravity.CENTER);
				pareggiata.setTextColor(Color.BLACK);
				pareggiata.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				pareggiata.setTypeface(null, Typeface.BOLD);
				azione_container.addView(pareggiata);
				break;
			case Partita.VINCITORE_1:
			case Partita.VINCITORE_2:
				try {
					if (p.haiVinto()) {
						TextView vinta = new TextView(getApplicationContext());
						vinta.setText(R.string.vinto);
						vinta.setGravity(Gravity.CENTER);
						vinta.setTextColor(Color.BLACK);
						vinta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
						vinta.setTypeface(null, Typeface.BOLD);
						azione_container.addView(vinta);
					}
					else {
						TextView persa = new TextView(getApplicationContext());
						persa.setText(R.string.persa);
						persa.setGravity(Gravity.CENTER);
						persa.setTextColor(Color.BLACK);
						persa.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
						persa.setTypeface(null, Typeface.BOLD);
						azione_container.addView(persa);
					}
				}
				catch (DettagliNonPresentiException e) {
					TextView errore = new TextView(getApplicationContext());
					errore.setText(R.string.visualizza_errore_dettagli);
					errore.setGravity(Gravity.CENTER);
					errore.setTextColor(Color.BLACK);
					errore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
					errore.setTypeface(null, Typeface.BOLD);
					azione_container.addView(errore);
					e.printStackTrace();
				}
				break;
			case Partita.TEMPO_SCADUTO:
				TextView tempo_scaduto = new TextView(getApplicationContext());
				tempo_scaduto.setText(R.string.tempo_scaduto);
				tempo_scaduto.setGravity(Gravity.CENTER);
				tempo_scaduto.setTextColor(Color.BLACK);
				tempo_scaduto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				tempo_scaduto.setTypeface(null, Typeface.BOLD);
				azione_container.addView(tempo_scaduto);
				break;
			case Partita.ABBANDONATA_1:
			case Partita.ABBANDONATA_2:
				TextView abbandonata = new TextView(getApplicationContext());

				abbandonata.setGravity(Gravity.CENTER);
				abbandonata.setTextColor(Color.BLACK);
				abbandonata.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				abbandonata.setTypeface(null, Typeface.BOLD);
				azione_container.addView(abbandonata);
				try {
					if (p.isAbbandonata()) {
						abbandonata.setText(R.string.abbandonata);
					}
					else
						abbandonata.setText(R.string.abbandonata_sfidante);
				}
				catch (DettagliNonPresentiException e) {
					abbandonata.setText(R.string.visualizza_errore_dettagli);
					e.printStackTrace();
				}
				break;
		}
	}

	private void visualizzaRisposte(StatoPartita p) {
		int risposteutente[] = p.getRisposteUtente();
		int risposteavversario[] = p.getRisposteAvversario();

		for (int i = 0; i < 6; i++) {
			switch (i) {
				case 0:
					ImageView risut = (ImageView) findViewById(R.id.risutente1);
					ImageView risavv = (ImageView) findViewById(R.id.risavv1);
					if (risposteutente[i] == Domanda.ESATTA)
						risut.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
				case 1:
					ImageView risut2 = (ImageView) findViewById(R.id.risutente2);
					ImageView risavv2 = (ImageView) findViewById(R.id.risavv2);
					if (risposteutente[i] == Domanda.ESATTA)
						risut2.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut2.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut2.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv2.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv2.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv2.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
				case 2:
					ImageView risut3 = (ImageView) findViewById(R.id.risutente3);
					ImageView risavv3 = (ImageView) findViewById(R.id.risavv3);
					if (risposteutente[i] == Domanda.ESATTA)
						risut3.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut3.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut3.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv3.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv3.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv3.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
				case 3:
					ImageView risut4 = (ImageView) findViewById(R.id.risutente4);
					ImageView risavv4 = (ImageView) findViewById(R.id.risavv4);
					if (risposteutente[i] == Domanda.ESATTA)
						risut4.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut4.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut4.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv4.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv4.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv4.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
				case 4:
					ImageView risut5 = (ImageView) findViewById(R.id.risutente5);
					ImageView risavv5 = (ImageView) findViewById(R.id.risavv5);
					if (risposteutente[i] == Domanda.ESATTA)
						risut5.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut5.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut5.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv5.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv5.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv5.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
				case 5:
					ImageView risut6 = (ImageView) findViewById(R.id.risutente6);
					ImageView risavv6 = (ImageView) findViewById(R.id.risavv6);
					if (risposteutente[i] == Domanda.ESATTA)
						risut6.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteutente[i] == Domanda.SBAGLIATA)
						risut6.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risut6.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					if (risposteavversario[i] == Domanda.ESATTA)
						risavv6.setBackgroundResource(R.drawable.bottone_visualizza_ok);
					else if (risposteavversario[i] == Domanda.SBAGLIATA)
						risavv6.setBackgroundResource(R.drawable.bottone_visualizza_sbagliata);
					else
						risavv6.setBackgroundResource(R.drawable.bottone_visualizza_attesa);
					break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), HomeGiocoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void aggiungiListenerShowDomande(final Partita p) {
		if (p != null && p.getDettagliPartita() != null) {
			if (p.getStatoPartita() > Partita.INIZIATA || p.getDettagliPartita().isUtenteRisposto()) {
				if (!p.hasDomande()) {
					Messaggio m = CommunicationMessageCreator.getInstance().createGetDomande(p.getIDPartita());
					try {
						comm.send(m);
						ArrayList<Domanda> dom = CommunicationParser.getInstance().parseGetDomande(m);
						p.aggiungiDomande(dom);
					}
					catch (IOException | LoginException | ConnectionException e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), R.string.errore_connessione, Toast.LENGTH_LONG).show();
						return;
					}
				}
				ImageView risut1 = (ImageView) findViewById(R.id.risutente1);
				if (risut1 != null)
					risut1.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(0).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv1 = (ImageView) findViewById(R.id.risavv1);
				if (risavv1 != null)
					risavv1.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(0).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});

				ImageView risut2 = (ImageView) findViewById(R.id.risutente2);
				if (risut2 != null)
					risut2.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(1).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv2 = (ImageView) findViewById(R.id.risavv2);
				if (risavv2 != null)
					risavv2.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(1).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});

				ImageView risut3 = (ImageView) findViewById(R.id.risutente3);
				if (risut3 != null)
					risut3.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(2).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv3 = (ImageView) findViewById(R.id.risavv3);
				if (risavv3 != null)
					risavv3.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(2).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});

				ImageView risut4 = (ImageView) findViewById(R.id.risutente4);
				if (risut4 != null)
					risut4.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(3).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv4 = (ImageView) findViewById(R.id.risavv4);
				if (risavv4 != null)
					risavv4.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(3).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});

				ImageView risut5 = (ImageView) findViewById(R.id.risutente5);
				if (risut5 != null)
					risut5.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(4).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv5 = (ImageView) findViewById(R.id.risavv5);
				if (risavv5 != null)
					risavv5.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(4).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});

				ImageView risut6 = (ImageView) findViewById(R.id.risutente6);
				if (risut6 != null)
					risut6.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(5).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
				ImageView risavv6 = (ImageView) findViewById(R.id.risavv6);
				if (risavv6 != null)
					risavv6.setOnClickListener(new ImageView.OnClickListener() {
						public void onClick(View v) {
							new AlertDialog.Builder(VisualizzaPartitaActivity.this).setMessage(p.getDomanda(5).getDomanda()).setCancelable(true).setNegativeButton("OK", null).show();
						}
					});
			}
		}
	}

	private void addListenerAddAmico(final Partita p) {
		final int id_utente = p.getUtenteSfidato().getID();
		Button bottone = (Button) findViewById(R.id.visualizza_add_friend);
		if (id_utente <= 0 || Status.getInstance().isMyFriend(id_utente)) {
			RelativeLayout l = (RelativeLayout) bottone.getParent();
			l.removeView(bottone);
			// bottone.setVisibility(View.INVISIBLE);
			return;
		}
		bottone.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Messaggio m = CommunicationMessageCreator.getInstance().createAggiungiAmico(id_utente);
				try {
					comm.send(m);
					boolean res = CommunicationParser.getInstance().parseAggiungiAmico(m);
					if (res) {
						Status.getInstance().aggiungiAmico(p.getUtenteSfidato());
						Toast.makeText(getApplicationContext(), R.string.amico_aggiunto, Toast.LENGTH_LONG).show();
						// v.setVisibility(View.INVISIBLE);
						RelativeLayout l = (RelativeLayout) v.getParent();
						l.removeView(v);
					}
					else
						Toast.makeText(getApplicationContext(), R.string.amico_non_aggiunto, Toast.LENGTH_LONG).show();
				}
				catch (IOException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				catch (LoginException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				catch (ConnectionException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
	}
}
