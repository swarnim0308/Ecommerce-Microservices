package com.order.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int orderid;
	@OneToMany(cascade = CascadeType.MERGE)
	@JoinColumn(name = "fk_order", referencedColumnName = "orderid")
	List<LineItem> lineitem;

	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public List<LineItem> getLineitem() {
		return lineitem;
	}

	public void setLineitem(List<LineItem> lineitem) {
		this.lineitem = lineitem;
	}

	public Order() {}
	public Order(int orderid, List<LineItem> lineitem) {
		super();
		this.orderid = orderid;
		this.lineitem = lineitem;
	}

}
