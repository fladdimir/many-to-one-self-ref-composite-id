package org.demo.inheritence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredOfferSourceRepository extends JpaRepository<StoredOfferSource, Long> {

}
