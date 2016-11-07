package br.com.compras.app;

import java.util.ArrayList;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.compras.app.adapter.ProductAdapter;
import br.com.compras.app.repositoryImp.ProductSql;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.ISynchronizer;

public class ProductListActivity extends FragmentActivity {
	
	private ProductAdapter productAdapter; 
	private IProduct productSql;
	private ISynchronizer synchronizer;
	private Buy buyClicked;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_view);
		
		productSql = new ProductSql(this);
		synchronizer = new SynchronizerSql(this);
		
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
		ArrayList<Product> list = productSql.listByBuy(buyClicked);
		
		productAdapter = new ProductAdapter(this, list);
		
		ListView listView = (ListView) findViewById(R.id.listViewProduct);
		listView.setAdapter(productAdapter);
		listView.setOnItemClickListener(click());
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	public OnItemClickListener click() {
		return (new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductListActivity.this);
				 
		        // Setting Dialog Title
		        alertDialog.setTitle("Confirm Delete...");
		 
		        // Setting Dialog Message
		        alertDialog.setMessage("Are you sure you want delete this?");
		 
		        // Setting Icon to Dialog
		        alertDialog.setIcon(R.drawable.delete);
		 
		        // Setting Positive "Yes" Button
		        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	Product product = productAdapter.getItem(position);
		            	
		            	if(productSql.delete(product)) {
		            		Gson gson = new Gson();
		        			synchronizer.insert(Synchronizer.reload(TypeStatus.Product, CrudStatus.Delete, gson.toJson(product), null));
		            		load();
		            	}
		            }
		        });
		 
		        // Setting Negative "NO" Button
		        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.cancel();
		            }
		        });
		 
		        // Showing Alert Message
		        alertDialog.show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.product_menu, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				startActivity(new Intent(this, BuyGridActivity.class));	break;
			case R.id.addProduct:
				Intent intent = new Intent(this, ProductActivity.class);
				intent.putExtra("buy_id", buyClicked.getId());
				intent.putExtra("buy_phone", buyClicked.getUser().getUser_phone());
				startActivity(intent);	break;
		}
		finish();
		return true;
	}
	
	public void onClickStatusProduct(View view) {
		Product product = productAdapter.getItem(view.getId());
		
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
	protected void onDestroy() {
		super.onDestroy();
	}
}
