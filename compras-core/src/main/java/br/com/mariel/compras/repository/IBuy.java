package br.com.mariel.compras.repository;

import java.util.ArrayList;

import br.com.mariel.compras.domain.Buy;

public interface IBuy {
	Buy findById(Buy buy);
	boolean insert(Buy buy);
	boolean update(Buy buy);
	boolean delete(Buy buy);
	ArrayList<Buy> list();
}