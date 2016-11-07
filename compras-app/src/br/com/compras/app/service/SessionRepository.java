package br.com.compras.app.service;

import br.com.mariel.compras.domain.User;

public final class SessionRepository {

    static User user;

    public SessionRepository(User user) {
        SessionRepository.user = user;
    }

    public static User getUser() {
        return user;
    }
}
