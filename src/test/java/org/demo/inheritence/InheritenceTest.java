package org.demo.inheritence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class InheritenceTest {

	@Autowired
	StoredAdSourceRepository adSourceRepository;

	@Autowired
	StoredOfferSourceRepository offerSourceRepository;

	@Autowired
	StoredDataRepository dataRepository;

	@BeforeEach
	public void setup() {
		dataRepository.deleteAll();
		dataRepository.flush();

		// data1 <-> ad1
		StoredAdSource ad1 = new StoredAdSource();
		StoredData data1 = new StoredData();
		ad1.setData(data1);
		data1.setSource(ad1);
		dataRepository.save(data1); // cascaded save of ad1
	}

	@Test
	void testSetup() {
		assertThat(offerSourceRepository.findAll()).isEmpty();
		assertThat(adSourceRepository.findAll()).hasSize(1);
		assertThat(dataRepository.findAll()).hasSize(1);
	}

	@Test
	void testChangeStoredSourceEntity() {
		var ad1 = adSourceRepository.findAll().get(0);
		var data1 = ad1.getData();

		// data1 <-> (new) ad2
		var ad2 = new StoredAdSource();
		data1.setSource(ad2);
		ad2.setData(data1);

		dataRepository.saveAndFlush(data1);
		// also saves ad2
		// and deletes the "orphan" ad1, which is not connected to any data now

		assertThat(adSourceRepository.findAll()).hasSize(1);
		assertThat(dataRepository.findAll()).hasSize(1);
	}

	@Test
	void testChangeStoredSourceProperty() {
		String newAdIdValue = "crazy_new_ad_id";

		var data1 = dataRepository.findAll().get(0);
		// JPA instantiated the correct sub-class (data did not need to know about)
		if (data1.getSource() instanceof StoredAdSource) {
			var ad1 = (StoredAdSource) data1.getSource();
			ad1.setAdId(newAdIdValue);
		}

		dataRepository.saveAndFlush(data1); // cascades save

		assertThat(dataRepository.findAll()).hasSize(1);
		assertThat(adSourceRepository.findAll()).hasSize(1);
		assertThat(adSourceRepository.findAll().get(0).getAdId()).isEqualTo(newAdIdValue);
	}

}
