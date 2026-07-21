package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceFindAllItemsUserTest {

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

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private Long ownerId;
    private Long itemId;
    private Long commentId;
    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Booking bookingLast;
    private Booking bookingNext;
    private BookingDtoShort bookingDtoShortLast;
    private BookingDtoShort bookingDtoShortNext;

    @BeforeEach
    void setUp() {
        userId = 1L;
        ownerId = 2L;
        itemId = 10L;
        commentId = 100L;

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("user@example.com");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(ownerId);

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .build();

        comment = new Comment();
        comment.setId(commentId);
        comment.setText("Great item!");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());

        bookingLast = new Booking();
        bookingLast.setId(1L);
        bookingLast.setStart(LocalDateTime.now().minusDays(5));
        bookingLast.setEnd(LocalDateTime.now().minusDays(1));
        bookingLast.setStatus(Status.APPROVED);
        bookingLast.setItem(item);

        bookingNext = new Booking();
        bookingNext.setId(2L);
        bookingNext.setStart(LocalDateTime.now().plusDays(1));
        bookingNext.setEnd(LocalDateTime.now().plusDays(3));
        bookingNext.setStatus(Status.APPROVED);
        bookingNext.setItem(item);

        bookingDtoShortLast = new BookingDtoShort();
        bookingDtoShortLast.setId(1L);
        bookingDtoShortLast.setStart(LocalDateTime.now().minusDays(5));
        bookingDtoShortLast.setEnd(LocalDateTime.now().minusDays(1));
        bookingDtoShortLast.setItem(itemDto);

        bookingDtoShortNext = new BookingDtoShort();
        bookingDtoShortNext.setId(2L);
        bookingDtoShortNext.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoShortNext.setEnd(LocalDateTime.now().plusDays(3));
        bookingDtoShortNext.setItem(itemDto);
    }

    private Pageable createPageable(int from, int size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    void shouldReturnItemsForUserWhenExists() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);
        List<Item> items = List.of(item);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByUserIdOrderById(userId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto.getName(), result.get(0).getName());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(userId, pageable);
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoItems() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByUserIdOrderById(userId, pageable)).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(userId, pageable);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {
        int from = 0;
        int size = 10;

        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getAllItems(userId, from, size)
        );

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldIncludeLastAndNextBookingForOwner() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);
        List<Item> items = List.of(item);
        List<Booking> bookings = List.of(bookingLast, bookingNext);

        ItemDto itemDtoWithBookings = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .lastBooking(bookingDtoShortLast)
                .nextBooking(bookingDtoShortNext)
                .build();

        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllByUserIdOrderById(ownerId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithBookings);
        when(bookingRepository.findAllByOwnerId(ownerId, pageable)).thenReturn(bookings);
        when(bookingMapper.convertToDtoShort(any(Booking.class)))
                .thenReturn(bookingDtoShortLast, bookingDtoShortNext);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getLastBooking());
        assertNotNull(result.get(0).getNextBooking());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(ownerId, pageable);
        verify(bookingRepository, times(1)).findAllByOwnerId(ownerId, pageable);
        verify(bookingMapper, times(2)).convertToDtoShort(any(Booking.class));
    }

    @Test
    void shouldIncludeCommentsForItems() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);
        List<Item> items = List.of(item);
        List<Comment> comments = List.of(comment);

        ItemDto itemDtoWithComments = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .comments(List.of(commentDto))
                .build();

        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllByUserIdOrderById(ownerId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithComments);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(comments);
        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(commentDto);
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getComments());
        assertEquals(1, result.get(0).getComments().size());
        assertEquals(commentDto.getText(), result.get(0).getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.get(0).getComments().get(0).getAuthorName());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(ownerId, pageable);
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList(), any(Sort.class));
        verify(commentMapper, times(1)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldNotIncludeBookingsForNonOwner() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);
        List<Item> items = List.of(item);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByUserIdOrderById(userId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLastBooking());
        assertNull(result.get(0).getNextBooking());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(userId, pageable);
        verify(bookingRepository, times(1)).findAllByOwnerId(userId, pageable);
    }

    @Test
    void shouldHandleItemsWithoutBookingsAndComments() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);
        List<Item> items = List.of(item);

        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.findAllByUserIdOrderById(ownerId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLastBooking());
        assertNull(result.get(0).getNextBooking());
        assertNotNull(result.get(0).getComments());
        assertTrue(result.get(0).getComments().isEmpty());

        verify(userService, times(1)).getUserById(ownerId);
        verify(itemRepository, times(1)).findAllByUserIdOrderById(ownerId, pageable);
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList(), any(Sort.class));
        verify(bookingRepository, times(1)).findAllByOwnerId(ownerId, pageable);
    }

    @Test
    void shouldReturnItemsWithCorrectPagination() {
        int from = 5;
        int size = 10;
        Pageable pageable = createPageable(from, size);

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByUserIdOrderById(userId, pageable)).thenReturn(List.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(itemRepository, times(1)).findAllByUserIdOrderById(userId, pageable);
    }

    @Test
    void shouldHandleMultipleItems() {
        int from = 0;
        int size = 10;
        Pageable pageable = createPageable(from, size);

        Item item2 = new Item();
        item2.setId(20L);
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setUserId(userId);

        List<Item> items = List.of(item, item2);

        ItemDto itemDto2 = ItemDto.builder()
                .id(20L)
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findAllByUserIdOrderById(userId, pageable)).thenReturn(items);
        when(itemMapper.convertToDto(item)).thenReturn(itemDto);
        when(itemMapper.convertToDto(item2)).thenReturn(itemDto2);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class))).thenReturn(List.of());
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Drill", result.get(0).getName());
        assertEquals("Hammer", result.get(1).getName());

        verify(itemRepository, times(1)).findAllByUserIdOrderById(userId, pageable);
        verify(itemMapper, times(2)).convertToDto(any(Item.class));
    }
}