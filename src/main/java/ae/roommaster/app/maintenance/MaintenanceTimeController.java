package ae.roommaster.app.maintenance;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance-times")
public class MaintenanceTimeController {

    private final MaintenanceTimeService maintenanceTimeService;


    public MaintenanceTimeController(MaintenanceTimeService maintenanceTimeService) {
        this.maintenanceTimeService = maintenanceTimeService;
    }

    @PreAuthorize("hasAuthority('admin:create') || hasAuthority('manager:create')")
    @PostMapping("/add")
    public MaintenanceTime addMaintenanceTime(@RequestParam Long room,
                                              @RequestParam String startTime,
                                              @RequestParam String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return maintenanceTimeService.addMaintenanceTime(room, start, end);
    }

    @PreAuthorize("hasAuthority('admin:delete') || hasAuthority('manager:delete')")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeMaintenanceTime(@PathVariable Long id) {
        maintenanceTimeService.removeMaintenanceTime(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/rooms/{roomId}")
    public List<MaintenanceTime> getMaintenanceTimesByRoom(@PathVariable Long roomId) {
        return maintenanceTimeService.getMaintenanceTimesByRoom(roomId);
    }

    @GetMapping("/rooms")
    public List<MaintenanceTime> getAllMaintenanceTimes() {
        return maintenanceTimeService.getAllMaintenanceTimes();

    }

}
