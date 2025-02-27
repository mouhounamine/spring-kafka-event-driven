package net.amine.order_service.controllers;

import net.amine.base_domains.dtos.Order;
import net.amine.base_domains.dtos.OrderEvent;
import net.amine.order_service.kafka.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    private OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestBody Order order) {
        LOGGER.info("Order placement request received with orderName: {}, orderPrice: {}, orderQuantity: {}",
                order.getOrderName(),
                order.getPrice(),
                order.getQuantity());

        order.setOrderId(UUID.randomUUID().toString());

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is in pending state");
        orderEvent.setOrder(order);

        orderProducer.sendMessage(orderEvent);

        LOGGER.info("Order placement event sent successfully with orderEventStatus: {}, order: {}",
                orderEvent.getStatus(),
                orderEvent.getOrder().toString());

        return "Order placed successsfully";
    }
}
