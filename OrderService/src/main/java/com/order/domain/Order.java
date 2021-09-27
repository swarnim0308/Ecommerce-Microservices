package com.order.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
