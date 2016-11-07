package br.com.compras.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.ISynchronizer;

public class BuyActivity extends Activity {
	
	private EditText buyNameText;
	private IBuy buySql;
	private ISynchronizer synchronizer;
	private Buy buyClicked;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_cad);
		
		buySql = new BuySql(this);
		synchronizer = new SynchronizerSql(this);
		buyNameText = (EditText) findViewById(R.id.buyNameText);
		
		Intent intent = getIntent();
		
		if(intent != null) {
			Bundle param = intent.getExtras();
			if(param != null) {
				buyClicked = Buy.reload(param.getLong("buy_id"), User.reload(param.getString("buy_phone")));
				buyNameText.setText(buyClicked.getName());
			}
		}

		// Botão home do aplicativo
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			startActivity(new Intent(this, BuyGridActivity.class));
			finish();
		}
		return true;
	}
	
	public void saveBuy(View view) {
		Gson gson = new Gson();
		int status;
		
		if(buyClicked != null) {
			buyClicked.setName(buyNameText.getText().toString());
			status = CrudStatus.Update.getCode();
		}else{
			buyClicked = Buy.reload(0, buyNameText.getText().toString(), BuyStatus.Buy, SessionRepository.getUser());
			status = CrudStatus.Create.getCode();
		}
		
		if(buySql.insert(buyClicked)) {
			Synchronizer reload = Synchronizer.reload(TypeStatus.Buy, CrudStatus.from(status), gson.toJson(buyClicked), null);;
			synchronizer.insert(reload);
			
			startActivity(new Intent(this, BuyGridActivity.class));
			finish();
		}
		else{
			String mensagemErro = getString(R.string.erro_save_buy);
    		Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
    		toast.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}