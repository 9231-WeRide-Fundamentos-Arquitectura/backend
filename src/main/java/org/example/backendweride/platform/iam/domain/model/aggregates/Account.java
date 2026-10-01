package org.example.backendweride.platform.iam.domain.model.aggregates;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.valueobjects.ProfileId;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Account Aggregate Root
 *
 * @summary Represents an account in WeRide Platform.
 */
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account extends AbstractAggregateRoot<Account> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NotBlank
    @Getter
    @Size(max=20)
    @Column(unique = true)
    private String userName;

    @NotBlank
    @Getter
    private String password;

    @Embedded
    @Getter
    private ProfileId profileId;

    public Account(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.userName = username;
        this.password = password;
    }

    public Account(String username, String password, ProfileId profileId) {
        this(username, password);
        this.profileId = profileId;
    }

    public Account(SignUpCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SignUpCommand cannot be null");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.userName = command.username();
        this.password = command.password();
    }

    public Account(SignUpCommand command, String hashedPassword) {
        if (command == null) {
            throw new IllegalArgumentException("SignUpCommand cannot be null");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        this.userName = command.username();
        this.password = hashedPassword;
    }

    public Account(SignUpCommand command, String hashedPassword, ProfileId profileId) {
        this(command, hashedPassword);
        this.profileId = profileId;
    }

    public Account updatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
        return this;
    }

    public Account updateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.userName = userName;
        return this;
    }

    public Account updateProfileId(ProfileId profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("ProfileId cannot be null");
        }
        this.profileId = profileId;
        return this;
    }

}
