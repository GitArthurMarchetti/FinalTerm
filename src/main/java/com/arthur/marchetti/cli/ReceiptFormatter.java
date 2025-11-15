package com.arthur.marchetti.cli;

import com.arthur.marchetti.model.CartItem;
import com.arthur.marchetti.model.Order;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class ReceiptFormatter {
    private ReceiptFormatter() {}

    public static String format(Order order) {
        StringBuilder sb = new StringBuilder();
        var dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(order.timestamp());

        sb.append("Cafe Receipt\n");
        sb.append("Order: ").append(order.orderId())
                .append("    Date: ").append(dt).append("\n");
        sb.append("Customer: ").append(order.customerName()).append("\n");
        sb.append("------------------------------------------\n");

        for (CartItem li : order.lines()) {
            sb.append(li.getQuantity()).append(" x ")
                    .append(li.getItem().getName())
                    .append(" @ ").append(li.getItem().getPrice().toPlainString())
                    .append(" = ").append(li.lineTotal().toPlainString())
                    .append("\n");
        }

        sb.append("------------------------------------------\n");
        sb.append("Subtotal: ").append(order.subtotal().toPlainString()).append("\n");
        sb.append("Tax: ").append(order.tax().toPlainString()).append("\n");
        sb.append("Total: ").append(order.total().toPlainString()).append("\n");
        sb.append("Thank you!\n");
        return sb.toString();
    }
}