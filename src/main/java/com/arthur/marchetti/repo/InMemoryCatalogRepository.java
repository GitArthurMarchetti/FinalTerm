package com.arthur.marchetti.repo;

import com.arthur.marchetti.interfaces.TaxCalculator.CatalogRepository;
import com.arthur.marchetti.model.Category;
import com.arthur.marchetti.model.MenuItem;

import java.math.BigDecimal;
import java.util.List;

public class InMemoryCatalogRepository implements CatalogRepository {

    private List<MenuItem> data = List.of(
            new MenuItem("Coffee", new BigDecimal("3.00"), Category.DRINK),
            new MenuItem("Tea", new BigDecimal("2.50"), Category.DRINK),
            new MenuItem("Croissant", new BigDecimal("4.25"), Category.BAKERY),
            new MenuItem("Salad", new BigDecimal("9.50"), Category.MEAL),
            new MenuItem("Club Sandwitch", new BigDecimal("9.50"), Category.SANDWITCH),
            new MenuItem("Cuban sandwich", new BigDecimal("10.50"), Category.SANDWITCH),
            new MenuItem("Pesto focaccia sandwich", new BigDecimal("13.50"), Category.SANDWITCH),
            new MenuItem("Egg sandwich", new BigDecimal("6.50"), Category.SANDWITCH)
            );

    @Override public List<MenuItem> all(){
        return data;
    }

    @Override
    public List<MenuItem> byCategory(Category cat){
     return data.stream().filter(d -> d.getCategory() == cat).toList();
    }
}