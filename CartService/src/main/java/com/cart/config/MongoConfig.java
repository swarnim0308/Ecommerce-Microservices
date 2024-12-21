package com.cart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.cart", // Scan for repositories in this package
        considerNestedRepositories = true,        // Consider nested repository interfaces
        createIndexesForQueryMethods = true      // Automatically create indexes for query methods
)
public class MongoConfig {
    // Other configuration for MongoDB connection etc.
}

