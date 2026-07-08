package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class BookingServiceIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User ownerItem;
    private User bookerUser;
    private User otherUser;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        ownerItem = userRepository.save(ownerItem);

        bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        bookerUser = userRepository.save(bookerUser);

        otherUser = new User();
        otherUser.setName("NameOther");
        otherUser.setEmail("NameOther@email");
        otherUser = userRepository.save(otherUser);

        item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setUserId(ownerItem.getId());
        item = itemRepository.save(item);
    }

    @Test
    void shouldCreateBookingToDataBase() {
        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(item.getId());
        bookingInputDto.setStart(now.plusMinutes(10));
        bookingInputDto.setEnd(now.plusMinutes(20));

        BookingDto bookingSaved = bookingService.addBooking(bookerUser.getId(), bookingInputDto);

        Booking result = bookingRepository.findById(bookingSaved.getId()).orElseThrow();
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(item.getName(), result.getItem().getName());
        assertEquals(item.getDescription(), result.getItem().getDescription());
        assertEquals(bookingInputDto.getStart(), result.getStart());
        assertEquals(bookingInputDto.getEnd(), result.getEnd());
        assertEquals(bookerUser.getId(), result.getBooker().getId());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void shouldApproveBookingToDataBase() {
        Booking booking = new Booking();
        booking.setStart(now.plusMinutes(10));
        booking.setEnd(now.plusMinutes(20));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(bookerUser);
        Booking bookingSaved = bookingRepository.save(booking);

        BookingDto result = bookingService.approveOrRejectBooking(
                ownerItem.getId(), bookingSaved.getId(), true, AccessLevel.OWNER);

        assertEquals(Status.APPROVED, result.getStatus());

        Booking bookingFromDb = bookingRepository.findById(bookingSaved.getId()).orElseThrow();
        assertEquals(Status.APPROVED, bookingFromDb.getStatus());
    }

    @Test
    void shouldRejectBookingToDataBase() {
        Booking booking = new Booking();
        booking.setStart(now.plusMinutes(10));
        booking.setEnd(now.plusMinutes(20));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(bookerUser);
        Booking bookingSaved = bookingRepository.save(booking);

        BookingDto result = bookingService.approveOrRejectBooking(
                ownerItem.getId(), bookingSaved.getId(), false, AccessLevel.OWNER);

        assertEquals(Status.REJECTED, result.getStatus());

        Booking bookingFromDb = bookingRepository.findById(bookingSaved.getId()).orElseThrow();
        assertEquals(Status.REJECTED, bookingFromDb.getStatus());
    }

    @Test
    void shouldGetBookingFromDataBase() {
        Booking booking = new Booking();
        booking.setStart(now.plusMinutes(10));
        booking.setEnd(now.plusMinutes(20));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(bookerUser);
        Booking bookingSaved = bookingRepository.save(booking);

        BookingDto result = bookingService.getBooking(
                bookingSaved.getId(), ownerItem.getId(), AccessLevel.OWNER_AND_BOOKER);

        assertNotNull(result);
        assertEquals(bookingSaved.getId(), result.getId());
        assertEquals(bookingSaved.getStart(), result.getStart());
        assertEquals(bookingSaved.getEnd(), result.getEnd());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void shouldGetBookingsByStateToBookerFromDataBase() {
        Booking bookingFirst = new Booking();
        bookingFirst.setStart(now.plusMinutes(10));
        bookingFirst.setEnd(now.plusMinutes(20));
        bookingFirst.setStatus(Status.WAITING);
        bookingFirst.setItem(item);
        bookingFirst.setBooker(bookerUser);
        Booking savedFirstBooking = bookingRepository.save(bookingFirst);

        Booking bookingSecond = new Booking();
        bookingSecond.setStart(now.plusMinutes(30));
        bookingSecond.setEnd(now.plusMinutes(40));
        bookingSecond.setStatus(Status.REJECTED);
        bookingSecond.setItem(item);
        bookingSecond.setBooker(bookerUser);
        Booking savedSecondBooking = bookingRepository.save(bookingSecond);

        Booking bookingThird = new Booking();
        bookingThird.setStart(now.plusMinutes(50));
        bookingThird.setEnd(now.plusMinutes(60));
        bookingThird.setStatus(Status.WAITING);
        bookingThird.setItem(item);
        bookingThird.setBooker(bookerUser);
        Booking savedThirdBooking = bookingRepository.save(bookingThird);

        Booking bookingFourth = new Booking();
        bookingFourth.setStart(now.plusMinutes(10));
        bookingFourth.setEnd(now.plusMinutes(20));
        bookingFourth.setStatus(Status.WAITING);
        bookingFourth.setItem(item);
        bookingFourth.setBooker(otherUser);
        bookingRepository.save(bookingFourth);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(
                State.WAITING, bookerUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(savedThirdBooking.getId(), result.get(0).getId());
        assertEquals(savedFirstBooking.getId(), result.get(1).getId());
    }

    @Test
    void shouldGetAllBookingsForBooker() {
        Booking booking1 = new Booking();
        booking1.setStart(now.plusMinutes(10));
        booking1.setEnd(now.plusMinutes(20));
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setBooker(bookerUser);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusMinutes(30));
        booking2.setEnd(now.plusMinutes(40));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(bookerUser);
        bookingRepository.save(booking2);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(
                State.ALL, bookerUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldGetAllBookingsForOwner() {
        Booking booking1 = new Booking();
        booking1.setStart(now.plusMinutes(10));
        booking1.setEnd(now.plusMinutes(20));
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setBooker(bookerUser);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusMinutes(30));
        booking2.setEnd(now.plusMinutes(40));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(otherUser);
        bookingRepository.save(booking2);

        List<BookingDto> result = bookingService.getBookingsOfOwner(
                State.ALL, ownerItem.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldGetPastBookingsForBooker() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(bookerUser);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setStatus(Status.WAITING);
        futureBooking.setItem(item);
        futureBooking.setBooker(bookerUser);
        bookingRepository.save(futureBooking);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(
                State.PAST, bookerUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void shouldGetCurrentBookingsForBooker() {
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(bookerUser);
        bookingRepository.save(currentBooking);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(
                State.CURRENT, bookerUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void shouldGetFutureBookingsForBooker() {
        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(3));
        futureBooking.setStatus(Status.WAITING);
        futureBooking.setItem(item);
        futureBooking.setBooker(bookerUser);
        bookingRepository.save(futureBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(5));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(bookerUser);
        bookingRepository.save(pastBooking);

        List<BookingDto> result = bookingService.getBookingsOfCurrentUser(
                State.FUTURE, bookerUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }
}