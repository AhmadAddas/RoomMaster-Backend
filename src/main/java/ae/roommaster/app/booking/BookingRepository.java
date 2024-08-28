package ae.roommaster.app.booking;

import ae.roommaster.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookedBy(User user);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsByRoomAndTimeRange(@Param("roomId") Long roomId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "(b.startTime < :endTime AND b.endTime > :startTime)")
    List<Booking> findByRoomAndTimeRange(@Param("roomId") Long roomId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.bookedBy.id = :bookedBy AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsByRoomAndBookedByAndOverlappingTime(@Param("roomId") Long roomId,
                                                      @Param("bookedBy") Long bookedBy,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    boolean existsByRoomIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND (:startTime BETWEEN b.startTime AND b.endTime OR :endTime BETWEEN b.startTime AND b.endTime)")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}


