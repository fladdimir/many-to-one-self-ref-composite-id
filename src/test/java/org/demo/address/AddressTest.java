package org.demo.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class AddressTest {

  @Autowired
  AddressRepository addressRepository;

  @Autowired
  HomeRepository homeRepository;

  @Autowired
  OfficeRepository officeRepository;

  @BeforeEach
  public void setup() {
    var repos = List.of(homeRepository, officeRepository, addressRepository);
    repos.forEach(JpaRepository::deleteAllInBatch);
    repos.forEach(repo -> assertThat(repo.findAll()).isEmpty());
  }

  @Test
  void testSetNull() {
    // arrange
    // home
    var home = homeRepository.save(new Home());
    var a1 = addressRepository.save(new Address());
    home.setAddress(a1);
    homeRepository.save(home);

    // office
    var office = officeRepository.save(new Office());
    var a2 = addressRepository.save(new Address());
    office.setAddress(a2);
    officeRepository.save(office);

    // sanity check
    assertThat(homeRepository.findById(home.getId()).get().getAddress()).isEqualTo(a1);
    assertThat(officeRepository.findById(office.getId()).get().getAddress()).isEqualTo(a2);

    // act
    home.setAddress(null); // -> orphanRemoval shall be triggered!
    homeRepository.save(home); // transaction created and commited

    // assert
    assertThat(addressRepository.findById(a1.getId())).isEmpty(); // deleted!
    assertThat(officeRepository.findById(office.getId()).get().getAddress()).isEqualTo(a2); // (not affected)
  }

}
