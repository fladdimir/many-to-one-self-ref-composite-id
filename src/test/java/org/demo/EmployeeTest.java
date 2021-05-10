package org.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.demo.employee.DepartmentRepository;
import org.demo.employee.EmployeeRepository;
import org.demo.employee.Entities.Department;
import org.demo.employee.Entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class EmployeeTest {

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@BeforeEach
	public void before() {
		departmentRepository.findAll().forEach(d -> d.getEmployees().clear());
		departmentRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	@Test
	public void test() {
		var emp = new Employee();

		var dep1 = new Department();
		var dep2 = new Department();
		departmentRepository.save(dep1);
		departmentRepository.save(dep2);

		dep1.getEmployees().add(emp);
		dep2.getEmployees().add(emp);

		employeeRepository.save(emp);

		assertThat(departmentRepository.count()).isEqualTo(2);
		assertThat(employeeRepository.count()).isEqualTo(1);
	}

}
