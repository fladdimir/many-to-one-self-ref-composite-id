package org.demo.manyToOneCompositeKey;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.demo.manyToOneCompositeKey.Entities.Student;
import org.demo.manyToOneCompositeKey.Entities.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class MtoCkTest {

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@BeforeEach
	public void setup() {
		// delete children before parents (if present)
		studentRepository.deleteAll();
		subjectRepository.deleteAll();

		var student = new Student();
		student.studentId = "student1";

		var subject1 = new Subject();
		subject1.name = "subject_name_1";
		subject1.volume = "subject_volume_1";

		var subject2 = new Subject();
		subject2.name = "subject_name_2";
		subject2.volume = "subject_volume_2";

		student.subject = subject1;

		subjectRepository.saveAll(List.of(subject1, subject2));
		studentRepository.save(student);
	}

	@Test
	public void testSubject1() {
		assertThat(studentRepository.findAll()).hasSize(1);
		assertThat(subjectRepository.findAll()).hasSize(2);

		var student = studentRepository.findById("student1").get();

		assertThat(student.subjectName).isEqualTo("subject_name_1");
	}

	@Test
	public void testSwitchToSubject2() {
		var student = studentRepository.findById("student1").get();
		var subjects = subjectRepository.findAll();
		var subject2 = subjects.stream().filter(s -> s.name.equals("subject_name_2")).findAny().get();

		student.subject = subject2;
		studentRepository.saveAndFlush(student);

		student = studentRepository.findById("student1").get();
		assertThat(student.subjectName).isEqualTo("subject_name_2");
	}

}
