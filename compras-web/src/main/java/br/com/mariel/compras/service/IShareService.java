package br.com.mariel.compras.service;

import java.util.List;
import java.util.Map;

import br.com.mariel.compras.domain.Share;
import br.com.mariel.compras.domain.User;

public interface IShareService {
	boolean share(Share share);
	boolean unshare(Share share);
	boolean restoreUserDada(User user);
	
	void register(Map<String, Share> map, List<String> list);
}
