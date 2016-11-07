package br.com.mariel.compras.serviceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mariel.compras.domain.Product;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;
import br.com.mariel.compras.repository.IProduct;
import br.com.mariel.compras.repository.IShare;
import br.com.mariel.compras.repository.ISynchronizer;
import br.com.mariel.compras.service.IProductService;
import br.com.mariel.compras.service.ISyncronizerService;

@Service
public class ProductService implements IProductService {
	
	@Autowired private ISyncronizerService sync;
	@Autowired private IProduct productSql;
	@Autowired private IShare shareSql;
	@Autowired private ISynchronizer syncSql;
	
	@Override
	public void register(Map<String, Product> map, List<String> list) {
		
		for(Map.Entry<String, Product> entry : map.entrySet()) {
			Product product = entry.getValue();
			String arr[] = entry.getKey().split("-");
			boolean ret = false;
			
			switch(CrudStatus.from(Integer.parseInt(arr[1]))) {
				case Create:
					ret = insertProduct(product); break;
				case Delete:
					ret = deleteProduct(product); break;
				case Update:
					ret = updateProduct(product); break;
				default:
					break;
			}	
			
			if(ret)	
				list.add(arr[0]);
		}
	}

	private boolean insertProduct(Product product) {
		if(productSql.insert(product)) {
			if(registerToSync(product, CrudStatus.Create))
				return true;
			else
				productSql.delete(product);
		}
		return false;
	}
	
	private boolean deleteProduct(Product product) {
		if(productSql.delete(product))
			return registerToSync(product, CrudStatus.Delete);
		return false;
	}
	
	private boolean updateProduct(Product product) {
		if(productSql.update(product)) {
			return registerToSync(product, CrudStatus.Update);
		}
		return false;
	}
	
	private boolean registerToSync(Product product, CrudStatus status) {
		boolean ret = true;
		ArrayList<Share> listShare = shareSql.list(product.getBuy());
		Gson gson = new Gson();
		
		if(listShare!= null && listShare.size() > 0) {
			for(Share share : listShare) {
				if(!product.getUser().getUser_phone().equals(share.getUser().getUser_phone())) {
					Synchronizer synchronize = Synchronizer.reload(TypeStatus.Product, status, gson.toJson(product), share.getUser());
				
					if(!syncSql.insert(synchronize))
						ret = false; break;
				}
			}
		}
		return ret;
	}
}
