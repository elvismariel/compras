package br.com.compras.app;

import java.util.ArrayList;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.compras.app.adapter.BuyAdapter;
import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.ISynchronizer;

public class BuyListActivity extends FragmentActivity {

	private IBuy buySql;
	private IProduct productSql;
	private boolean status = false;
	private BuyAdapter buyAdapter;
	private ISynchronizer synchronizer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_view);

		buySql = new BuySql(this);
		synchronizer = new SynchronizerSql(this);

		load();
	}

	private void load() {
		ArrayList<Buy> list = buySql.list();

		buyAdapter = new BuyAdapter(this, list, status);

		ListView listView = (ListView) findViewById(R.id.listViewBuy);
		listView.setAdapter(buyAdapter);
		listView.setOnItemClickListener(click());

		// Botão home do aplicativo
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle(R.string.buyListLabel);
	}

	public OnItemClickListener click() {
		return (new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Buy buy = buyAdapter.getItem(position);

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(BuyListActivity.this);

				if (buy.getUser().getUser_phone().equals(SessionRepository.getUser().getUser_phone())) {
					alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getBaseContext(), BuyActivity.class);
								intent.putExtra("buy_id", buy.getId());
								intent.putExtra("buy_phone", buy.getUser().getUser_phone());

							startActivity(intent);
						}
					});
					alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (productSql.deleteAllProductOfBuy(buy)) {
								if (buySql.delete(buy)) {
									Gson gson = new Gson();
									Share share = Share.reload(buy, buy.getUser(), SessionRepository.getUser());

									Synchronizer sync = Synchronizer.reload(TypeStatus.Share, CrudStatus.Delete,
											gson.toJson(share), null);
									synchronizer.insert(sync);
								}
							}
							load();
						}
					});
					alertDialog.setNeutralButton("Share", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getBaseContext(), ShareListActivity.class);
								intent.putExtra("buy_id", buy.getId());
								intent.putExtra("buy_phone", buy.getUser().getUser_phone());

							startActivity(intent);
						}
					});
				} else {
					alertDialog.setNeutralButton("Exit of Group", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (productSql.deleteAllProductOfBuy(buy)) {
								if (buySql.delete(buy)) {
									Gson gson = new Gson();
									Share share = Share.reload(buy, buy.getUser(), SessionRepository.getUser());

									Synchronizer sync = Synchronizer.reload(TypeStatus.Share, CrudStatus.Delete,
											gson.toJson(share), null);
									synchronizer.insert(sync);
								}
							}
							load();
						}
					});
				}

				alertDialog.show();
			}
		});
	}

	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, ItemListActivity.class));
			finish();
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}