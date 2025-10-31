package com.arthur.marchetti.interfaces.TaxCalculator;
import com.arthur.marchetti.model.Category;
import com.arthur.marchetti.model.MenuItem;

import java.util.List;

public interface CatalogRepository {
    List<MenuItem> all();
    List<MenuItem> byCategory(Category cat);
}