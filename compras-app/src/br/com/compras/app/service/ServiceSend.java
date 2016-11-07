package br.com.compras.app.service;

import java.util.ArrayList;
import java.util.TimerTask;

import com.google.gson.Gson;

import android.content.Context;
import br.com.compras.app.R;
import br.com.compras.app.repositoryImp.SynchronizerSql;
import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.SyncList;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.repository.ISynchronizer;

public class ServiceSend extends TimerTask {
	
	private Context context;
	private ISynchronizer syncSql = null;
	private Gson gson;
	
	public ServiceSend(Context context){
		this.context = context;
		syncSql = new SynchronizerSql(context);
		gson = new Gson();
	}
	
	public void run() {
		try {
			if (WiFiCommunicationHelper.isNetworkAvailable(context)) {
				ArrayList<Synchronizer> listToSync = syncSql.listToSync(SessionRepository.getUser());
				
				if(listToSync.size() > 0) {
					SyncList syncList = fillListToSync(listToSync);
					
					String json = gson.toJson(syncList);
					
					String returnJson = ServiceHttpUtil.post(this.context.getString(R.string.url_web_service)+"/sync/put", json);
					
					ArrayList<String> fromJson = gson.fromJson(returnJson, ArrayList.class);
					
					if(!fromJson.isEmpty()) {
						for(String id : fromJson){
							syncSql.delete(id);
						}
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Erro ao enviar a sincronização, erro: " +e.getMessage());
			e.printStackTrace();
		}
	}
	
	private SyncList fillListToSync(ArrayList<Synchronizer> listToSync) {
		SyncList syncList = new SyncList();
		
		for(Synchronizer sync : listToSync) {
			String key = sync.getId().concat("-"+sync.getItem_action().getCode());

			switch(sync.getItem_type()){
				case Buy:
					Buy buy = gson.fromJson(sync.getItem_object(), Buy.class);
					syncList.getBuyMap().put(key, buy);
					break;
				case Product:
					Product product = gson.fromJson(sync.getItem_object(), Product.class);
					syncList.getProductMap().put(key, product);
					break;
				case Share:
					Share share = gson.fromJson(sync.getItem_object(), Share.class);
					syncList.getShareMap().put(key, share);
				default:
					break;
			}
		}
		return syncList;
	}
}
