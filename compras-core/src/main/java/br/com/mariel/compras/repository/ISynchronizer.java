package br.com.mariel.compras.repository;

import java.util.ArrayList;

import br.com.mariel.compras.domain.Synchronizer;
import br.com.mariel.compras.domain.User;

public interface ISynchronizer {
	boolean insert(Synchronizer synchronize);
	boolean delete(String id);
	ArrayList<Synchronizer> listToSync(User user);
}
