package org.demo.order;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ORDER_ITEMS")
@IdClass(OrderItemId.class)
public class OrderItem {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private Order order;

    @Id
    @SequenceGenerator(name = "ordItmSeq", sequenceName = "ORDER_ITEM_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "ordItmSeq")
    private Long itemId;

    private String itemText;

    @Override
    public String toString() {
        return "{" + " orderId='" + getOrder().getOrderId() + "'" + ", itemId='" + getItemId() + "'" + ", itemText='"
                + getItemText() + "'" + "}";
    }

}