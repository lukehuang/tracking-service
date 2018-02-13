package ch.admin.seco.service.tracking.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.tracking.domain.TrackingItem;
import ch.admin.seco.service.tracking.repository.TrackingItemRepository;
import ch.admin.seco.service.tracking.service.TrackingItemService;

/**
 * Service Implementation for managing TrackingItem.
 */
@Service
@Transactional
public class TrackingItemServiceImpl implements TrackingItemService {

    private final Logger log = LoggerFactory.getLogger(TrackingItemServiceImpl.class);

    private final TrackingItemRepository trackingItemRepository;

    public TrackingItemServiceImpl(TrackingItemRepository trackingItemRepository) {
        this.trackingItemRepository = trackingItemRepository;
    }

    /**
     * Save a trackingItem.
     *
     * @param trackingItem the entity to save
     * @return the persisted entity
     */
    @Override
    public TrackingItem save(TrackingItem trackingItem) {
        trackingItem.setTimeStamp(Instant.now());
        log.debug("Request to save TrackingItem : {}", trackingItem);
        return trackingItemRepository.save(trackingItem);
    }

    /**
     * Get all the trackingItems.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrackingItem> findAll() {
        log.debug("Request to get all TrackingItems");
        return trackingItemRepository.findAll();
    }

    /**
     * Get one trackingItem by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingItem> findOne(UUID id) {
        log.debug("Request to get TrackingItem : {}", id);
        return trackingItemRepository.findById(id);
    }

    /**
     * Delete the trackingItem by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete TrackingItem : {}", id);
        trackingItemRepository.deleteById(id);
    }
}
