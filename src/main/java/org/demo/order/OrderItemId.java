package org.demo.order;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderItemId implements Serializable {

    private Order order;

    private Long itemId;
}
