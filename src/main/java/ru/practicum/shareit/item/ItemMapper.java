package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item transformItemDtoToItem(ItemDto itemDto);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto transformItemToItemDto(Item item);
}
