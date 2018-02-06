package ch.admin.seco.service.tracking.repository;

import java.util.UUID;

import ch.admin.seco.service.tracking.domain.TrackingItem;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the TrackingItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrackingItemRepository extends JpaRepository<TrackingItem, UUID> {

}
