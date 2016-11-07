package br.com.compras.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import br.com.compras.app.adapter.ExpandableAdapter;
import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.ProductSql;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.compras.app.repositoryImp.UserSql;
import br.com.compras.app.service.ServiceReceive;
import br.com.compras.app.service.ServiceSend;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.repository.IUser;

public class ItemListActivity extends Activity {

	private IBuy buySql;
	private IUser userSql;
	private IProduct productSql;
	private ISynchronizer synchronizer;
	private ExpandableAdapter expandableAdapter;
	private ArrayList<Buy> listBuy;
	private HashMap<Buy, ArrayList<Product>> mapListProduct;
	private int expandGroupIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_list);

		buySql = new BuySql(this);
		userSql = new UserSql(this);
		productSql = new ProductSql(this);
		synchronizer = new SynchronizerSql(this);

		// Botão home do aplicativo
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		User user = userSql.findByIdToLogin();

		// Se usuário já existe
		if (user != null) {
			new SessionRepository(user);

			// Grupo selecionado
			Intent intent = getIntent();
			if (intent != null) {
				Bundle param = intent.getExtras();
				if (param != null) {
					expandGroupIndex = param.getInt("expandGroupIndex", 0);
				}
			}

			// Carrega a lista
			load();
		} else {
			Intent intent = new Intent(getBaseContext(), LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void load(){
		listBuy = buySql.list();
		mapListProduct = new HashMap<Buy, ArrayList<Product>>();

		if(listBuy != null && listBuy.size() > 0){
			for(Buy buy : listBuy){
				ArrayList<Product> listProduct = productSql.listByBuy(buy);
				mapListProduct.put(buy, listProduct);
			}

			expandableAdapter = new ExpandableAdapter(ItemListActivity.this, listBuy, mapListProduct);

			ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.explandableListView);
			expandableListView.setAdapter(expandableAdapter);
			expandableListView.expandGroup(expandGroupIndex); // Expandir pelo número do grupo
			expandableListView.setOnChildClickListener(clickChildListener());
			expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					expandGroupIndex = groupPosition;
					return false;
				}
			});
		}
		else{
			Toast toast = Toast.makeText(this, R.string.itemListEmpty, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private OnChildClickListener clickChildListener(){
		return new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItemListActivity.this);
				final Product product = (Product) expandableAdapter.getChild(groupPosition, childPosition);

				alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Bundle param = new Bundle();
						param.putLong("product_id", product.getId());
						param.putString("product_nm", product.getName());
						param.putInt("department", product.getDepartment());
						param.putLong("buy_id", product.getBuy().getId());
						param.putString("buy_phone", product.getUser().getUser_phone());
						param.putInt("expandGroupIndex", groupPosition);

						Intent intent = new Intent(getBaseContext(), ProductActivity.class);
						intent.putExtras(param);
						startActivity(intent);
						finish();
					}
				});

				alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (productSql.delete(product)) {
							Gson gson = new Gson();
							synchronizer.insert(Synchronizer.reload(TypeStatus.Product, CrudStatus.Delete, gson.toJson(product), null));
							load();
						}
					}
				});

				alertDialog.show();
				return false;
			}
		};
	}

	public void addProduct(View view) {
		Buy buy = (Buy) view.getTag();

		Bundle param = new Bundle();
		param.putLong("buy_id", buy.getId());
		param.putString("buy_phone", buy.getUser().getUser_phone());
		param.putInt("expandGroupIndex", expandGroupIndex);

		Intent intent = new Intent(getBaseContext(), ProductActivity.class);
		intent.putExtras(param);
		startActivity(intent);
		finish();
	}

	public void onClickStatusProduct(View view) {
		Product product = (Product) view.getTag();

		if(product.getStatus().equals(BuyStatus.Buy))
			product.setStatus(BuyStatus.Bought);
		else
			product.setStatus(BuyStatus.Buy);

		if(productSql.update(product)) {
			Gson gson = new Gson();
			synchronizer.insert(Synchronizer.reload(TypeStatus.Product, CrudStatus.Update, gson.toJson(product), null));
		}

		load();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.item_list_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(this, ItemListActivity.class));
				finish();
				break;
			case R.id.addBuy:
				startActivity(new Intent(this, BuyActivity.class));
				finish();
				break;
			case R.id.sync:
				new Sincronizar().execute("");	break;
			/*case R.id.showBuy:
				startActivity(new Intent(this, BuyListActivity.class));
				break;
			*/
		}
		return true;
	}

	// Classe interna/aninhada
	class Sincronizar extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			new ServiceSend(getBaseContext()).run();
			new ServiceReceive(getBaseContext()).run();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if("Sucess".equals(result)) {
				startActivity(new Intent(getBaseContext(), BuyGridActivity.class));
				finish();
			}else{
				Toast toast = Toast.makeText(getBaseContext(), R.string.erro_register_login, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}
}
