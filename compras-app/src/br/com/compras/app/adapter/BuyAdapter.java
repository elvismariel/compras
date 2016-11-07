package br.com.compras.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.compras.app.R;
import br.com.mariel.compras.domain.Buy;

public class BuyAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Buy> listBuy;
	private boolean status;
	
	public BuyAdapter(Context context, ArrayList<Buy> listBuy, boolean status){
		this.context = context;
		this.listBuy = listBuy;
		this.status = status;
	}

	@Override
	public int getCount() {
		return listBuy.size();
	}

	@Override
	public Buy getItem(int position) {
		return listBuy.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Buy buy = getItem(position);
		
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View view = inflate.inflate(R.layout.buy_list, null);
		
		TextView buyName = (TextView) view.findViewById(R.id.buyName);
		buyName.setText(buy.getName());
		
		return view;
	}
}
