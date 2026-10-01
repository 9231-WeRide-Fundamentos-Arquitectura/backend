package org.example.backendweride.platform.profile.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendweride.platform.profile.domain.model.aggregates.Profile;
import org.example.backendweride.platform.profile.domain.model.valueobjects.Preferences;
import org.example.backendweride.platform.profile.domain.model.valueobjects.Statistics;

import java.time.LocalDate;

@Entity
@Table(name = "profiles")
@NoArgsConstructor
@Getter
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String name;
    private String phone;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String address;
    private String emergencyContact;

    @Embedded
    private Preferences preferences;

    @Embedded
    private Statistics statistics;

    public ProfileEntity(Profile profile) {
        this.id = profile.getId();
        this.userId = profile.getUserId();
        this.name = profile.getName();
        this.phone = profile.getPhone();
        this.profilePicture = profile.getProfilePicture();
        this.dateOfBirth = profile.getDateOfBirth();
        this.address = profile.getAddress();
        this.emergencyContact = profile.getEmergencyContact();
        this.preferences = profile.getPreferences();
        this.statistics = profile.getStatistics();
    }

    public Profile toDomain() {
        return new Profile(
                this.id,
                this.userId,
                this.name,
                this.phone,
                this.profilePicture,
                this.dateOfBirth,
                this.address,
                this.emergencyContact,
                this.preferences,
                this.statistics
        );
    }
}