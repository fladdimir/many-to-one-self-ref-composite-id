package org.demo.onetoonecascade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class CarEngineTest {

	@Autowired
	CarRepository repository;

	@BeforeEach
	public void setup() {
		repository.deleteAll();
		var all = repository.findAll();
		assertThat(all).isEmpty();
	}

	@Test
	public void test() {
		var car = new Car();
		var engine = new Engine();

		car.setEngine(engine); // <-- save(car) will cascade persist to engine
		engine.setCar(car); // <-- saving the engine will persist the foreign key of the associated car

		repository.save(car);

		var cars = repository.findAll();
		assertThat(cars).hasSize(1);
		var engineOfPersistedCar = cars.get(0).getEngine();
		assertThat(engineOfPersistedCar).isNotNull();
	}

}
