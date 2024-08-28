package ae.roommaster.app;

import ae.roommaster.app.booking.Booking;
import ae.roommaster.app.booking.BookingRepository;
import ae.roommaster.app.booking.BookingRequest;
import ae.roommaster.app.booking.BookingService;
import ae.roommaster.app.exception.InvalidBookingException;
import ae.roommaster.app.exception.OverlappingBookingException;
import ae.roommaster.app.exception.ResourceNotFoundException;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomRepository;
import ae.roommaster.app.user.User;
import ae.roommaster.app.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private User user;
    private BookingRequest bookingRequest;

    // Overlapping Booking Test will fail if you try it after 11 PM, as it will add minutes which will make booking span across days
    // as you cannot book other than same day, so you will get InvalidBooking Exception due to time
    LocalDateTime startTime = roundToNearest15Minutes(LocalDateTime.now().plusMinutes(30));
    LocalDateTime endTime = roundToNearest15Minutes(startTime.plusMinutes(15));

    public static LocalDateTime roundToNearest15Minutes(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int newMinute = (minute + 7) / 15 * 15;  // +7 to handle rounding up

        // If rounding up results in 60 minutes, adjust the hour
        if (newMinute == 60) {
            dateTime = dateTime.plusHours(1).withMinute(0);
        } else {
            dateTime = dateTime.withMinute(newMinute);
        }

        return dateTime.truncatedTo(ChronoUnit.MINUTES);  // Truncate to remove seconds and nanos
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        room = new Room();
        room.setId(1L);
        room.setName("Test Room");

        user = new User();
        user.setId(1L);
        user.setEmail("test@roommaster.ae");

        bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setUserId(1L);
        bookingRequest.setStartTime(startTime);
        bookingRequest.setEndTime(endTime);
        bookingRequest.setNumberOfPeople(5);
    }

    @Test
    void createBooking_RoomNotFound() {
        when(roomRepository.findById(bookingRequest.getRoomId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_UserNotFound() {
        when(roomRepository.findById(bookingRequest.getRoomId())).thenReturn(Optional.of(room));
        when(userRepository.findById(bookingRequest.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_InvalidBookingTime() {
        bookingRequest.setStartTime(LocalDateTime.of(2024, 8, 26, 12, 12));
        bookingRequest.setEndTime(LocalDateTime.of(2024, 8, 26, 10, 0));

        when(roomRepository.findById(bookingRequest.getRoomId())).thenReturn(Optional.of(room));
        when(userRepository.findById(bookingRequest.getUserId())).thenReturn(Optional.of(user));

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(bookingRequest));
    }

    @Test
    void createBooking_OverlappingBooking() {

        Room room = new Room();
        room.setId(1L);
        room.setName("Test Room");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@roommaster.ae");

        Booking existingBooking = Booking.builder()
                .room(room)
                .bookedBy(user)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findOverlappingBookings(
                room.getId(),
                existingBooking.getStartTime(),
                existingBooking.getEndTime())
        ).thenReturn(List.of(existingBooking));


        BookingRequest overlappingRequest = new BookingRequest();
        overlappingRequest.setRoomId(room.getId());
        overlappingRequest.setUserId(user.getId());
        overlappingRequest.setStartTime(startTime);
        overlappingRequest.setEndTime(endTime);
        overlappingRequest.setNumberOfPeople(5);


        assertThrows(OverlappingBookingException.class, () -> bookingService.createBooking(overlappingRequest),
                "Expected an OverlappingBookingException when the booking overlaps with an existing one");
    }

}

