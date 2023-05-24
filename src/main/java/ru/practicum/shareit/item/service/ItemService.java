package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
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

    public Item create(long userId, ItemDto itemDto) {
        User user = userService.get(userId);
        Item item = itemMapper.transformItemDtoToItem(itemDto);
        item.setOwner(user);

        return itemRepository.create(item);
    }

    public Item update(long id, long userId, ItemDto itemDto) {
        User user = userService.get(userId);
        Item item = itemRepository.get(id).orElseThrow(() -> new ObjectNotFoundException("Предмет", id));

        if (!user.equals(item.getOwner())) {
            throw new ObjectNotFoundException("Предмет", id);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
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
