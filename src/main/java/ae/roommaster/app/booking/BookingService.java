package ae.roommaster.app.booking;

import ae.roommaster.app.exception.InvalidBookingException;
import ae.roommaster.app.exception.OverlappingBookingException;
import ae.roommaster.app.exception.ResourceNotFoundException;
import ae.roommaster.app.maintenance.MaintenanceTime;
import ae.roommaster.app.maintenance.MaintenanceTimeRepository;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomRepository;
import ae.roommaster.app.user.User;
import ae.roommaster.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MaintenanceTimeRepository maintenanceTimeRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(BookingRequest bookingRequest) {
        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + bookingRequest.getRoomId()));
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + bookingRequest.getUserId()));

        validateBookingTime(bookingRequest.getStartTime(), bookingRequest.getEndTime());
        checkForOverlappingBookings(bookingRequest.getRoomId(), bookingRequest.getStartTime(), bookingRequest.getEndTime());
        checkForOverlappingMaintenance(bookingRequest.getRoomId(), bookingRequest.getStartTime(), bookingRequest.getEndTime());



        if (bookingRequest.getNumberOfPeople() > room.getCapacity()) {
            throw new InvalidBookingException("Number of people exceeds room capacity");
        }

        if (bookingRequest.getNumberOfPeople() <= 1) {
            throw new InvalidBookingException("Number of people should be more than 1");
        }
        Booking booking = Booking.builder()
                .room(room)
                .bookedBy(user)
                .startTime(bookingRequest.getStartTime())
                .endTime(bookingRequest.getEndTime())
                .numberOfPeople(bookingRequest.getNumberOfPeople())
                .build();


        return bookingRepository.save(booking);
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
        if (LocalDateTime.now().isAfter(booking.getStartTime().minusMinutes(30))) {
            throw new InvalidBookingException("Cannot cancel booking within 30 minutes of the start time");
        }
        bookingRepository.delete(booking);
    }

    private void validateBookingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidBookingException("Start time must be before end time");
        }

        if (!isTimeIn15MinuteInterval(startTime) || !isTimeIn15MinuteInterval(endTime)) {
            throw new InvalidBookingException("Booking times must be in 15-minute intervals");
        }

        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            throw new InvalidBookingException("Booking cannot span across multiple days");
        }

        if (startTime.toLocalTime().isBefore(LocalDateTime.now().toLocalTime())) {
            throw new InvalidBookingException("Booking cannot be in the past");
        }
    }

    private boolean isTimeIn15MinuteInterval(LocalDateTime time) {
        int minutes = time.getMinute();
        return minutes % 15 == 0;
    }

    private void checkForOverlappingBookings(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(roomId, startTime, endTime);
        if (!overlappingBookings.isEmpty()) {
            throw new OverlappingBookingException("Booking overlaps with an existing booking");
        }
    }

    private void checkForOverlappingMaintenance(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<MaintenanceTime> overlappingMaintenance = maintenanceTimeRepository.findOverlappingMaintenance(roomId,
                startTime.toLocalTime(), endTime.toLocalTime());
        if (!overlappingMaintenance.isEmpty()) {
            throw new InvalidBookingException("Booking time overlaps with scheduled maintenance");
        }
    }

    public boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        // Ensure the room exists
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        LocalTime startTimeMaintenance = startTime.toLocalTime();
        LocalTime endTimeMaintenance = endTime.toLocalTime();
        // Check for overlapping maintenance times
        List<MaintenanceTime> maintenanceTimes = maintenanceTimeRepository.findByRoomId(roomId);
        for (MaintenanceTime mt : maintenanceTimes) {
            if (mt.getStartTime().isBefore(startTimeMaintenance) && mt.getEndTime().isAfter(endTimeMaintenance)) {
                return false;
            }
        }

        // Check for conflicting bookings
        boolean hasConflictingBooking = bookingRepository.existsByRoomIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                roomId, startTime, endTime);
        return !hasConflictingBooking;
    }

    public List<Booking> getAllBookingsForBookedBy(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return bookingRepository.findByBookedBy(user);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

    }

}



