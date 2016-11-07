package br.com.mariel.compras.domain;

import br.com.mariel.compras.enumeration.BuyStatus;

public class Buy {
	private long id;
	private String name;
	private BuyStatus status;
	private User user;

	private Buy(long id, String name, BuyStatus status, User user) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.user = user;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public BuyStatus getStatus() {
		return status;
	}
	public void setStatus(BuyStatus status) {
		this.status = status;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public static Buy reload(long id) {
		return reload(id, null, null, null);
	}
	
	public static Buy reload(long id, User user) {
		return reload(id, null, null, user);
	}

	public static Buy reload(long id, String name, BuyStatus status, User user) {
		return new Buy(id, name, status, user);
	}
}