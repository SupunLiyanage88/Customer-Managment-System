insert into countries (name, code) values ('Sri Lanka', 'LK');
insert into countries (name, code) values ('India', 'IN');
insert into countries (name, code) values ('United States', 'US');

insert into cities (name, country_id) values ('Colombo', (select id from countries where code = 'LK'));
insert into cities (name, country_id) values ('Kandy', (select id from countries where code = 'LK'));
insert into cities (name, country_id) values ('Galle', (select id from countries where code = 'LK'));
insert into cities (name, country_id) values ('Jaffna', (select id from countries where code = 'LK'));

insert into cities (name, country_id) values ('Chennai', (select id from countries where code = 'IN'));
insert into cities (name, country_id) values ('Mumbai', (select id from countries where code = 'IN'));
insert into cities (name, country_id) values ('Bangalore', (select id from countries where code = 'IN'));

insert into cities (name, country_id) values ('New York', (select id from countries where code = 'US'));
insert into cities (name, country_id) values ('Boston', (select id from countries where code = 'US'));
insert into cities (name, country_id) values ('Seattle', (select id from countries where code = 'US'));

insert into customers (name, date_of_birth, nic_number) values ('Nimal Perera', date '1990-05-12', 'NIC-0001');
insert into customer_mobile_numbers (mobile_number, customer_id) values ('0771234567', (select id from customers where nic_number = 'NIC-0001'));
insert into customer_addresses (address_line1, address_line2, city_id, country_id, customer_id)
values ('12 Main Street', 'Apt 4B',
        (select id from cities where name = 'Colombo' and country_id = (select id from countries where code = 'LK')),
        (select id from countries where code = 'LK'),
        (select id from customers where nic_number = 'NIC-0001'));