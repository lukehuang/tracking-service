package ch.admin.seco.service.tracking.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

/**
 * A TrackingItem.
 */
@Entity
@Table(name = "tracking_item")
public class TrackingItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Column(name = "event", nullable = false)
    private String event;

    @Column(name = "data")
    @JsonRawValue
    private String data;

    @Column(name = "time_stamp")
    private Instant timeStamp;

    @NotNull
    @Column(name = "tracking_id", nullable = false)
    private String trackingId;

    @Column(name = "ip_hash")
    private String ipHash;

    @Column(name = "locale")
    private String locale;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public TrackingItem event(String event) {
        this.event = event;
        return this;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public TrackingItem data(String data) {
        this.data = data;
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty(value = "data")
    public void setJsonRaw(JsonNode jsonNode) {
        setData(jsonNode.toString());
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public TrackingItem timeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public TrackingItem trackingId(String trackingId) {
        this.trackingId = trackingId;
        return this;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getIpHash() {
        return ipHash;
    }

    public TrackingItem ipHash(String ipHash) {
        this.ipHash = ipHash;
        return this;
    }

    public void setIpHash(String ipHash) {
        this.ipHash = ipHash;
    }

    public String getLocale() {
        return locale;
    }

    public TrackingItem locale(String locale) {
        this.locale = locale;
        return this;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrackingItem trackingItem = (TrackingItem) o;
        if (trackingItem.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), trackingItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TrackingItem{" +
            "id=" + getId() +
            ", event='" + getEvent() + "'" +
            ", data='" + getData() + "'" +
            ", timeStamp='" + getTimeStamp() + "'" +
            ", trackingId='" + getTrackingId() + "'" +
            ", ipHash='" + getIpHash() + "'" +
            ", locale='" + getLocale() + "'" +
            "}";
    }
}
