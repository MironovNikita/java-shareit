package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public Item create(long userId, ItemDtoCreate itemDtoCreate) {
        User user = userService.get(userId);
        Item item = itemMapper.transformItemDtoToItem(itemDtoCreate);
        item.setOwner(user);

        return itemRepository.create(item);
    }

    public Item update(long id, long userId, ItemDtoUpdate itemDtoUpdate) {
        User user = userService.get(userId);
        Item item = itemRepository.get(id).orElseThrow(() -> new ObjectNotFoundException("Предмет", id));

        if (!user.equals(item.getOwner())) {
            throw new ObjectNotFoundException("Предмет", id);
        }

        if (itemDtoUpdate.getName() != null) {
            item.setName(itemDtoUpdate.getName());
        }

        if (itemDtoUpdate.getDescription() != null) {
            item.setDescription(itemDtoUpdate.getDescription());
        }

        if (itemDtoUpdate.getAvailable() != null) {
            item.setAvailable(itemDtoUpdate.getAvailable());
        }

        return itemRepository.update(item);
    }

    public Item get(long id) {
        return itemRepository.get(id).orElseThrow(() -> new ObjectNotFoundException("Предмет", id));
    }

    public List<Item> getByUserId(long userId) {
        return itemRepository.getByUserId(userId);
    }

    public List<Item> getBySearchText(String text) {
        return itemRepository.getBySearchText(text);
    }

    public void delete(long id) {
        boolean isItemDeleted = itemRepository.delete(id);
        if (!isItemDeleted) {
            log.error("Предмет с идентификатором {} не найден!", id);
            throw new ObjectNotFoundException("Предмет", id);
        }
    }
}
