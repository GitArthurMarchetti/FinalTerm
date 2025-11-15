package com.arthur.marchetti.cli;

import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.CartItem;
import com.arthur.marchetti.model.MenuItem;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CartTableModel extends AbstractTableModel {
    private final Cart cart;
    private List<CartItem> snapshot;
    private static final String[] COLS = {"Item", "Qty", "Price", "Line Total"};

    public CartTableModel(Cart cart) {
        this.cart = cart;
        this.snapshot = cart.items();
    }

    public void refresh() {
        this.snapshot = cart.items();
        fireTableDataChanged();
    }

    public MenuItem getItemAt(int row) {
        return snapshot.get(row).getItem();
    }

    @Override public int getRowCount() { return snapshot.size(); }
    @Override public int getColumnCount() { return COLS.length; }
    @Override public String getColumnName(int col) { return COLS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        var li = snapshot.get(row);
        return switch (col) {
            case 0 -> li.getItem().getName();
            case 1 -> li.getQuantity();
            case 2 -> li.getItem().getPrice().toPlainString();
            case 3 -> li.lineTotal().toPlainString();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 1;
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (col != 1) return;
        try {
            int q = Integer.parseInt(String.valueOf(aValue));
            cart.updateQty(snapshot.get(row).getItem(), q);
            refresh();
        } catch (NumberFormatException ignore) { }
    }
}