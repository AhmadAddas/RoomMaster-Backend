package ae.roommaster.app.maintenance;

import ae.roommaster.app.room.Room;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalTime;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "room") // Exclude toString to avoid circular reference
@Entity
@Table(name = "maintenance_time", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "startTime", "endTime"})})
public class MaintenanceTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalTime startTime;
    
    private LocalTime endTime;

    @Setter
    @ManyToOne
    @NotNull
    @JoinColumn(name = "room_id")
    @JsonBackReference // Handle circular reference by indicating the inverse side of the relationship
    private Room room;

}
