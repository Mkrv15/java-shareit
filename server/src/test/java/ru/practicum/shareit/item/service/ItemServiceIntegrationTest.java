package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class ItemServiceIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private User otherUser;
    private Item item;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        otherUser = new User();
        otherUser.setName("OtherUser");
        otherUser.setEmail("other@email.com");
        otherUser = userRepository.save(otherUser);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Professional drill");
        item.setAvailable(true);
        item.setUserId(owner.getId());
        item = itemRepository.save(item);
    }

    @Test
    void shouldFindAllItemsUserFromDataBase() {
        Item itemSecond = new Item();
        itemSecond.setName("Hammer");
        itemSecond.setDescription("Heavy hammer");
        itemSecond.setAvailable(false);
        itemSecond.setUserId(owner.getId());
        Item savedSecondItem = itemRepository.save(itemSecond);

        Item itemThird = new Item();
        itemThird.setName("Saw");
        itemThird.setDescription("Wood saw");
        itemThird.setAvailable(true);
        itemThird.setUserId(otherUser.getId());
        itemRepository.save(itemThird);

        List<ItemDto> result = itemService.getAllItems(owner.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().allMatch(dto ->
                dto.getName().equals("Drill") || dto.getName().equals("Hammer")
        ));

        ItemDto firstItem = result.stream().filter(dto -> dto.getName().equals("Drill")).findFirst().orElseThrow();
        ItemDto secondItem = result.stream().filter(dto -> dto.getName().equals("Hammer")).findFirst().orElseThrow();
        assertTrue(firstItem.getAvailable());
        assertFalse(secondItem.getAvailable());
    }

    @Test
    void shouldAddNewItem() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(booker);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        ItemDto itemDto = ItemDto.builder()
                .name("New Drill")
                .description("Brand new drill")
                .available(true)
                .requestId(savedRequest.getId())
                .build();

        ItemDto result = itemService.addItem(owner.getId(), itemDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());

        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(owner.getId(), savedItem.getUserId());
        assertEquals(savedRequest.getId(), savedItem.getRequestId());
    }

    @Test
    void shouldAddNewItemWithoutRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .name("Simple Drill")
                .description("Simple drill")
                .available(true)
                .build();

        ItemDto result = itemService.addItem(owner.getId(), itemDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNull(result.getRequestId());

        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertNull(savedItem.getRequestId());
    }

    @Test
    void shouldDeleteItemFromDataBaseById() {
        Long itemId = item.getId();

        itemService.removeItem(owner.getId(), itemId);

        assertFalse(itemRepository.findById(itemId).isPresent());
    }

    @Test
    void shouldFindItemWithBookingsAndCommentsByIdToOwnerFromDataBase() {
        LocalDateTime now = LocalDateTime.now();

        Booking bookingPast = new Booking();
        bookingPast.setStatus(Status.APPROVED);
        bookingPast.setItem(item);
        bookingPast.setBooker(booker);
        bookingPast.setStart(now.minusDays(5));
        bookingPast.setEnd(now.minusDays(1));
        bookingPast = bookingRepository.save(bookingPast);

        Booking bookingFuture = new Booking();
        bookingFuture.setStatus(Status.APPROVED);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(booker);
        bookingFuture.setStart(now.plusDays(1));
        bookingFuture.setEnd(now.plusDays(3));
        bookingFuture = bookingRepository.save(bookingFuture);

        Comment comment1 = new Comment();
        comment1.setItem(item);
        comment1.setText("Great item!");
        comment1.setAuthor(booker);
        comment1.setCreated(now.minusHours(1));
        comment1 = commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setItem(item);
        comment2.setText("Very useful!");
        comment2.setAuthor(booker);
        comment2.setCreated(now.minusMinutes(30));
        comment2 = commentRepository.save(comment2);

        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertTrue(result.getAvailable());

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());

        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());
        assertTrue(result.getComments().stream().anyMatch(c -> c.getText().equals("Great item!")));
        assertTrue(result.getComments().stream().anyMatch(c -> c.getText().equals("Very useful!")));
    }

    @Test
    void shouldFindItemByTextFromDataBase() {
        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer for construction");
        item2.setAvailable(true);
        item2.setUserId(owner.getId());
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Saw");
        item3.setDescription("Wood saw");
        item3.setAvailable(true);
        item3.setUserId(owner.getId());
        itemRepository.save(item3);

        Item item4 = new Item();
        item4.setName("Wrench");
        item4.setDescription("Adjustable wrench");
        item4.setAvailable(false);
        item4.setUserId(owner.getId());
        itemRepository.save(item4);

        List<ItemDto> result = itemService.searchItems("drill", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());

        List<ItemDto> resultByDescription = itemService.searchItems("construction", 0, 10);
        assertEquals(1, resultByDescription.size());
        assertEquals("Hammer", resultByDescription.get(0).getName());

        List<ItemDto> resultUnavailable = itemService.searchItems("wrench", 0, 10);
        assertTrue(resultUnavailable.isEmpty());
    }

    @Test
    void shouldUpdateItemInDataBase() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Drill")
                .description("Updated professional drill")
                .available(false)
                .build();

        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getAvailable(), result.getAvailable());

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(updateDto.getName(), updatedItem.getName());
        assertEquals(updateDto.getDescription(), updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void shouldUpdateItemPartially() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name Only")
                .build();

        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertTrue(result.getAvailable());

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals("Updated Name Only", updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void shouldAddCommentToDataBase() {
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.minusDays(5));
        booking.setEnd(now.minusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Excellent drill!");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(booker.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());

        Comment savedComment = commentRepository.findById(result.getId()).orElseThrow();
        assertEquals(commentDto.getText(), savedComment.getText());
        assertEquals(booker.getId(), savedComment.getAuthor().getId());
        assertEquals(item.getId(), savedComment.getItem().getId());
    }

    @Test
    void shouldFindItemWithoutBookingsAndComments() {
        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void shouldFindItemsWithPagination() {
        for (int i = 1; i <= 15; i++) {
            Item newItem = new Item();
            newItem.setName("Item " + i);
            newItem.setDescription("Description " + i);
            newItem.setAvailable(true);
            newItem.setUserId(owner.getId());
            itemRepository.save(newItem);
        }

        List<ItemDto> result = itemService.getAllItems(owner.getId(), 0, 5);

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerForUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .build();

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), item.getId(), updateDto));
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwnerForDelete() {
        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemService.removeItem(booker.getId(), item.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenTextIsBlank() {
        List<ItemDto> result = itemService.searchItems("", 0, 10);
        List<ItemDto> result2 = itemService.searchItems("   ", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }
}