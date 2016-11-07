package br.com.mariel.compras.domain;

import br.com.mariel.compras.enumeration.CrudStatus;
import br.com.mariel.compras.enumeration.TypeStatus;

public class Synchronizer {
	
	private String id;
	private TypeStatus item_type;
	private CrudStatus item_action;
	private String item_object;
	private User userToReceive;
	
	private Synchronizer(String id, TypeStatus item_type, CrudStatus item_action, String item_object, User userToReceive) {
		super();
		this.id = id;
		this.item_type = item_type;
		this.item_action = item_action;
		this.item_object = item_object;
		this.userToReceive = userToReceive;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public TypeStatus getItem_type() {
		return item_type;
	}
	public void setItem_type(TypeStatus item_type) {
		this.item_type = item_type;
	}

	public CrudStatus getItem_action() {
		return item_action;
	}
	public void setItem_action(CrudStatus item_action) {
		this.item_action = item_action;
	}

	public String getItem_object() {
		return item_object;
	}
	public void setItem_object(String item_object) {
		this.item_object = item_object;
	}

	public User getUserToReceive() {
		return userToReceive;
	}
	public void setUserToReceive(User userToReceive) {
		this.userToReceive = userToReceive;
	}
	
	public static Synchronizer reload(TypeStatus item_type, CrudStatus item_action, String item_object, User userToReceive){
		return reload(null, item_type, item_action, item_object, userToReceive);
	}
	
	public static Synchronizer reload(String id, TypeStatus item_type, CrudStatus item_action, String item_object, User userToReceive){
		return new Synchronizer(id, item_type, item_action, item_object, userToReceive);
	}
}
