package br.com.mariel.compras.service;

import java.util.List;

import br.com.mariel.compras.domain.SyncList;
import br.com.mariel.compras.domain.User;

public interface ISyncronizerService {
	List<String> listToRegister(SyncList syncList);
	SyncList listProductToSync(User user);
	boolean delete(String synchronize_id);
}
