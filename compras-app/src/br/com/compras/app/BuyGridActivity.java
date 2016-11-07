package br.com.compras.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.compras.app.adapter.BuyGridAdapter;
import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.UserSql;
import br.com.compras.app.service.ServiceReceive;
import br.com.compras.app.service.ServiceSend;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IUser;

public class BuyGridActivity extends Activity {

	private IBuy buySql;
	private IUser userSql;
	private BuyGridAdapter buyGridAdapter;
	private boolean status = false;
	private GridView gridView;
	private Button btnGridView;
	private ArrayList<Buy> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_grid);

		buySql = new BuySql(this);
		userSql = new UserSql(this);

		login();

		// Botão home do aplicativo
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

	private void login(){
		User user = userSql.findByIdToLogin();

		// Se usuário já existe
		if (user != null) {
			new SessionRepository(user);
			// Carrega a lista
			load();
		} else {
			Intent intent = new Intent(getBaseContext(), LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void load() {
		list = buySql.list();

		buyGridAdapter = new BuyGridAdapter(this, list);

		gridView = (GridView) findViewById(R.id.buyGridView);
		gridView.setAdapter(buyGridAdapter);
		btnGridView = (Button) findViewById(R.id.buttonGridView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			case R.id.addBuy:
				startActivity(new Intent(this, BuyActivity.class));
				finish();
				break;
			case R.id.delBuy:
				showCheckBox();
				btnGridView.setText("Excluir");
				btnGridView.setTag(TypeStatus.Delete);
				break;
			case R.id.shareBuy:
				showCheckBox();
				btnGridView.setText("Compartilhar");
				btnGridView.setTag(TypeStatus.Share);
				break;
			case R.id.sync:
				new Sincronizar().execute("");	break;
			default:
				startActivity(new Intent(this, BuyGridActivity.class));
				finish();
				break;
		}
		return true;
	}

	public void onClickButtonGrid(View view){
		TypeStatus type = (TypeStatus)btnGridView.getTag();
		switch (type){
			case Delete:
				deleteBuysSelected(); break;
			case Share:
				shareBuys(); break;
		}
	}

	private void shareBuys(){
		List<Buy> listBuy = new ArrayList<>();

		for(CheckBox cb : getCheckBoxList()){
			for(Buy buy : list){
				if(cb.getId() == buy.getId())
					listBuy.add(buy);
			}
		}

		if(listBuy.size() > 0){
			Gson gson = new Gson();
			String buys = gson.toJson(listBuy, ArrayList.class);

			Intent intent = new Intent(getBaseContext(), ContactActivity.class);
			intent.putExtra("buys", buys);
			startActivity(intent);
			finish();
		}
	}

	private void showCheckBox(){
		btnGridView.setVisibility(View.VISIBLE);

		for(CheckBox cb : getCheckBoxList()){
			cb.setVisibility(View.VISIBLE);
		}
	}

	private void deleteBuysSelected(){
		for(CheckBox cb : getCheckBoxList()){
			if(cb.isChecked()){
				Buy buy = Buy.reload(cb.getId());
				buySql.delete(buy);
			}
		}
		startActivity(new Intent(this, BuyGridActivity.class));
		finish();
	}

	private List<CheckBox> getCheckBoxList(){
		List<CheckBox> list = new ArrayList<>();
		for(int i = 0; i < gridView.getChildCount(); i++) {
			ViewGroup gridChild = (ViewGroup) gridView.getChildAt(i);
			for(int k = 0; k < gridChild.getChildCount(); k++) {
				if( gridChild.getChildAt(k) instanceof CheckBox ) {
					CheckBox cb = (CheckBox) gridChild.getChildAt(k);
					list.add(cb);
				}
			}
		}
		return list;
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
			if(result) {
				startActivity(new Intent(getBaseContext(), BuyGridActivity.class));
				finish();
			}else{
				Toast toast = Toast.makeText(getBaseContext(), R.string.erro_share_buys, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}
}