package org.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.demo.order.OrderRepository;
import org.demo.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class OrderTest {

	@Autowired
	OrderRepository repository;

	@Autowired
	OrderService service;

	@BeforeEach
	public void setup() {

		repository.deleteAll();

		service.createOrderWithItems();
		service.createOrderWithItems();
	}

	@Test
	public void testFindAllWithRelations() {
		var all = repository.findAll();
		assertThat(all).hasSize(2);
		all.forEach(order -> assertThat(order.getOrderItems()).hasSize(2));
	}

}
