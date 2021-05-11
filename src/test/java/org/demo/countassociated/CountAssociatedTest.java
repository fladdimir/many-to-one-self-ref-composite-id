package org.demo.countassociated;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class CountAssociatedTest {

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	PlanRepository planRepository;

	@Autowired
	RateCardRepository rateCardRepository;

	@BeforeEach
	public void setup() {
		subscriptionRepository.deleteAll();
		planRepository.deleteAll();
		rateCardRepository.deleteAll();

		// create some test data
		var rateCard1 = rateCardRepository.save(new RateCard(1L));

		var plan1 = planRepository.save(new Plan());
		var plan2 = planRepository.save(new Plan());

		var sub1 = subscriptionRepository.save(new Subscription());
		var sub2 = subscriptionRepository.save(new Subscription());
		var sub3 = subscriptionRepository.save(new Subscription());
		var sub4 = subscriptionRepository.save(new Subscription());
		var sub5 = subscriptionRepository.save(new Subscription());

		plan1.setRateCard(rateCard1);
		plan2.setRateCard(rateCard1);
		sub1.setPlan(plan1);
		sub2.setPlan(plan1);
		sub3.setPlan(plan1);
		sub4.setPlan(plan1);
		sub5.setPlan(plan2);

		var rateCard2 = rateCardRepository.save(new RateCard(2L));
		var plan3 = planRepository.save(new Plan());
		plan3.setRateCard(rateCard2); // no subscriptions

		List.of(rateCard1, rateCard2).forEach(rateCardRepository::save);
		List.of(plan1, plan2, plan3).forEach(planRepository::save);
		List.of(sub1, sub2, sub3, sub4, sub5).forEach(subscriptionRepository::save);
	}

	@Test
	public void test() {
		assertThat(subscriptionRepository.count()).isEqualTo(5);
		assertThat(planRepository.count()).isEqualTo(3);
		assertThat(rateCardRepository.count()).isEqualTo(2);

		RateCard rateCard1 = rateCardRepository.findById(1L).get();
		assertThat(subscriptionRepository.countByPlanRateCardId(rateCard1.getId())).isEqualTo(5);

		RateCard rateCard2 = rateCardRepository.findById(2L).get();
		assertThat(subscriptionRepository.countByPlanRateCardId(rateCard2.getId())).isEqualTo(0);
	}

}
