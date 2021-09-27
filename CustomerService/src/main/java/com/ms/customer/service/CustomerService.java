package com.ms.customer.service;

import com.ms.customer.entity.Customer;
import com.ms.customer.exception.IdNotFoundException;

public interface CustomerService {

	Customer save(Customer customer);

	Customer findCustomerById(int id) throws IdNotFoundException;

	void deleteCustomer(int id) throws IdNotFoundException;

	Customer updateCustomerById(Customer customer, int id) throws IdNotFoundException;

}
