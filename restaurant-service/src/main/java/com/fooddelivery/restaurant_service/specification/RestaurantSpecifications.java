package com.fooddelivery.restaurant_service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fooddelivery.restaurant_service.model.Restaurant;

public class RestaurantSpecifications {

    private RestaurantSpecifications() {
    }

    public static Specification<Restaurant> search(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("address")), pattern));
        };
    }

    public static Specification<Restaurant> hasTag(String tag) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("tags")), "%" + tag.toLowerCase() + "%");
    }

    public static Specification<Restaurant> isOpen(Boolean open) {
        return (root, query, cb) -> cb.equal(root.get("isOpen"), open);
    }

    public static Specification<Restaurant> hasOwnerId(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Restaurant> build(String search, String tag, Boolean open, Long ownerId) {
        List<Specification<Restaurant>> specs = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            specs.add(search(search));
        }
        if (tag != null && !tag.isBlank()) {
            specs.add(hasTag(tag));
        }
        if (open != null) {
            specs.add(isOpen(open));
        }
        if (ownerId != null) {
            specs.add(hasOwnerId(ownerId));
        }
        return Specification.allOf(specs);
    }
}
