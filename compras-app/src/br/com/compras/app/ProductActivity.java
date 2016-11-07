package br.com.compras.app;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.compras.app.repositoryImp.ProductSql;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.compras.app.service.SessionRepository;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.BuyStatus;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.ISynchronizer;

public class ProductActivity extends Activity {
	
	private EditText productNameText;
	private IProduct productSql;
	private ISynchronizer synchronizer;
	private Buy buyClicked;
	private Spinner productDepartment;
	private Product product = null;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_cad);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_spinner_dropdown_item);

		productSql = new ProductSql(this);
		synchronizer = new SynchronizerSql(this);

		productNameText = (EditText) findViewById(R.id.productNameText);
		productDepartment = (Spinner) findViewById(R.id.productDepartment);
		productDepartment.setAdapter(adapter);

		Intent intent = getIntent();

		if(intent != null) {
			Bundle param = intent.getExtras();
			if(param != null) {
				buyClicked = Buy.reload(param.getLong("buy_id"), User.reload(param.getString("buy_phone")));

				if(getIntent().getLongExtra("product_id", 0) > 0) {
					product = Product.reload(param.getLong("product_id"));
					product.setName(param.getString("product_nm"));
					product.setDepartment(param.getInt("department"));

					productNameText.setText(product.getName());
					productDepartment.setSelection(product.getDepartment());
				}
			}
		}

		// Botão home do aplicativo
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onMenuItemSelected(int panel, MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, BuyGridActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
	}

	public void saveProduct(View view) {
		int selectedItemPosition = productDepartment.getSelectedItemPosition();

		if(selectedItemPosition > 0) {
			Product prod = Product.reload((product != null ? product.getId() : 0),
					productNameText.getText().toString(), BuyStatus.Buy, buyClicked, SessionRepository.getUser(), selectedItemPosition);

			if(productSql.insert(prod)) {
				Gson gson = new Gson();
				synchronizer.insert(Synchronizer.reload(TypeStatus.Product, CrudStatus.Create, gson.toJson(prod), null));

				Intent intent = new Intent(this, ProductListActivity.class);
				intent.putExtra("buy_id", buyClicked.getId());
				intent.putExtra("buy_phone", buyClicked.getUser().getUser_phone());
				startActivity(intent);
				finish();
			}
			else{
				String mensagemErro = getString(R.string.erro_save_product);
	    		Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
	    		toast.show();
			}
		}else{
			String mensagemErro = getString(R.string.department_validation);
    		Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
    		toast.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}