package ch.admin.seco.service.tracking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.admin.seco.service.tracking.domain.TrackingItem;

/**
 * Service Interface for managing TrackingItem.
 */
public interface TrackingItemService {

    /**
     * Save a trackingItem.
     *
     * @param trackingItem the entity to save
     * @return the persisted entity
     */
    TrackingItem save(TrackingItem trackingItem);

    /**
     * Get all the trackingItems.
     *
     * @return the list of entities
     */
    List<TrackingItem> findAll();

    /**
     * Get the "id" trackingItem.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TrackingItem> findOne(UUID id);

    /**
     * Delete the "id" trackingItem.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);
}
