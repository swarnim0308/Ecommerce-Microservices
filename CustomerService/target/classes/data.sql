/* Address Sample Data */
insert into customer_address(address_id, city, door_no, layout, pincode, street_name)
values(1, 'address1', 1, 'layout 1', 111111, 'street1');

insert into customer_address(address_id, city, door_no, layout, pincode, street_name)
values(2, 'address2', 2, 'layout 2', 222222, 'street2');

insert into customer_address(address_id, city, door_no, layout, pincode, street_name)
values(3, 'address3', 3, 'layout 3', 333333, 'street3');

insert into customer_address(address_id, city, door_no, layout, pincode, street_name)
values(4, 'address4', 4, 'layout 4', 444444, 'street4');

insert into customer_address(address_id, city, door_no, layout, pincode, street_name)
values(5, 'address5', 5, 'layout 5', 55555, 'street5');

/* Customer Sample data */

insert into customer(customer_id, customer_email_id, customer_name, billing_address_id, shipping_address_id)
values(1, 'customer1@email.com', 'customer1', 1, 1);

insert into customer(customer_id, customer_email_id, customer_name, billing_address_id, shipping_address_id)
values(2, 'customer2@email.com', 'customer2', 2, 2);

insert into customer(customer_id, customer_email_id, customer_name, billing_address_id, shipping_address_id)
values(3, 'customer3@email.com', 'customer3', 3, 3);

insert into customer(customer_id, customer_email_id, customer_name, billing_address_id, shipping_address_id)
values(4, 'customer4@email.com', 'customer4', 4, 4);

insert into customer(customer_id, customer_email_id, customer_name, billing_address_id, shipping_address_id)
values(5, 'customer5@email.com', 'customer5', 5, 5)
