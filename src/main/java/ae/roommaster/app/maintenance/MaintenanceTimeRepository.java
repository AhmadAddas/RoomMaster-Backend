package ae.roommaster.app.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MaintenanceTimeRepository extends JpaRepository<MaintenanceTime, Long> {

    List<MaintenanceTime> findByRoomId(Long roomId);

    @Query("SELECT m FROM MaintenanceTime m WHERE m.room.id = :roomId AND " +
            "(m.startTime < :endTime AND m.endTime > :startTime)")
    List<MaintenanceTime> findOverlappingMaintenance(@Param("roomId") Long roomId,
                                                     @Param("startTime") LocalTime startTime,
                                                     @Param("endTime") LocalTime endTime);
    boolean existsByRoomIdAndStartTimeAndEndTime(Long roomId, LocalTime startTime, LocalTime endTime);

    @Query("SELECT m FROM MaintenanceTime m WHERE m.room.id = :roomId")
    List<MaintenanceTime> findMaintenanceTimesByRoom(@Param("roomId") Long roomId);

}
