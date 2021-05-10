package org.demo.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "ORDERS")
public class Order implements Serializable {

    @Id
    @SequenceGenerator(name = "ordSeq", sequenceName = "ORDER_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "ordSeq")
    private Long orderId;

    private String status;

    @CreationTimestamp
    private Date orderDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItem> orderItems;

    public void addItem(OrderItem item) {
        if (orderItems == null)
            orderItems = new ArrayList<OrderItem>();
        orderItems.add(item);
        item.setOrder(this);
    }
}