package com.arthur.marchetti;

import com.arthur.marchetti.interfaces.TaxCalculator.CatalogRepository;
import com.arthur.marchetti.model.Cart;
import com.arthur.marchetti.model.FlatRateTaxCalculator;
import com.arthur.marchetti.model.MenuItem;
import com.arthur.marchetti.repo.InMemoryCatalogRepository;
import com.arthur.marchetti.services.ReceiptService;

import java.math.BigDecimal;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args){
        CatalogRepository repo = new InMemoryCatalogRepository();
        Cart cart = new Cart();
        ReceiptService receipt = new ReceiptService(new FlatRateTaxCalculator(new BigDecimal("0.06")));
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to Order Kiosk CLI");
        printHelp();

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            String cmd = parts[0].toLowerCase();
            try {
                switch (cmd) {
                    case "menu" -> repo.all().forEach(mi -> System.out.println(mi.getName() + " " + mi.getPrice()));
                    case "add" -> {
                        // add Coffee 2
                        String name = parts[1];
                        int qty = Integer.parseInt(parts[2]);
                        MenuItem mi = repo.all().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("unknown item"));
                        cart.add(mi, qty);
                        System.out.println("Added " + qty + " x " + mi.getName());
                    }
                    case "remove" -> { cart.remove(parts[1]); System.out.println("Removed " + parts[1]); }
                    case "show" -> cart.items().forEach(ci -> System.out.println(ci.getItem().getName() + " x" + ci.getQuantity() + " = " + ci.lineTotal()));
                    case "receipt" -> receipt.render(cart).forEach(System.out::println);
                    case "help" -> printHelp();
                    case "quit" -> { System.out.println("Bye"); return; }
                    default -> System.out.println("Unknown command, type help");
                }
            } catch (Exception e){ System.out.println("Error, " + e.getMessage()); }
        }
    }

    private static void printHelp(){
        System.out.println("Commands, menu | add <Name> <Qty> | remove <Name> | show | receipt | quit");
    }
}