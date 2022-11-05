package org.demo.isolation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import org.hibernate.exception.LockAcquisitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IsolationTest {

    @Inject
    private DoctorRepository repository;

    @Inject
    private DoctorService service;

    long docId1;
    long docId2;

    Semaphore s1 = new Semaphore(1);
    Semaphore s2 = new Semaphore(1);

    @BeforeEach
    void beforeEach() {
        repository.deleteAllInBatch();
        docId1 = repository.save(new Doctor()).getId();
        docId2 = repository.save(new Doctor()).getId();
        assertThat(repository.countByOnCall(true)).isEqualTo(2);
    }

    @Test
    void test_non_concurrent() {

        service.setOnCallFalseIfPossible_RepeatableRead(docId1, s1, s2); // works
        s2.release();
        service.setOnCallFalseIfPossible_RepeatableRead(docId2, s1, s2); // does not work

        assertThat(repository.findById(docId1).get().isOnCall()).isFalse(); // worked
        assertThat(repository.findById(docId2).get().isOnCall()).isTrue(); // still on call
        assertThat(repository.countByOnCall(true)).isEqualTo(1);
    }

    @Test
    void test_concurrent_repeatableRead() throws Throwable {

        s1.acquire();
        s2.acquire();

        Thread t1 = new Thread(() -> service.setOnCallFalseIfPossible_RepeatableRead(docId1, s2, s1));
        Thread t2 = new Thread(() -> service.setOnCallFalseIfPossible_RepeatableRead(docId2, s1, s2));

        t1.start(); // waits until 2nd thread releases s1
        t2.start(); // waits until 1st thread releases s2

        t1.join();
        t2.join();

        assertThat(repository.countByOnCall(true)).isZero(); // RepeatableRead allows violation
    }

    @Test
    void test_concurrent_serializable() throws Throwable {

        s1.acquire();
        s2.acquire();

        Thread t1 = new Thread(() -> service.setOnCallFalseIfPossible_Serializable(docId1, s2, s1));
        Thread t2 = new Thread(() -> service.setOnCallFalseIfPossible_Serializable(docId2, s1, s2));

        List<Throwable> exceptions = new LinkedList<>();
        t1.setUncaughtExceptionHandler((t, e) -> exceptions.add(e));
        t2.setUncaughtExceptionHandler((t, e) -> exceptions.add(e));

        t1.start(); // waits until 2nd thread releases s1
        t2.start(); // waits until 1st thread releases s2

        t1.join();
        t2.join();

        assertThat(repository.countByOnCall(true)).isEqualTo(1); // serializable disallows violation
        assertThat(exceptions).hasSize(1); // one update failed
        assertThat(exceptions.get(0)).hasCauseInstanceOf(LockAcquisitionException.class)
                .hasMessageContaining("update doctor set on_call");
    }

}
