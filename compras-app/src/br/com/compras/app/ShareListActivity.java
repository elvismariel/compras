package br.com.compras.app;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import br.com.compras.app.adapter.ShareAdapter;
import br.com.compras.app.repositoryImp.ShareSql;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IShare;

public class ShareListActivity extends FragmentActivity {
 
	private IShare shareSql;
	private Buy buyClicked;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_view);
		shareSql = new ShareSql(this);
		
		Intent intent = getIntent();
		
		if(intent != null) {
			Bundle param = intent.getExtras();
			if(param != null) {
				buyClicked = Buy.reload(param.getLong("buy_id"), User.reload(param.getString("buy_phone")));
				load();
			}
		}
	}

	private void load() {
		ArrayList<Share> list = shareSql.list(buyClicked);
		
		if(list != null) {
			ListView listView = (ListView) findViewById(R.id.listViewShare);
			listView.setAdapter(new ShareAdapter(this, list));
		}
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.share_menu, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				startActivity(new Intent(this, ItemListActivity.class));	break;
			case R.id.addShare:
				Intent intent = new Intent(this, ShareActivity.class);
					intent.putExtra("buy_id", buyClicked.getId());
					intent.putExtra("buy_phone", buyClicked.getUser().getUser_phone());
    			
				startActivity(intent);	break;
		}
		finish();
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
