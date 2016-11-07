package br.com.compras.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.compras.app.R;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.enumeration.BuyStatus;

public class ProductAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Product> listProduct;

	public ProductAdapter(Context context, ArrayList<Product> listProduct){
		this.context = context;
		this.listProduct = listProduct;
	}
	
	@Override
	public int getCount() {
		return listProduct.size();
	}

	@Override
	public Product getItem(int position) {
		return listProduct.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Product product = getItem(position);
		
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View view = inflate.inflate(R.layout.product_list, null);
		
		TextView productName = (TextView) view.findViewById(R.id.productName);
		productName.setText(product.getName());

		TextView productPrice = (TextView) view.findViewById(R.id.productPrice);
		productPrice.setText("Preço: R$ 2,45");
		
		ImageView productImage = (ImageView) view.findViewById(R.id.productImage);
		productImage.setImageResource(product.getStatus().equals(BuyStatus.Buy) ? R.drawable.ok_gray : R.drawable.ok_green);
		productImage.setId(position);
	
		return view;
	}
}
