package ch.admin.seco.alv.service.tracking.service;

import ch.admin.seco.alv.service.tracking.domain.TrackingItem;

public class TrackingItemNotFoundException extends AggregateNotFoundException {

    public TrackingItemNotFoundException(String aggregateId) {
        super(TrackingItem.class, aggregateId);
    }
}
