package br.com.mariel.compras.serviceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IBuy;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.service.IBuyService;

@Service
public class BuyService implements IBuyService {
	
	@Autowired private IBuy buySql;
	@Autowired private IShare shareSql;
	@Autowired private ISynchronizer syncSql;
	
	@Override
	public void register(Map<String, Buy> map, List<String> list) {
		for(Map.Entry<String, Buy> entry : map.entrySet()) {
			Buy buy = entry.getValue();
			String arr[] = entry.getKey().split("-");
			boolean ret = false;
			
			switch(CrudStatus.from(Integer.parseInt(arr[1]))) {
				case Create:
					ret = addAndShare(buy); break;
				case Update:
					ret = updateAndAddToSync(buy); break;
				default:
					break;
			}	
			
			if(ret)	
				list.add(arr[0]);
		}
	}
	
	private boolean addAndShare(Buy buy){
		if(buySql.insert(buy)){
			Share share= Share.reload(buy, buy.getUser(), buy.getUser());
			if(shareSql.insert(share))
				return true;
			else
				buySql.delete(buy);
		}
		return false;
	}
	
	private boolean updateAndAddToSync(Buy buy) {
		if(buySql.update(buy)) {
			ArrayList<Share> listShare = shareSql.list(buy);
			Gson gson = new Gson();
			
			if(listShare!= null && listShare.size() > 0) {
				for(Share share : listShare) {
					if(!buy.getUser().getUser_phone().equals(share.getUser().getUser_phone())) {
						Synchronizer synchronize = Synchronizer.reload(TypeStatus.Buy, CrudStatus.Update, gson.toJson(buy), share.getUser());
					
						if(!syncSql.insert(synchronize))
							return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}
