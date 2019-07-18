package ch.admin.seco.alv.service.tracking.web;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.micrometer.core.annotation.Timed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ch.admin.seco.alv.service.tracking.domain.TrackingItem;
import ch.admin.seco.alv.service.tracking.service.CreateTrackingItemDto;
import ch.admin.seco.alv.service.tracking.service.TrackingItemService;

@RestController
@RequestMapping("/api/tracking-item")
public class TrackingItemRestController {

    private final TrackingItemService trackingItemService;

    public TrackingItemRestController(TrackingItemService trackingItemService) {
        this.trackingItemService = trackingItemService;
    }

    @Timed
    @PostMapping
    public ResponseEntity<TrackingItem> createTrackingItem(
        @Valid @RequestBody CreateTrackingItemDto trackingItem,
        HttpServletRequest request,
        UriComponentsBuilder uriComponentsBuilder) {

        UUID trackingItemId = trackingItemService.createTrackingItem(trackingItem, request.getRemoteAddr());

        return ResponseEntity.created(
            uriComponentsBuilder
                .path("/api/tracking-item/{id}")
                .buildAndExpand(trackingItemId)
                .toUri())
            .build();
    }

    @Timed
    @GetMapping
    public Page<TrackingItem> getAllTrackingItems(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        return trackingItemService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeStamp")));
    }

    @Timed
    @GetMapping("/{id}")
    public TrackingItem getTrackingItem(@PathVariable UUID id) {
        return trackingItemService.findOne(id);
    }

    @Timed
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrackingItem(@PathVariable UUID id) {
        trackingItemService.delete(id);
    }

}
