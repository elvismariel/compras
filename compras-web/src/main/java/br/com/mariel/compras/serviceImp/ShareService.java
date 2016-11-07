package br.com.mariel.compras.serviceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.service.IShareService;

import com.google.gson.Gson;

@Service
public class ShareService implements IShareService {
	
	@Autowired private IBuy buySql;
	@Autowired private IShare shareSql;
	@Autowired private IProduct productSql;
	@Autowired private ISynchronizer sync;
	
	@Override
	public void register(Map<String, Share> map, List<String> list) {
		for(Map.Entry<String, Share> entry : map.entrySet()) {
			Share share = entry.getValue();
			String arr[] = entry.getKey().split("-");
			
			if(unshare(share))	
				list.add(arr[0]);
		}
	}
	
	@Override
	public boolean share(Share share) {
		if(shareSql.insert(share)) {
			if(shareBuy(share)){
				if(shareProduct(share)) {
					return true;
				}
			}else
				shareSql.delete(share);
		}
        return false;
	}

	@Override
	public boolean unshare(Share share) {
		if(shareSql.delete(share)) {
			ArrayList<Share> listShare = shareSql.list(share.getBuy());
			if(listShare == null || listShare.size() == 0) {
				if(productSql.deleteAllProductOfBuy(share.getBuy()))
					return buySql.delete(share.getBuy());
				else
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean restoreUserDada(User user) {
		ArrayList<Share> list = shareSql.list(user); // Todas as compras que o usuário está associado
		if(list.size() > 0) {
			for(Share share : list) {
				if(!shareBuy(share)) // Registra compra para sincronizar
					return false;
				
				if(!shareSync(share)) // Registra compartilhamentos associados
					return false;
				
				if(!shareProduct(share)) // Registra produto para sincronizar
					return false;
			}
		}
		return true;
	}
	
	private boolean shareSync(Share share) {
		Gson gson = new Gson();
		Synchronizer s = Synchronizer.reload(TypeStatus.Share, CrudStatus.Create, gson.toJson(share), share.getUser());
		
		return sync.insert(s);
	}
	
	private boolean shareBuy(Share share) {
		Gson gson = new Gson();
		Synchronizer synchronizer = Synchronizer.reload(TypeStatus.Buy, CrudStatus.Create, gson.toJson(share.getBuy()), share.getUser());
		
		return sync.insert(synchronizer);
	}
	
	private boolean shareProduct(Share share) {
		boolean ret = true;
		Gson gson = new Gson();
		ArrayList<Product> listByBuyId = productSql.listByBuy(share.getBuy());
		
		if(listByBuyId.size() > 0) {
			for(Product product : listByBuyId) {
				Synchronizer synchronizer = Synchronizer.reload(TypeStatus.Product, CrudStatus.Create, gson.toJson(product), share.getUser());
				
				sync.insert(synchronizer);
			}
		}
		return ret;
	}
}