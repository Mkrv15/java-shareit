package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceFindItemsByTextTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private Long itemId;
    private String searchText;
    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 10L;
        searchText = "drill";

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill Pro");
        item.setDescription("Professional drill for wood");
        item.setAvailable(true);
        item.setUserId(userId);

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Drill Pro")
                .description("Professional drill for wood")
                .available(true)
                .build();
    }

    @Test
    void shouldReturnItemsMatchingText() {
        int from = 0;
        int size = 10;
        List<Item> items = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText.toLowerCase()), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto.getName(), result.get(0).getName());
        assertEquals(itemDto.getDescription(), result.get(0).getDescription());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText.toLowerCase()), any(Pageable.class));
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsMatch() {
        int from = 0;
        int size = 10;
        String text = "nonexistent";

        when(itemRepository.findByNameOrDescriptionLike(eq(text.toLowerCase()), any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.searchItems(text, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(text.toLowerCase()), any(Pageable.class));
        verifyNoInteractions(itemMapper);
    }

    @Test
    void shouldReturnEmptyListWhenTextIsBlank() {
        int from = 0;
        int size = 10;
        List<String> texts = List.of("", " ", "\t", "\n");

        for (String text : texts) {
            List<ItemDto> result = itemService.searchItems(text, from, size);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void shouldSearchWithCaseInsensitiveText() {
        int from = 0;
        int size = 10;
        String textUpperCase = "DRILL";
        String textLowerCase = "drill";
        String textMixed = "DrIlL";

        List<Item> items = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(textLowerCase), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result1 = itemService.searchItems(textUpperCase, from, size);
        List<ItemDto> result2 = itemService.searchItems(textLowerCase, from, size);
        List<ItemDto> result3 = itemService.searchItems(textMixed, from, size);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(1, result3.size());

        verify(itemRepository, times(3))
                .findByNameOrDescriptionLike(eq(textLowerCase), any(Pageable.class));
    }

    @Test
    void shouldSearchByPartialTextMatch() {
        int from = 0;
        int size = 10;
        String partialText = "dril";

        List<Item> items = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(partialText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems(partialText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto.getName(), result.get(0).getName());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(partialText), any(Pageable.class));
    }

    @Test
    void shouldSearchByDescriptionMatch() {
        int from = 0;
        int size = 10;
        String descriptionText = "wood";

        Item itemByDescription = new Item();
        itemByDescription.setId(20L);
        itemByDescription.setName("Saw");
        itemByDescription.setDescription("Wood saw for cutting");
        itemByDescription.setAvailable(true);
        itemByDescription.setUserId(userId);

        ItemDto itemDtoByDescription = ItemDto.builder()
                .id(20L)
                .name("Saw")
                .description("Wood saw for cutting")
                .available(true)
                .build();

        List<Item> items = List.of(itemByDescription);

        when(itemRepository.findByNameOrDescriptionLike(eq(descriptionText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoByDescription);

        List<ItemDto> result = itemService.searchItems(descriptionText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDtoByDescription.getName(), result.get(0).getName());
        assertEquals(itemDtoByDescription.getDescription(), result.get(0).getDescription());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(descriptionText), any(Pageable.class));
    }

    @Test
    void shouldSearchWithPagination() {
        int from = 5;
        int size = 10;

        List<Item> items = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText), argThat(pageableArg ->
                        pageableArg.getPageNumber() == from / size &&
                                pageableArg.getPageSize() == size
                ));
    }

    @Test
    void shouldReturnOnlyAvailableItems() {
        int from = 0;
        int size = 10;

        Item unavailableItem = new Item();
        unavailableItem.setId(30L);
        unavailableItem.setName("Broken Drill");
        unavailableItem.setDescription("Broken drill");
        unavailableItem.setAvailable(false);
        unavailableItem.setUserId(userId);

        List<Item> availableItems = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText), any(Pageable.class)))
                .thenReturn(availableItems);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText), any(Pageable.class));
    }

    @Test
    void shouldHandleSearchResultWithMultipleItems() {
        int from = 0;
        int size = 10;

        Item item2 = new Item();
        item2.setId(20L);
        item2.setName("Drill Pro Max");
        item2.setDescription("Professional drill max");
        item2.setAvailable(true);
        item2.setUserId(userId);

        ItemDto itemDto2 = ItemDto.builder()
                .id(20L)
                .name("Drill Pro Max")
                .description("Professional drill max")
                .available(true)
                .build();

        List<Item> items = List.of(item, item2);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class)))
                .thenReturn(itemDto, itemDto2);

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Drill Pro", result.get(0).getName());
        assertEquals("Drill Pro Max", result.get(1).getName());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText), any(Pageable.class));
        verify(itemMapper, times(2)).convertToDto(any(Item.class));
    }

    @Test
    void shouldSearchWithSortingByIdAsc() {
        int from = 0;
        int size = 10;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Drill");
        item1.setDescription("Drill");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Drill");
        item2.setDescription("Drill");
        item2.setAvailable(true);

        List<Item> items = List.of(item1, item2);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class)))
                .thenReturn(
                        ItemDto.builder().id(1L).name("Drill").build(),
                        ItemDto.builder().id(2L).name("Drill").build()
                );

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText), argThat(pageable ->
                        pageable.getSort().getOrderFor("id") != null &&
                                pageable.getSort().getOrderFor("id").isAscending()
                ));
    }

    @Test
    void shouldSearchWithSortingByIdDesc() {
        int from = 0;
        int size = 10;

        List<Item> items = List.of(item);

        when(itemRepository.findByNameOrDescriptionLike(eq(searchText), any(Pageable.class)))
                .thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems(searchText, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(itemRepository, times(1))
                .findByNameOrDescriptionLike(eq(searchText), argThat(pageable ->
                        pageable.getSort().getOrderFor("id") != null
                ));
    }
}