package ch.admin.seco.alv.service.tracking.domain;


import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonRawValue;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "tracking_item")
public class TrackingItem {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    private String eventName;

    @JsonRawValue
    private String eventData;

    private Instant timeStamp;

    @NotNull
    private String trackingId;

    private String userRoles;

    private String ipHash;

    private String locale;


    public UUID getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventData() {
        return eventData;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public String getIpHash() {
        return ipHash;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TrackingItem that = (TrackingItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TrackingItem{" +
            "id=" + id +
            ", eventName='" + eventName + '\'' +
            ", eventData='" + eventData + '\'' +
            ", timeStamp=" + timeStamp +
            ", trackingId='" + trackingId + '\'' +
            ", userRoles='" + userRoles + '\'' +
            ", ipHash='" + ipHash + '\'' +
            ", locale='" + locale + '\'' +
            '}';
    }

    public static final class Builder {
        private String eventName;
        private String eventData;
        private String trackingId;
        private String ipHash;
        private String locale;
        private Set<String> userRoles = new HashSet<>();

        public Builder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder setEventData(String eventData) {
            this.eventData = eventData;
            return this;
        }

        public Builder setTrackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder setIpHash(String ipHash) {
            this.ipHash = ipHash;
            return this;
        }

        public Builder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder setUserRoles(Collection<String> userRoles) {
            this.userRoles.addAll(userRoles);
            return this;
        }

        public TrackingItem build() {
            TrackingItem trackingItem = new TrackingItem();
            trackingItem.eventName = this.eventName;
            trackingItem.trackingId = this.trackingId;
            trackingItem.ipHash = this.ipHash;
            trackingItem.locale = this.locale;
            trackingItem.userRoles = String.join(",", this.userRoles);
            trackingItem.eventData = this.eventData;
            trackingItem.timeStamp = Instant.now();

            return trackingItem;
        }
    }
}
