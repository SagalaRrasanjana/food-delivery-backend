package com.fooddelivery.restaurant_service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fooddelivery.restaurant_service.model.MenuItem;

public class MenuItemSpecifications {

    private MenuItemSpecifications() {
    }

    public static Specification<MenuItem> forRestaurant(Long restaurantId) {
        return (root, query, cb) -> cb.equal(root.get("restaurant").get("id"), restaurantId);
    }

    public static Specification<MenuItem> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("menuCategory")), category.toLowerCase());
    }

    public static Specification<MenuItem> isAvailable(Boolean available) {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), available);
    }

    public static Specification<MenuItem> build(Long restaurantId, String category, Boolean available) {
        List<Specification<MenuItem>> specs = new ArrayList<>();
        specs.add(forRestaurant(restaurantId));
        if (category != null && !category.isBlank()) {
            specs.add(hasCategory(category));
        }
        if (available != null) {
            specs.add(isAvailable(available));
        }
        return Specification.allOf(specs);
    }
}
