package br.com.mariel.compras.domain;

import java.util.HashMap;
import java.util.Map;

public class SyncList {

	private Map<String, Buy> buyMap;
	private Map<String, Product> productMap;
	private Map<String, User> userMap;
	private Map<String, Share> shareMap;
	
	public SyncList() {
		this.buyMap = new HashMap<String, Buy>();
		this.productMap = new HashMap<String, Product>();
		this.userMap = new HashMap<String, User>();
		this.shareMap = new HashMap<String, Share>();
	}

	public Map<String, Buy> getBuyMap() {
		return buyMap;
	}
	public void setBuyMap(Map<String, Buy> buyMap) {
		this.buyMap = buyMap;
	}

	public Map<String, Product> getProductMap() {
		return productMap;
	}
	public void setProductMap(Map<String, Product> productMap) {
		this.productMap = productMap;
	}

	public Map<String, User> getUserMap() {
		return userMap;
	}
	public void setUserMap(Map<String, User> userMap) {
		this.userMap = userMap;
	}

	public Map<String, Share> getShareMap() {
		return shareMap;
	}
	public void setShareMap(Map<String, Share> shareMap) {
		this.shareMap = shareMap;
	}
}
