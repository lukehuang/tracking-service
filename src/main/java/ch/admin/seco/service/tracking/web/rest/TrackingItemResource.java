package ch.admin.seco.service.tracking.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.tracking.domain.TrackingItem;
import ch.admin.seco.service.tracking.service.TrackingItemService;
import ch.admin.seco.service.tracking.web.rest.errors.BadRequestAlertException;
import ch.admin.seco.service.tracking.web.rest.util.HeaderUtil;

/**
 * REST controller for managing TrackingItem.
 */
@RestController
@RequestMapping("/api")
public class TrackingItemResource {

    private static final String ENTITY_NAME = "trackingItem";
    private final Logger log = LoggerFactory.getLogger(TrackingItemResource.class);
    private final TrackingItemService trackingItemService;

    public TrackingItemResource(TrackingItemService trackingItemService) {
        this.trackingItemService = trackingItemService;
    }

    /**
     * POST  /tracking-items : Create a new trackingItem.
     *
     * @param trackingItem the trackingItem to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trackingItem, or with status 400 (Bad Request) if the trackingItem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tracking-items")
    @Timed
    public ResponseEntity<TrackingItem> createTrackingItem(@Valid @RequestBody TrackingItem trackingItem, HttpServletRequest request) {
        log.debug("REST request to save TrackingItem : {}", trackingItem);
        if (trackingItem.getId() != null) {
            throw new BadRequestAlertException("A new trackingItem cannot already have an ID", ENTITY_NAME, "idexists");
        }

        final String ipHash = Hashing.sha1().hashString(request.getRemoteAddr(), Charsets.UTF_8).toString();
        trackingItem.setIpHash(ipHash);
        trackingItemService.save(trackingItem);
        return ResponseEntity.ok().build();
    }


    /**
     * GET  /tracking-items : get all the trackingItems.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of trackingItems in body
     */
    @GetMapping("/tracking-items")
    @Timed
    public List<TrackingItem> getAllTrackingItems() {
        log.debug("REST request to get all TrackingItems");
        return trackingItemService.findAll();
    }

    /**
     * GET  /tracking-items/:id : get the "id" trackingItem.
     *
     * @param id the id of the trackingItem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trackingItem, or with status 404 (Not Found)
     */
    @GetMapping("/tracking-items/{id}")
    @Timed
    public ResponseEntity<TrackingItem> getTrackingItem(@PathVariable UUID id) {
        log.debug("REST request to get TrackingItem : {}", id);
        Optional<TrackingItem> trackingItem = trackingItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trackingItem);
    }

    /**
     * DELETE  /tracking-items/:id : delete the "id" trackingItem.
     *
     * @param id the id of the trackingItem to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tracking-items/{id}")
    @Timed
    public ResponseEntity<Void> deleteTrackingItem(@PathVariable UUID id) {
        log.debug("REST request to delete TrackingItem : {}", id);
        trackingItemService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
