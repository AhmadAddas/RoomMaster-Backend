package ae.roommaster.app.booking;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class BookingRequest {

    private Long roomId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int numberOfPeople;

}
