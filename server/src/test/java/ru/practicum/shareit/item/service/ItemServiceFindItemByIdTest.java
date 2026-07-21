package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceFindItemByIdTest {

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
    private Long itemId;
    private Long nonOwnerId;
    private User user;
    private User nonOwner;
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
        itemId = 10L;
        nonOwnerId = 99L;

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("user@example.com");

        nonOwner = new User();
        nonOwner.setId(nonOwnerId);
        nonOwner.setName("Non Owner");
        nonOwner.setEmail("nonowner@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(userId);

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .build();

        comment = new Comment();
        comment.setId(100L);
        comment.setText("Great item!");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(100L);
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

    @Test
    void shouldReturnItemWhenExists() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemMapper, times(1)).convertToDto(any(Item.class));
        verify(bookingRepository, times(1))
                .findByItemIdAndStatus(eq(itemId), eq(Status.APPROVED), any(Sort.class));
        verify(commentRepository, times(1))
                .findAllByItemId(eq(itemId), any(Sort.class));
    }

    @Test
    void shouldThrowNotFoundWhenItemNotFound() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(itemId, userId)
        );

        assertEquals("Вещь с id " + itemId + " не найдена", exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(itemId, userId)
        );

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldIncludeLastAndNextBookingForOwner() {
        List<Booking> bookings = List.of(bookingLast, bookingNext);
        ItemDto itemDtoWithBookings = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .lastBooking(bookingDtoShortLast)
                .nextBooking(bookingDtoShortNext)
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithBookings);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(bookings);
        when(bookingMapper.convertToDtoShort(any(Booking.class)))
                .thenReturn(bookingDtoShortLast, bookingDtoShortNext);
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(bookingDtoShortLast.getId(), result.getLastBooking().getId());
        assertEquals(bookingDtoShortNext.getId(), result.getNextBooking().getId());

        verify(bookingRepository, times(1))
                .findByItemIdAndStatus(eq(itemId), eq(Status.APPROVED), any(Sort.class));
        verify(bookingMapper, times(2)).convertToDtoShort(any(Booking.class));
    }

    @Test
    void shouldNotIncludeBookingsForNonOwner() {
        List<Booking> bookings = List.of(bookingLast, bookingNext);

        when(userService.getUserById(nonOwnerId)).thenReturn(nonOwner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(bookings);
        when(bookingMapper.convertToDtoShort(any(Booking.class)))
                .thenReturn(bookingDtoShortLast, bookingDtoShortNext);
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(itemId, nonOwnerId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        verify(bookingRepository, times(1))
                .findByItemIdAndStatus(eq(itemId), eq(Status.APPROVED), any(Sort.class));
        verify(bookingMapper, times(2)).convertToDtoShort(any(Booking.class));
    }

    @Test
    void shouldIncludeCommentsForItem() {
        List<Comment> comments = List.of(comment);

        ItemDto itemDtoWithComments = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .comments(List.of(commentDto))
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithComments);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(comments);
        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(commentDto);

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());

        verify(commentRepository, times(1))
                .findAllByItemId(eq(itemId), any(Sort.class));
        verify(commentMapper, times(1)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldReturnItemWithoutComments() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());

        verify(commentRepository, times(1))
                .findAllByItemId(eq(itemId), any(Sort.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldHandleMultipleComments() {
        Comment comment2 = new Comment();
        comment2.setId(200L);
        comment2.setText("Awesome!");
        comment2.setAuthor(user);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(200L);
        commentDto2.setText("Awesome!");
        commentDto2.setAuthorName("Test User");
        commentDto2.setCreated(LocalDateTime.now());

        List<Comment> comments = List.of(comment, comment2);

        ItemDto itemDtoWithComments = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .comments(List.of(commentDto, commentDto2))
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDtoWithComments);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(comments);
        when(commentMapper.convertToDto(any(Comment.class)))
                .thenReturn(commentDto, commentDto2);

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());

        verify(commentRepository, times(1))
                .findAllByItemId(eq(itemId), any(Sort.class));
        verify(commentMapper, times(2)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldCallFindAllByItemIdWithCorrectSort() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(any(Item.class))).thenReturn(itemDto);
        when(bookingRepository.findByItemIdAndStatus(anyLong(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of());

        itemService.getItemById(itemId, userId);

        verify(commentRepository).findAllByItemId(eq(itemId), argThat(sort ->
                sort.getOrderFor("created") != null &&
                        sort.getOrderFor("created").isAscending()
        ));
    }
}