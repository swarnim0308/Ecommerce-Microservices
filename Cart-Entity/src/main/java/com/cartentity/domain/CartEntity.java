package com.cartentity.domain;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
@Entity
public class CartEntity implements Serializable {
	@Id
	int cartid;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_cart", referencedColumnName = "cartid")
	List<LineItem> lineitem;

	public int getCartid() {
		return cartid;
	}

	public void setCartid(int cartid) {
		this.cartid = cartid;
	}

	public List<LineItem> getLineitem() {
		return lineitem;
	}

	public void setLineitem(List<LineItem> lineitem) {
		this.lineitem = lineitem;
	}

	public CartEntity() {
	}

	public CartEntity(int cartid, List<LineItem> lineitem) {
		super();
		this.cartid = cartid;
		this.lineitem = lineitem;
	}

}
