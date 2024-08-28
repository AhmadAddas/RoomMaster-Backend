package ae.roommaster.app;

import ae.roommaster.app.booking.BookingService;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomService;
import ae.roommaster.app.room.RoomRepository;
import ae.roommaster.app.maintenance.MaintenanceTime;
import ae.roommaster.app.maintenance.MaintenanceTimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MaintenanceTimeRepository maintenanceTimeRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private RoomService roomService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    LocalDateTime startTime = roundToNearest15Minutes(LocalDateTime.now().plusHours(1));
    LocalDateTime endTime = roundToNearest15Minutes(startTime.plusHours(2));

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

    @Test
    void testGetRoomById() {
        Room room = Room.builder().id(1L).name("Amaze").capacity(3).build();
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

        Room result = roomService.getRoomById(1L);

        assertEquals("Amaze", result.getName());
        assertEquals(3, result.getCapacity());
    }

    @Test
    void testIsThereRoomWithMaintenanceConflict() {
        Room room = Room.builder().id(1L).name("Amaze").capacity(3).build();
        MaintenanceTime maintenanceTime = new MaintenanceTime(null, startTime.toLocalTime(), endTime.toLocalTime(), room);
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(maintenanceTimeRepository.findByRoomId(anyLong())).thenReturn(List.of(maintenanceTime));
        boolean isAvailable = bookingService.isRoomAvailable(1L, startTime, endTime);

        assertFalse(isAvailable);

    }
}
