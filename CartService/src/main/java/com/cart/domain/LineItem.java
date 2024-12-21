package com.cart.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "lineItems") // Optional: Specify the collection name
public class LineItem {

	@Id
	private String id; // Use String for MongoDB ObjectIds
	private int productId;
	private String productName;
	private int quantity;
	private int price;

	// No need for relationship annotations (like @ManyToOne) in MongoDB

	// Getters and Setters (optional with @Data from Lombok)
}
