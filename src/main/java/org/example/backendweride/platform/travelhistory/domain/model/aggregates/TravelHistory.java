package org.example.backendweride.platform.travelhistory.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendweride.platform.travelhistory.domain.model.commands.CreateTravelHistoryCommand;
import org.example.backendweride.platform.travelhistory.domain.model.commands.UpdateTravelHistoryCommand;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class TravelHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    private Long userId;

    @Column(nullable = false)
    @Getter
    private String location;

    @Column(nullable = false)
    @Getter
    private String vehicle;

    @Column(nullable = false)
    @Getter
    private String image;

    @Column(nullable = false)
    @Getter
    private String tripDuration;

    @Column(nullable = false)
    @Getter
    private String travelDistance;

    @CreatedDate
    @Getter
    private Date createdAt;

    public TravelHistory() {}

    public TravelHistory(CreateTravelHistoryCommand travelHistoryCommand) {
        this.userId = travelHistoryCommand.userId();
        this.location = travelHistoryCommand.location();
        this.vehicle = travelHistoryCommand.vehicle();
        this.image = travelHistoryCommand.image();
        this.tripDuration = travelHistoryCommand.tripDuration();
        this.travelDistance = travelHistoryCommand.travelDistance();
    }

    public void updateFromCommand(UpdateTravelHistoryCommand travelHistoryCommand) {
        this.location = travelHistoryCommand.location();
        this.vehicle = travelHistoryCommand.vehicle();
        this.image = travelHistoryCommand.image();
        this.tripDuration = travelHistoryCommand.tripDuration();
        this.travelDistance = travelHistoryCommand.travelDistance();
    }
}
