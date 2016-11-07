package br.com.mariel.compras.service;

import java.util.List;
import java.util.Map;

import br.com.mariel.compras.domain.Buy;

public interface IBuyService {
	void register(Map<String, Buy> map, List<String> list);
}
