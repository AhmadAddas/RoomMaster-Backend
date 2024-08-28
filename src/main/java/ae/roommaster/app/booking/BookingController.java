package ae.roommaster.app.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @PreAuthorize("hasAuthority('admin:read') || hasAuthority('manager:read')")
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PreAuthorize("hasAuthority('admin:create') || hasAuthority('manager:create') || hasRole('USER')")
    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest bookingRequest) {
        return bookingService.createBooking(bookingRequest);
    }

    @PreAuthorize("hasAuthority('admin:read') || hasAuthority('manager:read')")
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PreAuthorize("hasAuthority('admin:delete') || hasAuthority('manager:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-rooms")
    public List<Room> getAvailableRooms(@RequestParam LocalDateTime startTime,
                                        @RequestParam LocalDateTime endTime) {
        return roomService.findAvailableRooms(startTime, endTime);
    }

    @PreAuthorize("hasAuthority('admin:read') || hasAuthority('manager:read')")
    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getAllBookingsForBookedBy(userId);

    }

}




