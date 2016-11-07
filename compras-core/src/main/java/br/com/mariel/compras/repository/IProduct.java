package br.com.mariel.compras.repository;

import java.util.ArrayList;

import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Product;

public interface IProduct {
	Product findById(Product product);
	ArrayList<Product> listByBuy(Buy buy);
	
	boolean delete(Product product);
	boolean deleteAllProductOfBuy(Buy buy);
	boolean insert(Product product);
	boolean update(Product product);	
}
