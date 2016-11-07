package br.com.compras.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.google.gson.Gson;

import android.content.Context;
import br.com.compras.app.R;
import br.com.compras.app.repositoryImp.BuySql;
import br.com.compras.app.repositoryImp.ProductSql;
import br.com.compras.app.repositoryImp.ShareSql;
import br.com.compras.app.repositoryImp.UserSql;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.SyncList;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.IUser;

public class ServiceReceive extends TimerTask {
	
	private IBuy buySql;
	private IUser userSql;
	private IShare shareSql;
	private IProduct productSql;
	private Context context;
	
	public ServiceReceive(Context context) {
		this.context = context;
		this.buySql = new BuySql(context);
		this.userSql = new UserSql(context);
		this.shareSql = new ShareSql(context);
		this.productSql = new ProductSql(context);
	}
	
	@Override
	public void run() {
		try {
			List<String> list = new ArrayList<String>();
			
			Gson gson = new Gson();
			User user = SessionRepository.getUser();
			String userJson = gson.toJson(user);
			String json = ServiceHttpUtil.post(this.context.getString(R.string.url_web_service)+"/sync/get", userJson);
			
			if(!json.equals("null") && (json != "")) {
				SyncList syncList = gson.fromJson(json, SyncList.class);
				
				if(syncList.getBuyMap().size() > 0)
					registerBuy(syncList.getBuyMap(), list);
				
				if(syncList.getShareMap().size() > 0)
					registerShare(syncList.getShareMap(), list);
				
				if(syncList.getProductMap().size() > 0)
					registerProduct(syncList.getProductMap(), list);
				
				
				if(list.size() > 0){
					ServiceHttpUtil.post(this.context.getString(R.string.url_web_service)+"/sync/del", gson.toJson(list));
				}
			}
		} catch(Exception e) {
			System.out.println("Erro ao enviar a sincronização, erro: " +e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void registerBuy(Map<String, Buy> map, List<String> list) {
		for(Map.Entry<String, Buy> entry : map.entrySet()) {
			Buy buy = entry.getValue();
			String arr[] = entry.getKey().split("-");
			boolean ret = false;
			
			switch(CrudStatus.from(Integer.parseInt(arr[1]))) {
				case Create:
					registerUser(buy.getUser());
					ret = buySql.insert(buy); break;
				case Update:
					ret = buySql.update(buy); break;
				case Delete:
					ret = buySql.delete(buy); break;
				default:
					break;
			}	
			
			if(ret)	
				list.add(arr[0]);
		}
	}
	
	private void registerShare(Map<String, Share> map, List<String> list) {
		for(Map.Entry<String, Share> entry : map.entrySet()) {
			Share share = entry.getValue();
			
			String arr[] = entry.getKey().split("-");
			boolean ret = false;
			
			switch(CrudStatus.from(Integer.parseInt(arr[1]))) {
				case Create:
					registerUser(share.getUser());
					ret = shareSql.insert(share); break;
				case Delete:
					ret = shareSql.delete(share); break;
				default:
					break;
			}
			
			if(ret)	
				list.add(arr[0]);
		}
	}
	
	private void registerProduct(Map<String, Product> map, List<String> list) {
		for(Map.Entry<String, Product> entry : map.entrySet()) {
			Product product = entry.getValue();
			String arr[] = entry.getKey().split("-");
			boolean ret = false;
			
			switch(CrudStatus.from(Integer.parseInt(arr[1]))) {
				case Create:
					ret = productSql.insert(product); break;
				case Delete:
					ret = productSql.delete(product); break;
				default:
					break;
			}	
			
			if(ret)	
				list.add(arr[0]);
		}
	}
	
	private boolean registerUser(User user){
		User user1 = userSql.findById(user.getUser_phone());
		if(user1 != null){
			return true;
		}else{
			if(userSql.insert(user) != null)
				return true;
		}
		return false;
	}
}
