package onlineretailstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import onlineretailstore.entity.Product;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private Long productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int quantity;
	
	public Product getProduct() {
		return new Product(this.productId, this.productName, this.productDescription, this.productPrice);
	}
}
