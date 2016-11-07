package br.com.compras.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.com.compras.app.R;
import br.com.mariel.compras.domain.Share;

public class ShareAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Share> listShare;

	public ShareAdapter(Context context, ArrayList<Share> listShare){
		this.context = context;
		this.listShare = listShare;
	}
	
	@Override
	public int getCount() {
		return listShare.size();
	}

	@Override
	public Share getItem(int position) {
		return listShare.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(getItem(position).getUser().getUser_phone());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Share share = getItem(position);
		
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View view = inflate.inflate(R.layout.share_list, null);
		
		TextView productName = (TextView) view.findViewById(R.id.shareName);
		productName.setText(share.getUser().getUser_name());
	
		return view;
	}
}
