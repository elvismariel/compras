package br.com.mariel.compras.domain;

import br.com.mariel.compras.enumeration.BuyStatus;

public class Product {
	
	private long id;
	private String name;
	private BuyStatus status;
	private Buy buy;
	private User user;
	private int department;
	
	private Product(long id, String name, BuyStatus status, Buy buy, User user, int department) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.buy = buy;
		this.user = user;
		this.department = department;
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

	public Buy getBuy() {
		return buy;
	}

	public void setBuy(Buy buy) {
		this.buy = buy;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getDepartment() {
		return department;
	}

	public void setDepartment(int department) {
		this.department = department;
	}
	
	public static Product reload(long id) {
		return reload(id, null, null, null, null, 0);
	}
	
	public static Product reload(long id, String name, BuyStatus status, Buy buy, User user, int department) {
		return new Product(id, name, status, buy, user, department);
	}
}
