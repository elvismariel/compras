package br.com.mariel.compras.domain;

public class Share {

	private Buy buy;
	private User buyUser;
	private User user;
	
	private Share(Buy buy, User buyUser, User user) {
		super();
		this.buy = buy;
		this.buyUser = buyUser;
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public Buy getBuy() {
		return buy;
	}
	public void setBuy(Buy buy) {
		this.buy = buy;
	}
	
	public User getBuyUser() {
		return buyUser;
	}
	public void setBuyUser(User buyUser) {
		this.buyUser = buyUser;
	}
	
	public static Share reload(Buy buy, User buyUser, User user) {
		return new Share(buy, buyUser, user);
	}
}
