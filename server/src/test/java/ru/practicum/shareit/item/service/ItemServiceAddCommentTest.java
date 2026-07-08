package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentAccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
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
class ItemServiceAddCommentTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private Long itemId;
    private Long userId;
    private Long commentId;

    @BeforeEach
    void setUp() {
        itemId = 1L;
        userId = 2L;
        commentId = 3L;

        user = new User();
        user.setId(userId);
        user.setName("User Name");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(1L);

        comment = new Comment();
        comment.setId(commentId);
        comment.setText("Great item!");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("User Name");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void shouldCreateCommentWhenUserHasBookedItem() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.of(List.of(booking)));
        when(commentMapper.convertFromDto(any(CommentDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getAuthorName(), result.getAuthorName());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1))
                .findAllByItemIdAndBookerIdAndStatus(eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class));
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowCommentAccessDeniedWhenUserHasNotBookedItem() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.empty());

        CommentAccessDeniedException exception = assertThrows(
                CommentAccessDeniedException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Пользователь с id " + userId + " не арендовал вещь с id " + itemId + ".",
                exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1))
                .findAllByItemIdAndBookerIdAndStatus(eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class));
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowCommentAccessDeniedWhenBookingNotCompleted() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.of(List.of(booking)));

        CommentAccessDeniedException exception = assertThrows(
                CommentAccessDeniedException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Пользователь с id " + userId + " не может оставлять комментарии вещи с id " + itemId + ", так как бронирование еще не завершено.",
                exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1))
                .findAllByItemIdAndBookerIdAndStatus(eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class));
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Вещь с id " + itemId + " не найдена", exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден"));
        when(commentMapper.convertFromDto(any(CommentDto.class))).thenReturn(comment);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Пользователь с id " + userId + " не найден", exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundDuringCommentSave() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldCreateCommentWithCorrectTimestamp() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.of(List.of(booking)));
        when(commentMapper.convertFromDto(any(CommentDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setCreated(LocalDateTime.now());
            return savedComment;
        });
        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertNotNull(result);
        assertNotNull(result.getCreated());

        verify(userService, times(1)).getUserById(userId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, times(1)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldUseCorrectAuthorNameInComment() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.of(List.of(booking)));
        when(commentMapper.convertFromDto(any(CommentDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.convertToDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertNotNull(result);
        assertEquals("User Name", result.getAuthorName());
        assertEquals("Great item!", result.getText());

        verify(userService, times(1)).getUserById(userId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, times(1)).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowCommentAccessDeniedWhenBookingIsRejected() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.REJECTED);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.empty());

        CommentAccessDeniedException exception = assertThrows(
                CommentAccessDeniedException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Пользователь с id " + userId + " не арендовал вещь с id " + itemId + ".",
                exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1))
                .findAllByItemIdAndBookerIdAndStatus(eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class));
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }

    @Test
    void shouldThrowCommentAccessDeniedWhenBookingIsWaiting() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatus(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(Optional.empty());

        CommentAccessDeniedException exception = assertThrows(
                CommentAccessDeniedException.class,
                () -> itemService.addComment(userId, itemId, commentDto)
        );

        assertEquals("Пользователь с id " + userId + " не арендовал вещь с id " + itemId + ".",
                exception.getMessage());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1))
                .findAllByItemIdAndBookerIdAndStatus(eq(itemId), eq(userId), eq(Status.APPROVED), any(Sort.class));
        verifyNoInteractions(commentRepository);
        verify(commentMapper, times(1)).convertFromDto(any(CommentDto.class));
        verify(commentMapper, never()).convertToDto(any(Comment.class));
    }
}