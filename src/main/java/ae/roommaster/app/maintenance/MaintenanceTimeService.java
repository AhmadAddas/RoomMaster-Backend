package ae.roommaster.app.maintenance;

import ae.roommaster.app.exception.ResourceNotFoundException;
import ae.roommaster.app.exception.OverlappingBookingException;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceTimeService {

    private final MaintenanceTimeRepository maintenanceTimeRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public void deleteMaintenanceTime(Long id) {
        MaintenanceTime maintenanceTime = maintenanceTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance time not found with ID: " + id));

        // Check for overlapping bookings before deletion
        if (maintenanceTimeRepository.existsByRoomIdAndStartTimeAndEndTime(
                maintenanceTime.getRoom().getId(),
                maintenanceTime.getStartTime(),
                maintenanceTime.getEndTime())) {
            throw new OverlappingBookingException("Cannot delete maintenance time with overlapping bookings");
        }

        maintenanceTimeRepository.delete(maintenanceTime);
    }

    public MaintenanceTime addMaintenanceTime(Long roomId, LocalTime startTime, LocalTime endTime) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        MaintenanceTime maintenanceTime = MaintenanceTime.builder()
                .startTime(startTime)
                .endTime(endTime)
                .room(room)
                .build();
        return maintenanceTimeRepository.save(maintenanceTime);
    }

    public List<MaintenanceTime> getMaintenanceTimesByRoom(Long roomId) {
        Long room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found")).getId();
        return maintenanceTimeRepository.findByRoomId(room);
    }

    public void removeMaintenanceTime(Long maintenanceTimeId) {

        MaintenanceTime maintenanceTime = maintenanceTimeRepository.findById(maintenanceTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance time not found with ID: " + maintenanceTimeId));

        Room room = maintenanceTime.getRoom();
        room.getMaintenanceTimes().remove(maintenanceTime);
        roomRepository.save(room);
    }

    public List<MaintenanceTime> getAllMaintenanceTimes() {
        return maintenanceTimeRepository.findAll();

    }

}



