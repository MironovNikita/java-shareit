package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0L;

    @Override
    public Item create(Item item) {
        long id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> get(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getBySearchText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return items.values()
                .stream()
                .filter(item -> isItemSearched(item, text))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return false;
        }
        items.remove(itemId);
        return true;
    }

    private long getId() {
        return ++id;
    }

    private boolean isItemSearched(Item item, String text) {
        boolean isNameSearched = item.getName().toLowerCase().contains(text.toLowerCase());
        boolean isDescriptionSearched = item.getDescription().toLowerCase().contains(text.toLowerCase());

        return item.getAvailable() && (isNameSearched || isDescriptionSearched);
    }

    public void resetData() {
        id = 1L;
        items.clear();
    }
}
