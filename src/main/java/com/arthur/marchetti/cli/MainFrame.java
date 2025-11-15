package com.arthur.marchetti.cli;

// Imports do seu projeto 'marchetti'
import com.arthur.marchetti.interfaces.TaxCalculator.CatalogRepository;
import com.arthur.marchetti.interfaces.TaxCalculator.TaxCalculator;
import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.Category;
import com.arthur.marchetti.model.FlatRateTaxCalculator;
import com.arthur.marchetti.model.MenuItem;
import com.arthur.marchetti.model.Order;
import com.arthur.marchetti.repo.FileReceiptRepository;
import com.arthur.marchetti.repo.InMemoryCatalogRepository;

// Imports do Swing e Java
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

    // Usando seus repositórios e modelos
    private final CatalogRepository catalogRepo = new InMemoryCatalogRepository();
    private final Cart cart = new Cart();
    private final TaxCalculator taxCalc = new FlatRateTaxCalculator(new BigDecimal("0.06"));
    private final FileReceiptRepository receiptRepository = new FileReceiptRepository(defaultReceiptDir());

    // Componentes da UI
    private JList<MenuItem> itemsList; // Usando o tipo raw para compatibilidade
    private DefaultListModel<MenuItem> itemsModel;
    private JSpinner qtySpinner;
    private JButton addBtn;
    private JTable cartTable;
    private CartTableModel cartTableModel;
    private JLabel subtotalLbl;
    private JLabel taxLbl;
    private JLabel totalLbl;

    public MainFrame() {
        super("Cafe Order Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);
        add(buildBottomBar(), BorderLayout.SOUTH);

        // Carrega os itens iniciais (ex: Drinks)
        loadItems(Category.DRINK);
        updateTotals();
    }

    private JComponent buildLeftPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        // Botões de Categoria
        var drinksBtn = new JButton("Drinks");
        drinksBtn.addActionListener(e -> loadItems(Category.DRINK));

        var bakeryBtn = new JButton("Bakery");
        bakeryBtn.addActionListener(e -> loadItems(Category.BAKERY));

        // Adicione outros botões se desejar, ex:
        var sandwichBtn = new JButton("Sandwiches");
        sandwichBtn.addActionListener(e -> loadItems(Category.SANDWITCH));

        panel.add(new JLabel("Categories"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(drinksBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(bakeryBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sandwichBtn);

        panel.setPreferredSize(new Dimension(180, 10));
        return panel;
    }

    private JComponent buildCenterPanel() {
        var root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        root.add(new JLabel("Items"), BorderLayout.NORTH);

        itemsModel = new DefaultListModel<>();
        itemsList = new JList<>(itemsModel);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsList.addListSelectionListener(e -> onItemSelected(e));

        // Renderer customizado para mostrar nome e preço do MenuItem
        itemsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                var c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MenuItem mi) {
                    setText(mi.getName() + " — " + mi.getPrice().toPlainString());
                }
                return c;
            }
        });

        root.add(new JScrollPane(itemsList), BorderLayout.CENTER);

        var foot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        foot.add(new JLabel("Qty:"));
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        foot.add(qtySpinner);

        addBtn = new JButton("Add to Cart");
        addBtn.addActionListener(e -> addSelectedToCart());
        addBtn.setEnabled(false);
        foot.add(addBtn);

        root.add(foot, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildRightPanel() {
        var panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        panel.add(new JLabel("Cart"), BorderLayout.NORTH);

        // Usa o CartTableModel com o seu 'Cart'
        cartTableModel = new CartTableModel(cart);
        cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);
        // Atualiza os totais sempre que a tabela do carrinho mudar
        cartTableModel.addTableModelListener(e -> updateTotals());

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        var btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> removeSelectedCartLine());
        var clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            cart.clear();
            cartTableModel.refresh();
            updateTotals();
        });

        btns.add(removeBtn);
        btns.add(clearBtn);
        panel.add(btns, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(420, 10));
        return panel;
    }

    private JComponent buildBottomBar() {
        var panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subtotalLbl = new JLabel("Subtotal: 0.00");
        taxLbl = new JLabel("Tax: 0.00");
        totalLbl = new JLabel("Total: 0.00");

        var checkoutBtn = new JButton("Checkout…");
        // Conecta o botão ao handler de checkout
        checkoutBtn.addActionListener(e -> handleCheckout());

        panel.add(subtotalLbl);
        panel.add(new JLabel("    "));
        panel.add(taxLbl);
        panel.add(new JLabel("    "));
        panel.add(totalLbl);
        panel.add(new JLabel("    "));
        panel.add(checkoutBtn);
        return panel;
    }

    // --- Métodos de Lógica e Eventos ---

    private void onItemSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            var selected = itemsList.getSelectedValue();
            addBtn.setEnabled(selected != null);
            if (selected != null) {
                qtySpinner.setValue(1);
            }
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
        // Usa o seu CatalogRepository
        List<MenuItem> all = catalogRepo.all(); //
        var list = all.stream()
                .filter(m -> m.getCategory() == category) //
                .collect(Collectors.toList());

        itemsModel.clear();
        for (var m : list) itemsModel.addElement(m);

        addBtn.setEnabled(false);
    }

    private void updateTotals() {
        // Usa os métodos do seu 'Cart' e 'TaxCalculator'
        var sub = cart.getSubtotal();
        var tax = cart.getTax(taxCalc); //
        var tot = cart.getTotal(taxCalc); //

        subtotalLbl.setText("Subtotal: " + sub.toPlainString());
        taxLbl.setText("Tax: " + tax.toPlainString());
        totalLbl.setText("Total: " + tot.toPlainString());
    }

    // --- Métodos de Checkout (Week 10) ---

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.", "Cannot checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter customer name:", "Checkout", JOptionPane.PLAIN_MESSAGE);
        if (name == null) return; // Cancelado
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
            // Usa o seu FileReceiptRepository
            var file = receiptRepository.save(receiptLines); //
            // Mostra o diálogo de recibo
            new ReceiptDialog(this, order, file).setVisible(true);
            receiptShown = true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save receipt: " + ex.getMessage(),
                    "Receipt Error", JOptionPane.ERROR_MESSAGE);
        }

        if (receiptShown) {
            // Limpa o carrinho após o checkout bem-sucedido
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

        // Cria o snapshot do Pedido (Order)
        return new Order(
                orderId,
                customerName,
                Instant.now(),
                cart.items(), //
                subtotal, tax, total
        );
    }

    private static Path defaultReceiptDir() {
        // Salva os recibos na pasta 'kiosk-receipts' dentro do home do usuário
        return Paths.get(System.getProperty("user.home"), "kiosk-receipts");
    }
}