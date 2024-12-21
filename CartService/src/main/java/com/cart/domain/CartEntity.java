package com.cart.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "carts") // Optional: Specify the collection name
public class CartEntity {

	@Id
	private String id; // Use String for MongoDB ObjectIds
	private List<LineItem> lineItems; // Use List<LineItem> for the collection of items

	// No need for JPA annotations like @OneToMany or @JoinColumn

	// Getters and Setters (optional with @Data from Lombok)
}
