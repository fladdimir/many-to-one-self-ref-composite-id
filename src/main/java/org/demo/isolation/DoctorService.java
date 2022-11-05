package org.demo.isolation;

import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

    @Inject
    private DoctorRepository repository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setOnCallFalseIfPossible_RepeatableRead(Long id, Semaphore toRelease, Semaphore toAcquire) {
        setOnCallFalseIfPossible(id, toRelease, toAcquire);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setOnCallFalseIfPossible_Serializable(Long id, Semaphore toRelease, Semaphore toAcquire) {
        setOnCallFalseIfPossible(id, toRelease, toAcquire);
    }

    private void setOnCallFalseIfPossible(Long id, Semaphore toRelease, Semaphore toAcquire) {

        if (repository.countByOnCall(true) > 1) {

            waitForConcurrentTx(toRelease, toAcquire);

            repository.findById(id).get().setOnCall(false);
        }
    }

    private void waitForConcurrentTx(Semaphore toRelease, Semaphore toAcquire) {
        toRelease.release();
        acquire(toAcquire); // the other Thread also released
    }

    private void acquire(Semaphore toAcquire) {
        try {
            toAcquire.acquire();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
