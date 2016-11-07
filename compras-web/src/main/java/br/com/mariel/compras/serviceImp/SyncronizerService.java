package br.com.mariel.compras.serviceImp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.SyncList;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.service.IBuyService;
import br.com.mariel.compras.service.IProductService;
import br.com.mariel.compras.service.IShareService;
import br.com.mariel.compras.service.ISyncronizerService;

@Service
public class SyncronizerService implements ISyncronizerService {
	
	@Autowired private ISynchronizer syncSql;
	@Autowired private IProduct productSql;
	@Autowired private IBuy buySql;
	@Autowired private IShare shareSql;
	
	@Autowired private IBuyService buyService;
	@Autowired private IShareService shareService;
	@Autowired private IProductService productService;
	
	@Override
	public SyncList listProductToSync(User user) {
		ArrayList<Synchronizer> listToSync = syncSql.listToSync(user);
		
		if(listToSync != null && listToSync.size() > 0) 
			return fillListToSync(listToSync);
			
		return null;
	}

	private SyncList fillListToSync(ArrayList<Synchronizer> listToSync) {
		SyncList syncList = new SyncList();
		Gson gson = new Gson();
		
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
					Share s = gson.fromJson(sync.getItem_object(), Share.class);
					
					Buy b = s.getBuy();
						b.setUser(s.getBuyUser());
					
					ArrayList<Share> list = shareSql.list(b);
					
					for(Share share: list) {
						syncList.getShareMap().put(key, share);
					}
					break;
				default:
					break;
			}
		}
		return syncList;
	}

	@Override
	public boolean delete(String synchronize_id) {
		return syncSql.delete(synchronize_id);
	}

	@Override
	public List<String> listToRegister(SyncList syncList) {
		
		List<String> list = new ArrayList<String>();
		
		if(!syncList.getBuyMap().isEmpty())
			buyService.register(syncList.getBuyMap(), list);
		
		if(!syncList.getProductMap().isEmpty())
			productService.register(syncList.getProductMap(), list);
		
		if(!syncList.getShareMap().isEmpty())
			shareService.register(syncList.getShareMap(), list);
		
		return list;
	}
}