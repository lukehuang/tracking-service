package ch.admin.seco.alv.service.tracking.service;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;

public class CreateTrackingItemDto {
    @NotNull
    private String eventName;

    private JsonNode eventData;

    @NotNull
    private String trackingId;

    @NotNull
    private String locale;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public JsonNode getEventData() {
        return eventData;
    }

    public void setEventData(JsonNode eventData) {
        this.eventData = eventData;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
