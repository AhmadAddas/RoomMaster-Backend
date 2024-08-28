package ae.roommaster.app.booking;

import ae.roommaster.app.room.Room;
import ae.roommaster.app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"room", "bookedBy"}) // Exclude room and bookedBy from toString to prevent circular references
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @NotNull
    @JoinColumn(name = "room_id")
    @JsonBackReference // Handle circular reference by indicating the inverse side of the relationship
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")  // Foreign key referencing the User entity
    private User bookedBy;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int numberOfPeople;
}
