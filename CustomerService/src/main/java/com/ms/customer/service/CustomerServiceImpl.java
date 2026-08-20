package com.ms.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ms.customer.dao.CustomerRepository;
import com.ms.customer.entity.Customer;
import com.ms.customer.exception.IdNotFoundException;
import com.ms.customer.security.JwtUtil;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Customer save(Customer customer) {
		if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
			customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		}
		if (customer.getRole() == null || customer.getRole().isEmpty()) {
			customer.setRole("CUSTOMER");
		}
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

	@Override
	public Customer findCustomerByEmail(String email) throws IdNotFoundException {
		return customerRepository.findByCustomerEmail(email)
				.orElseThrow(() -> new IdNotFoundException("Customer not found for email: " + email));
	}

	@Override
	public String login(String email, String password) throws IdNotFoundException {
		Customer customer = findCustomerByEmail(email);
		if (customer.getPassword() == null
				|| !passwordEncoder.matches(password, customer.getPassword())) {
			throw new IdNotFoundException("Invalid email or password");
		}
		return jwtUtil.generateToken(customer.getCustomerId(), customer.getCustomerEmail(), customer.getRole());
	}
}
