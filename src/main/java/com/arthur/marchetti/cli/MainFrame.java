package com.arthur.marchetti.cli;

import com.arthur.marchetti.interfaces.TaxCalculator.CatalogRepository;
import com.arthur.marchetti.interfaces.TaxCalculator.TaxCalculator;
import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.Category;
import com.arthur.marchetti.model.FlatRateTaxCalculator;
import com.arthur.marchetti.model.MenuItem;
import com.arthur.marchetti.model.Order;
import com.arthur.marchetti.repo.FileReceiptRepository;
import com.arthur.marchetti.repo.InMemoryCatalogRepository;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final CatalogRepository catalogRepo = new InMemoryCatalogRepository();
    private final Cart cart = new Cart();
    private final TaxCalculator taxCalc = new FlatRateTaxCalculator(new BigDecimal("0.06"));
    private final FileReceiptRepository receiptRepository = new FileReceiptRepository(defaultReceiptDir());

    private JList<MenuItem> itemsList;
    private DefaultListModel<MenuItem> itemsModel;
    private JSpinner qtySpinner;
    private JButton addBtn;
    private JTable cartTable;
    private CartTableModel cartTableModel;
    private JLabel subtotalLbl;
    private JLabel taxLbl;
    private JLabel totalLbl;

    private static final Color COLOR_BACKGROUND = new Color(250, 245, 238);
    private static final Color COLOR_PANEL = new Color(255, 255, 255);
    private static final Color COLOR_PRIMARY = new Color(101, 67, 33);
    private static final Color COLOR_SECONDARY = new Color(139, 90, 43);
    private static final Color COLOR_ACCENT = new Color(210, 180, 140);
    private static final Color COLOR_BUTTON = new Color(139, 90, 43);
    private static final Color COLOR_BUTTON_HOVER = new Color(101, 67, 33);
    private static final Color COLOR_CHECKOUT = new Color(34, 139, 34);

    public MainFrame() {
        super("☕ Cafe Order Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        setLayout(new BorderLayout(15, 15));
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadItems(Category.DRINK);
        updateTotals();
    }

    private JComponent buildLeftPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        var titleLabel = new JLabel("Categories");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        var drinksBtn = createCategoryButton("☕ Drinks");
        drinksBtn.addActionListener(e -> loadItems(Category.DRINK));

        var bakeryBtn = createCategoryButton("🥐 Bakery");
        bakeryBtn.addActionListener(e -> loadItems(Category.BAKERY));

        var sandwichBtn = createCategoryButton("🥪 Sandwiches");
        sandwichBtn.addActionListener(e -> loadItems(Category.SANDWITCH));

        panel.add(drinksBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(bakeryBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(sandwichBtn);

        panel.setPreferredSize(new Dimension(220, 10));
        return panel;
    }

    private JButton createCategoryButton(String text) {
        var btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(COLOR_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setPreferredSize(new Dimension(180, 50));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BUTTON);
            }
        });
        
        return btn;
    }

    private JComponent buildCenterPanel() {
        var root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(COLOR_PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        var titleLabel = new JLabel("Items");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_PRIMARY);
        root.add(titleLabel, BorderLayout.NORTH);

        itemsModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsModel);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsList.addListSelectionListener(e -> onItemSelected(e));
        itemsList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        itemsList.setBackground(Color.WHITE);
        itemsList.setSelectionBackground(COLOR_ACCENT);
        itemsList.setSelectionForeground(COLOR_PRIMARY);

        itemsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                var c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MenuItem mi) {
                    setText(mi.getName() + "  —  $" + mi.getPrice().toPlainString());
                    setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                }
                return c;
            }
        });

        var scrollPane = new JScrollPane(itemsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1, true));
        root.add(scrollPane, BorderLayout.CENTER);

        var foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        foot.setBackground(COLOR_PANEL);
        var qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qtyLabel.setForeground(COLOR_PRIMARY);
        foot.add(qtyLabel);
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtySpinner.setPreferredSize(new Dimension(60, 35));
        foot.add(qtySpinner);

        addBtn = new JButton("➕ Add to Cart");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setOpaque(true);
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.addActionListener(e -> addSelectedToCart());
        addBtn.setEnabled(false);
        updateAddButtonStyle();
        addBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (addBtn.isEnabled()) {
                    addBtn.setBackground(COLOR_BUTTON_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateAddButtonStyle();
            }
        });
        foot.add(addBtn);

        root.add(foot, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildRightPanel() {
        var panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        var titleLabel = new JLabel("🛒 Cart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        cartTableModel = new CartTableModel(cart);
        cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.setRowHeight(35);
        cartTable.setSelectionBackground(COLOR_ACCENT);
        cartTable.setSelectionForeground(COLOR_PRIMARY);
        cartTable.setGridColor(COLOR_ACCENT);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(COLOR_PRIMARY);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        cartTableModel.addTableModelListener(e -> updateTotals());

        var scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        var btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(COLOR_PANEL);
        
        var removeBtn = new JButton("🗑️ Remove");
        removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeBtn.setBackground(new Color(220, 53, 69));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setOpaque(true);
        removeBtn.setPreferredSize(new Dimension(100, 35));
        removeBtn.addActionListener(e -> removeSelectedCartLine());
        removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeBtn.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeBtn.setBackground(new Color(220, 53, 69));
            }
        });
        
        var clearBtn = new JButton("🗑️ Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearBtn.setBackground(new Color(108, 117, 125));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setPreferredSize(new Dimension(100, 35));
        clearBtn.addActionListener(e -> {
            cart.clear();
            cartTableModel.refresh();
            updateTotals();
        });
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearBtn.setBackground(new Color(90, 98, 104));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearBtn.setBackground(new Color(108, 117, 125));
            }
        });

        btns.add(removeBtn);
        btns.add(clearBtn);
        panel.add(btns, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(450, 10));
        return panel;
    }

    private JComponent buildBottomBar() {
        var panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_ACCENT),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        subtotalLbl = new JLabel("Subtotal: $0.00");
        subtotalLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtotalLbl.setForeground(COLOR_PRIMARY);
        
        taxLbl = new JLabel("Tax: $0.00");
        taxLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taxLbl.setForeground(COLOR_PRIMARY);
        
        totalLbl = new JLabel("Total: $0.00");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLbl.setForeground(COLOR_PRIMARY);

        var checkoutBtn = new JButton("💳 Checkout");
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkoutBtn.setBackground(COLOR_CHECKOUT);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setOpaque(true);
        checkoutBtn.setPreferredSize(new Dimension(180, 50));
        checkoutBtn.addActionListener(e -> handleCheckout());
        checkoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                checkoutBtn.setBackground(new Color(28, 115, 28));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                checkoutBtn.setBackground(COLOR_CHECKOUT);
            }
        });

        panel.add(subtotalLbl);
        panel.add(taxLbl);
        panel.add(totalLbl);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(checkoutBtn);
        return panel;
    }


    private void onItemSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            var selected = itemsList.getSelectedValue();
            addBtn.setEnabled(selected != null);
            updateAddButtonStyle();
            if (selected != null) {
                qtySpinner.setValue(1);
            }
        }
    }

    private void updateAddButtonStyle() {
        if (addBtn.isEnabled()) {
            addBtn.setBackground(COLOR_BUTTON);
            addBtn.setForeground(Color.WHITE);
        } else {
            addBtn.setBackground(new Color(200, 200, 200));
            addBtn.setForeground(new Color(120, 120, 120));
        }
    }

    private void addSelectedToCart() {
        var item = itemsList.getSelectedValue();
        if (item == null) return;
        int qty = (int) qtySpinner.getValue();
        try {
            cart.add(item, qty);
            cartTableModel.refresh();
            updateTotals();
            qtySpinner.setValue(1);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid quantity", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedCartLine() {
        int row = cartTable.getSelectedRow();
        if (row < 0) return;
        var item = cartTableModel.getItemAt(row);
        cart.remove(item.getName());
        cartTableModel.refresh();
        updateTotals();
    }

    private void loadItems(Category category) {
        List<MenuItem> all = catalogRepo.all();
        var list = all.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());

        itemsModel.clear();
        for (var m : list) itemsModel.addElement(m);

        addBtn.setEnabled(false);
        updateAddButtonStyle();
    }

    private void updateTotals() {
        var sub = cart.getSubtotal();
        var tax = cart.getTax(taxCalc);
        var tot = cart.getTotal(taxCalc);

        subtotalLbl.setText("Subtotal: $" + sub.toPlainString());
        taxLbl.setText("Tax: $" + tax.toPlainString());
        totalLbl.setText("Total: $" + tot.toPlainString());
    }


    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.", "Cannot checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter customer name:", "Checkout", JOptionPane.PLAIN_MESSAGE);
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order order = buildOrderSnapshot(name);
        String receiptText = ReceiptFormatter.format(order);
        var receiptLines = receiptText.lines().toList();
        boolean receiptShown = false;

        try {
            var file = receiptRepository.save(receiptLines);
            new ReceiptDialog(this, order, file).setVisible(true);
            receiptShown = true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save receipt: " + ex.getMessage(),
                    "Receipt Error", JOptionPane.ERROR_MESSAGE);
        }

        if (receiptShown) {
            cart.clear();
            cartTableModel.refresh();
            updateTotals();
        }
    }

    private Order buildOrderSnapshot(String customerName) {
        var subtotal = cart.getSubtotal();
        var tax = cart.getTax(taxCalc);
        var total = cart.getTotal(taxCalc);

        String orderId = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());

        return new Order(
                orderId,
                customerName,
                Instant.now(),
                cart.items(),
                subtotal, tax, total
        );
    }

    private static Path defaultReceiptDir() {
        return Paths.get(System.getProperty("user.home"), "kiosk-receipts");
    }
}