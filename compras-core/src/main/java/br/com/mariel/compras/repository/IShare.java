package br.com.mariel.compras.repository;

import java.util.ArrayList;

import br.com.mariel.compras.domain.Buy;
import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;

public interface IShare {
	boolean insert(Share share);
	boolean delete(Share share);
	ArrayList<Share> list(Buy buy);
	ArrayList<Share> list(User user);
}
