package br.com.compras.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.compras.app.R;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.enumeration.BuyStatus;

public class ExpandableAdapter extends BaseExpandableListAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<Buy> listBuy;
	private HashMap<Buy, ArrayList<Product>> mapListProduct;
	
	public ExpandableAdapter(Context context, ArrayList<Buy> listBuy, HashMap<Buy, ArrayList<Product>> mapListProduct){
		this.listBuy = listBuy;
		this.mapListProduct = mapListProduct;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getGroupCount() {
		return listBuy.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Buy buy = (Buy) getGroup(groupPosition);
		return mapListProduct.get(buy).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listBuy.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Buy buy = (Buy) getGroup(groupPosition);
		return mapListProduct.get(buy).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return listBuy.get(groupPosition).getId();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Buy buy = (Buy) getGroup(groupPosition);
		return mapListProduct.get(buy).get(childPosition).getId();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolderBuy holder;
		
		Buy buy = (Buy) getGroup(groupPosition);
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_buy_list, null);
			holder = new ViewHolderBuy();
			convertView.setTag(holder);
			
			holder.buyName = (TextView) convertView.findViewById(R.id.buyName);
			holder.addProduct = (ImageView) convertView.findViewById(R.id.addProduct);
		}else{
			holder = (ViewHolderBuy) convertView.getTag();
		}
		
		holder.buyName.setText(buy.getName());
		holder.addProduct.setTag(buy);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolderProduct holder;
		
		Product product = (Product) getChild(groupPosition, childPosition);
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_product_list, null);
			holder = new ViewHolderProduct();
			convertView.setTag(holder);
			
			holder.productName = (TextView) convertView.findViewById(R.id.productName);
			holder.productImage = (ImageView) convertView.findViewById(R.id.productImage);
		}else{
			holder = (ViewHolderProduct) convertView.getTag();
		}
		
		holder.productName.setText(product.getName());
	
		holder.productImage.setImageResource(product.getStatus().equals(BuyStatus.Buy) ? R.drawable.ok_gray : R.drawable.ok_green);
		holder.productImage.setTag(product);
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	class ViewHolderBuy {
		TextView buyName;
		ImageView addProduct;
	}
	
	class ViewHolderProduct {
		TextView productName;
		ImageView productImage;
	}
}
