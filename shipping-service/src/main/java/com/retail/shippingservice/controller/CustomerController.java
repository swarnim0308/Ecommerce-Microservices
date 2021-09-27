package com.retail.shippingservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.retail.shippingservice.entity.CustomerOrder;
import com.retail.shippingservice.repository.CustomerOrderRepository;
import com.retail.shippingservice.vo.CartVo;
import com.retail.shippingservice.vo.CustomerOrderVo;
import com.retail.shippingservice.vo.CustomerVo;
import com.retail.shippingservice.vo.LineItemVo;
import com.retail.shippingservice.vo.OrderVo;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private EurekaClient client;
	@Autowired
	private RestTemplate template;
	@Autowired
	private CustomerOrderRepository customerOrderRepository;
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello from composite Service";
	}
	//This API is used by external client to create a customer.
	@PostMapping("/")
	//@HystrixCommand(fallbackMethod = "fallbackfunction")
	public ResponseEntity<?> createCustomer(@RequestBody CustomerVo customerVo){
		
		
		String customerBaseUrl = getBaseURL("Customer-Service");
		customerBaseUrl += "/customer/addCustomer";
		ResponseEntity<CustomerVo> customerResponse = template.postForEntity(customerBaseUrl,customerVo, CustomerVo.class);
		CustomerVo newCustomer = customerResponse.getBody();
		System.err.println(newCustomer);
		int customerID = newCustomer.getCustomerId();
		CartVo cartVo = new CartVo(customerID, new ArrayList<LineItemVo>());
		
		String cartBaseUrl = getBaseURL("Cart-Service");
		cartBaseUrl += "/api/cart";
		//template.put(cartBaseUrl, cartVo);
		ResponseEntity<CartVo> newCartResponse = template.postForEntity(cartBaseUrl, cartVo, CartVo.class);
		CartVo newCart = newCartResponse.getBody();
		System.err.println(newCart);
		return new ResponseEntity<>(newCustomer, HttpStatus.OK);
	}
	
	//This API is used by external client to add products to the cart.
	@PutMapping("/{customerId}/cart")
	public ResponseEntity<?> addProductToCart(@PathVariable Long customerId, @RequestBody CartVo cartVo){
		//find customer
		String customerBaseUrl = getBaseURL("Customer-Service");
		String getCustomerUrl = customerBaseUrl+"/customer/searchCustomer/"+customerId;
		System.err.println(getCustomerUrl);
		ResponseEntity<CustomerVo> customerResponse =  template.getForEntity(getCustomerUrl, CustomerVo.class);
		CustomerVo theCustomer = customerResponse.getBody();
		//find cart
		String cartBaseUrl = getBaseURL("Cart-Service");
		String cartUpdateUrl = cartBaseUrl+ "api/cart/"+customerId;
		template.put(cartUpdateUrl, cartVo);
		//return cartVO
		return new ResponseEntity<CartVo>(template.getForEntity(cartUpdateUrl, CartVo.class).getBody(), HttpStatus.OK);
	}
	
	//placing order: remaining:update inventory, verify product
	@PostMapping("{customerId}/order")
	public ResponseEntity<?> placingOrder(@PathVariable Long customerId){
		String customerBaseUrl = getBaseURL("Customer-Service");
		String getCustomerUrl = customerBaseUrl+"customer/searchCustomer/"+customerId;
		System.err.println(getCustomerUrl);
		ResponseEntity<CustomerVo> customerResponse =  template.getForEntity(getCustomerUrl, CustomerVo.class);
		CustomerVo theCustomer = customerResponse.getBody();
		
		String cartBaseUrl = getBaseURL("Cart-Service");
		String getCartUrl = cartBaseUrl+ "api/cart/"+customerId;
		ResponseEntity<CartVo> cartResponse = template.getForEntity(getCartUrl, CartVo.class);
		CartVo theCart = cartResponse.getBody();
		System.out.println("@@@@@@@@@@@@");
		System.out.println(theCart);
		OrderVo orderVo = new OrderVo();
		//List<LineItemVo>
		orderVo.setLineitem(theCart.getLineitem()) ;
		System.err.println(orderVo);
		
		String orderBaseUrl = getBaseURL("Order-Service");
		String saveOrderUrl = orderBaseUrl + "api/order";
		ResponseEntity<OrderVo> orderResponse = template.postForEntity(saveOrderUrl, orderVo, OrderVo.class);
		OrderVo order = orderResponse.getBody();
		
		CustomerOrder customerOrder = new CustomerOrder();
		customerOrder.setCustomerID(customerId);
		customerOrder.setOrderID((long) order.getOrderid());
		customerOrderRepository.save(customerOrder);
		
		//empty the cart
		emptyCart(customerId);
		
		return new ResponseEntity<OrderVo>(order, orderResponse.getStatusCode());
	}
	
	@GetMapping("{customerId}/orders")
	public ResponseEntity<?> getAllOrdersOfCustomer(@PathVariable Long customerId){
		String customerBaseUrl = getBaseURL("Customer-Service");
		String getCustomerUrl = customerBaseUrl+"customer/searchCustomer/"+customerId;
		System.err.println(getCustomerUrl);
		ResponseEntity<CustomerVo> customerResponse =  template.getForEntity(getCustomerUrl, CustomerVo.class);
		CustomerVo theCustomer = customerResponse.getBody();
		List<Long> listOfOrderIds = customerOrderRepository.findByCustomerID(customerId).stream().map(a-> a.getOrderID()).collect(Collectors.toList());
		
		String orderBaseUrl = getBaseURL("Order-Service");
		List<CustomerOrder> co  = this.customerOrderRepository.findByCustomerID(customerId);
		System.out.println(co);
		List<OrderVo> ovo = new ArrayList<>();
		for(CustomerOrder c : co) {
			String orderUrl = orderBaseUrl+"api/order/"+c.getOrderID();
			System.err.println(orderUrl);
			OrderVo tempOrderVo = template.getForEntity(orderUrl, OrderVo.class).getBody();
			ovo.add(tempOrderVo);
		}
		CustomerOrderVo customerOrder = new CustomerOrderVo(theCustomer, ovo);
		return new ResponseEntity<CustomerOrderVo>(customerOrder, HttpStatus.OK);
	}
	//fallback method
	public ResponseEntity<?> fallbackfunction(CustomerVo customerVo)
	{
		return new ResponseEntity<String>("I am default fallback method", HttpStatus.NOT_FOUND);
	}
	//helper method---
	private String getBaseURL(String service) {
		InstanceInfo customerInstanceInfo = client.getNextServerFromEureka(service, false);
		return  customerInstanceInfo.getHomePageUrl();
	}
	
	private void emptyCart(Long customerId) {
		String baseUrl = getBaseURL("Cart-Service");
		String url = baseUrl+"api/cart/"+customerId;
		template.delete(url);
		
	}
	
}
