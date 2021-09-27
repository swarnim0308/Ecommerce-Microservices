package onlineretailstore.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="Product")
public class Product {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	public double getProductPrice() {
		return productPrice;
	}
  public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public Product()
	{
	
	}
	 public Product(Long productId, String productName,String productDescription,double productPrice) {
		 super();
		 this.productId=productId;
		 this.productName=productName;
		 this.productDescription=productDescription;
	 this.productPrice=productPrice;
		 
	 }
	
	 public String ToString() {
		 
	 return "Product[productId="+productId+",productName="+productName+",productDescription="+productDescription+",productPrice="+productPrice+"]";
		 
	 }
}
