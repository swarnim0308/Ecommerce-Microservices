package com.ms.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ms.customer.dao.CustomerRepository;
import com.ms.customer.entity.Customer;
import com.ms.customer.exception.IdNotFoundException;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public Customer findCustomerById(int id) throws IdNotFoundException {
		if (customerRepository.findById(id).isPresent()) {
			return customerRepository.findById(id).get();
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}

	@Override
	public void deleteCustomer(int id) throws IdNotFoundException {
		if (customerRepository.findById(id).isPresent()) {
			customerRepository.deleteById(id);
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}

	@Override
	public Customer updateCustomerById(Customer customer, int id) throws IdNotFoundException {
		if (customerRepository.findById(id).isPresent()) {
			customer.setCustomerId(id);
			customer.setCustomerName(customer.getCustomerName());
			customer.setCustomerEmail(customer.getCustomerEmail());
			customer.setCustomerBillingAddress(customer.getCustomerBillingAddress());
			customer.setCustomerShippingAddress(customer.getCustomerShippingAddress());
			return customerRepository.save(customer);
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}
}
