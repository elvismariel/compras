package br.com.mariel.compras.repository;

import br.com.mariel.compras.domain.User;

public interface IUser {
	User findByIdToLogin();
	User findById(String phone);
	User insert(User buy);
}
