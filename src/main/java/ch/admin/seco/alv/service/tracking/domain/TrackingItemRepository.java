package ch.admin.seco.alv.service.tracking.domain;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrackingItemRepository extends JpaRepository<TrackingItem, UUID> {

}
