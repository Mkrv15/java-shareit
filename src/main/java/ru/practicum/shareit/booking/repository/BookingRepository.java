package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime, Sort sort);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1")
    List<Booking> findAllByOwnerId(Long ownerId, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(Long ownerId, LocalDateTime localDateTime, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(Long ownerId, LocalDateTime localDateTime, Sort sort);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime, Sort sort);

    List<Booking> findByItemIdAndStatus(Long itemId, Status status, Sort sort);

    Optional<List<Booking>> findAllByItemIdAndBookerIdAndStatus(Long itemId, Long bookerId, Status status, Sort sort);
}