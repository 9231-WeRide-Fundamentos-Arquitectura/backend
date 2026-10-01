package org.example.backendweride.platform.unlock.infrastructure;

import org.example.backendweride.platform.unlock.domain.model.UnlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnlockRequestRepository extends JpaRepository<UnlockRequest, String> {
    List<UnlockRequest> findAllByUserIdOrderByRequestedAtDesc(String userId);
}
