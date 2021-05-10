package org.demo.order;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Transactional
    public Order createOrderWithItems() {
        var order = new Order();
        var item1 = new OrderItem();
        order.addItem(item1);
        var item2 = new OrderItem();
        order.addItem(item2);
        repository.save(order);
        return order;
    }
}
