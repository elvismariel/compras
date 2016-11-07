package br.com.compras.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.compras.app.ProductListActivity;
import br.com.compras.app.R;
import br.com.mariel.compras.domain.Buy;

public class BuyGridAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Buy> listBuy;
	private LayoutInflater mInflater;

	public BuyGridAdapter(Context context, ArrayList<Buy> listBuy){
		this.context = context;
		this.listBuy = listBuy;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		final Buy buy = getItem(position);

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_grid, null);

			holder = new ViewHolder();
			convertView.setTag(holder);

			holder.imageview = (ImageView) convertView.findViewById(R.id.imageViewGrid);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBoxGrid);
			holder.textView = (TextView) convertView.findViewById(R.id.textViewGrid);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.id = position;

		holder.checkbox.setChecked(false);
		holder.checkbox.setId((int) buy.getId());

		holder.textView.setText(buy.getName());
		holder.imageview.setId((int) buy.getId());
		holder.imageview.setImageResource(R.drawable.shopping);

		holder.imageview.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ProductListActivity.class);
				intent.putExtra("buy_id", buy.getId());
				intent.putExtra("buy_phone", buy.getUser().getUser_phone());
				v.getContext().startActivity(intent);
			}
		});

		return convertView;
	}

	class ViewHolder {
		int id;
		ImageView imageview;
		CheckBox checkbox;
		TextView textView;
	}
}
