package br.com.mariel.compras.service;

import java.util.List;
import java.util.Map;

import br.com.mariel.compras.domain.Product;

public interface IProductService {
	void register(Map<String, Product> map, List<String> list);
}
