package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveRequestToDatabase() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("DescriptionUser");

        ItemRequestDtoResponse itemRequestDtoResponseSaved =
                itemRequestService.createItemRequest(itemRequestDto, savedUser.getId());

        assertNotNull(itemRequestDtoResponseSaved.getId());
        assertNotNull(itemRequestDtoResponseSaved.getCreated());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoResponseSaved.getDescription());

        ItemRequest itemRequestFromDb = itemRequestRepository.findById(itemRequestDtoResponseSaved.getId())
                .orElseThrow();
        assertEquals(itemRequestDto.getDescription(), itemRequestFromDb.getDescription());
        assertEquals(savedUser.getId(), itemRequestFromDb.getRequester().getId());
    }

    @Test
    void shouldCreateRequestWithCurrentTimestamp() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test Description");

        ItemRequestDtoResponse response = itemRequestService.createItemRequest(itemRequestDto, savedUser.getId());

        assertNotNull(response.getCreated());
        assertTrue(response.getCreated().isBefore(java.time.LocalDateTime.now().plusSeconds(1)));
        assertTrue(response.getCreated().isAfter(java.time.LocalDateTime.now().minusSeconds(5)));
    }


    @Test
    void shouldFindRequestsByUserIdFromDatabase() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequestFirst = new ItemRequest();
        itemRequestFirst.setDescription("DescriptionRequestFirst");
        itemRequestFirst.setRequester(savedUser);
        itemRequestFirst.setCreated(java.time.LocalDateTime.now().minusMinutes(5));

        ItemRequest itemRequestSecond = new ItemRequest();
        itemRequestSecond.setDescription("DescriptionRequestSecond");
        itemRequestSecond.setRequester(savedUser);
        itemRequestSecond.setCreated(java.time.LocalDateTime.now());

        ItemRequest itemRequestFirstSaved = itemRequestRepository.save(itemRequestFirst);
        ItemRequest itemRequestSecondSaved = itemRequestRepository.save(itemRequestSecond);

        List<RequestDtoResponseWithMD> itemRequestsFromDb =
                itemRequestService.getPrivateRequests(savedUser.getId(), 0, 10);

        assertNotNull(itemRequestsFromDb);
        assertEquals(2, itemRequestsFromDb.size());

        assertEquals(itemRequestSecondSaved.getId(), itemRequestsFromDb.get(0).getId());
        assertEquals(itemRequestFirstSaved.getId(), itemRequestsFromDb.get(1).getId());

        assertEquals(itemRequestSecond.getDescription(), itemRequestsFromDb.get(0).getDescription());
        assertEquals(itemRequestFirst.getDescription(), itemRequestsFromDb.get(1).getDescription());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoRequests() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getPrivateRequests(savedUser.getId(), 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnRequestsWithItems() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("DescriptionRequest");
        itemRequest.setRequester(savedUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setRequestId(savedItemRequest.getId());
        item.setUserId(savedUser.getId());
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getPrivateRequests(savedUser.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(savedItemRequest.getId(), result.get(0).getId());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(savedItem.getId(), result.get(0).getItems().get(0).getId());
        assertEquals("NameItem", result.get(0).getItems().get(0).getName());
    }

    @Test
    void shouldFindAllRequestsFromDatabase() {
        User user1 = new User();
        user1.setName("NameUser1");
        user1.setEmail("EmailUser1@email");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("NameUser2");
        user2.setEmail("EmailUser2@email");
        User savedUser2 = userRepository.save(user2);

        ItemRequest itemRequestFirst = new ItemRequest();
        itemRequestFirst.setDescription("DescriptionRequestFirst");
        itemRequestFirst.setRequester(savedUser1);

        ItemRequest itemRequestSecond = new ItemRequest();
        itemRequestSecond.setDescription("DescriptionRequestSecond");
        itemRequestSecond.setRequester(savedUser2);

        ItemRequest itemRequestFirstSaved = itemRequestRepository.save(itemRequestFirst);
        ItemRequest itemRequestSecondSaved = itemRequestRepository.save(itemRequestSecond);

        List<RequestDtoResponseWithMD> itemRequestsFromDb =
                itemRequestService.getOtherRequests(savedUser2.getId(), 0, 10);

        assertNotNull(itemRequestsFromDb);
        assertEquals(1, itemRequestsFromDb.size());

        assertEquals(itemRequestFirstSaved.getId(), itemRequestsFromDb.get(0).getId());
        assertEquals(itemRequestFirst.getDescription(), itemRequestsFromDb.get(0).getDescription());
    }

    @Test
    void shouldNotReturnOwnRequestsInOtherRequests() {
        User user1 = new User();
        user1.setName("NameUser1");
        user1.setEmail("EmailUser1@email");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("NameUser2");
        user2.setEmail("EmailUser2@email");
        User savedUser2 = userRepository.save(user2);

        ItemRequest itemRequestOwn = new ItemRequest();
        itemRequestOwn.setDescription("Own Request");
        itemRequestOwn.setRequester(savedUser2);
        itemRequestRepository.save(itemRequestOwn);

        ItemRequest itemRequestOther = new ItemRequest();
        itemRequestOther.setDescription("Other Request");
        itemRequestOther.setRequester(savedUser1);
        itemRequestRepository.save(itemRequestOther);

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getOtherRequests(savedUser2.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Other Request", result.get(0).getDescription());
        assertNotEquals("Own Request", result.get(0).getDescription());
    }

    @Test
    void shouldReturnEmptyListWhenNoOtherRequests() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Own Request");
        itemRequest.setRequester(savedUser);
        itemRequestRepository.save(itemRequest);

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getOtherRequests(savedUser.getId(), 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldFindRequestByIdFromDatabaseAndCheckItemRequestResponseDto() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("DescriptionRequest");
        itemRequest.setRequester(savedUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setRequestId(savedItemRequest.getId());
        item.setUserId(savedUser.getId());
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        RequestDtoResponseWithMD result =
                itemRequestService.getItemRequest(savedUser.getId(), savedItemRequest.getId());

        assertNotNull(result);
        assertEquals(savedItemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(savedItem.getId(), result.getItems().get(0).getId());
        assertEquals("NameItem", result.getItems().get(0).getName());
        assertEquals("DescriptionItem", result.getItems().get(0).getDescription());
        assertTrue(result.getItems().get(0).getAvailable());
        assertEquals(savedItemRequest.getId(), result.getItems().get(0).getRequestId());
    }

    @Test
    void shouldFindRequestByIdWithoutItems() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("DescriptionRequest");
        itemRequest.setRequester(savedUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        RequestDtoResponseWithMD result =
                itemRequestService.getItemRequest(savedUser.getId(), savedItemRequest.getId());

        assertNotNull(result);
        assertEquals(savedItemRequest.getId(), result.getId());
        assertEquals("DescriptionRequest", result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void shouldFindRequestByIdWithMultipleItems() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request with multiple items");
        itemRequest.setRequester(savedUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setRequestId(savedItemRequest.getId());
        item1.setUserId(savedUser.getId());
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setRequestId(savedItemRequest.getId());
        item2.setUserId(savedUser.getId());
        item2.setAvailable(true);
        itemRepository.save(item2);

        RequestDtoResponseWithMD result =
                itemRequestService.getItemRequest(savedUser.getId(), savedItemRequest.getId());

        assertNotNull(result);
        assertEquals(savedItemRequest.getId(), result.getId());
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
    }


    @Test
    void shouldReturnPrivateRequestsWithPagination() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setDescription("Request " + i);
            itemRequest.setRequester(savedUser);
            itemRequestRepository.save(itemRequest);
        }

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getPrivateRequests(savedUser.getId(), 0, 3);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void shouldReturnOtherRequestsWithPagination() {
        User user1 = new User();
        user1.setName("NameUser1");
        user1.setEmail("EmailUser1@email");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("NameUser2");
        user2.setEmail("EmailUser2@email");
        User savedUser2 = userRepository.save(user2);

        for (int i = 1; i <= 3; i++) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setDescription("Other Request " + i);
            itemRequest.setRequester(savedUser1);
            itemRequestRepository.save(itemRequest);
        }

        List<RequestDtoResponseWithMD> result =
                itemRequestService.getOtherRequests(savedUser2.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreate() {
        Long nonExistentUserId = 999L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, nonExistentUserId));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForPrivateRequests() {
        Long nonExistentUserId = 999L;

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemRequestService.getPrivateRequests(nonExistentUserId, 0, 10));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForOtherRequests() {
        Long nonExistentUserId = 999L;

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemRequestService.getOtherRequests(nonExistentUserId, 0, 10));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestNotFound() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);
        Long nonExistentRequestId = 999L;

        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> itemRequestService.getItemRequest(savedUser.getId(), nonExistentRequestId));
    }
}