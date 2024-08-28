package ae.roommaster.app;

import ae.roommaster.app.maintenance.MaintenanceTime;
import ae.roommaster.app.maintenance.MaintenanceTimeRepository;
import ae.roommaster.app.room.Room;
import ae.roommaster.app.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final MaintenanceTimeRepository maintenanceTimeRepository;

    @Override
    public void run(String... args) throws Exception {

        // Create default rooms if not present
        if (roomRepository.count() == 0) {
            Room amaze = Room.builder().name("Amaze").capacity(3).build();
            Room beauty = Room.builder().name("Beauty").capacity(7).build();
            Room inspire = Room.builder().name("Inspire").capacity(12).build();
            Room strive = Room.builder().name("Strive").capacity(20).build();

            roomRepository.save(amaze);
            roomRepository.save(beauty);
            roomRepository.save(inspire);
            roomRepository.save(strive);

            System.out.println("Default conference rooms created.");

            seedMaintenanceTimes(amaze);
            seedMaintenanceTimes(beauty);
            seedMaintenanceTimes(inspire);
            seedMaintenanceTimes(strive);
        }
    }

    private void seedMaintenanceTimes(Room room) {

        maintenanceTimeRepository.save(MaintenanceTime.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 15))
                .room(room)
                .build());

        maintenanceTimeRepository.save(MaintenanceTime.builder()
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(13, 15))
                .room(room)
                .build());

        maintenanceTimeRepository.save(MaintenanceTime.builder()
                .startTime(LocalTime.of(17, 0))
                .endTime(LocalTime.of(17, 15))
                .room(room)
                .build());

    }

}
