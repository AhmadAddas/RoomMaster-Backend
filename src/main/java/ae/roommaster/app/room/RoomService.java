package ae.roommaster.app.room;

import ae.roommaster.app.exception.ResourceNotFoundException;
import ae.roommaster.app.maintenance.MaintenanceTime;
import ae.roommaster.app.maintenance.MaintenanceTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MaintenanceTimeRepository maintenanceTimeRepository;

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
        roomRepository.delete(room);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public List<Room> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        validateSameDay(startTime, endTime);
        validateSameDayAsServer(startTime);
        return filterRoomsWithMaintenance(roomRepository.findAvailableRooms(startTime, endTime), startTime.toLocalTime(), endTime.toLocalTime());
    }

    private void validateSameDay(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        if (!startDate.equals(endDate)) {
            throw new IllegalArgumentException("Start time and end time must be on the same day");
        }
    }

    private void validateSameDayAsServer(LocalDateTime startTime) {
        LocalDate serverDate = LocalDate.now();

        if (!startTime.toLocalDate().equals(serverDate)) {
            throw new IllegalArgumentException("Start time must be on the same day as the server's current day");
        }
    }

    private List<Room> filterRoomsWithMaintenance(List<Room> rooms, LocalTime startTime, LocalTime endTime) {
        return rooms.stream()
                .filter(room -> {
                    List<MaintenanceTime> maintenanceTimes = maintenanceTimeRepository.findMaintenanceTimesByRoom(room.getId());
                    return maintenanceTimes.stream().noneMatch(mt ->
                            (mt.getStartTime().isBefore(endTime) && mt.getEndTime().isAfter(startTime))
                    );
                })
                .toList();

    }

}



