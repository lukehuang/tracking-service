package ch.admin.seco.alv.service.tracking.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bouncycastle.util.encoders.Hex;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.alv.service.tracking.domain.TrackingItem;
import ch.admin.seco.alv.service.tracking.domain.TrackingItemRepository;
import ch.admin.seco.alv.service.tracking.service.security.IsSysAdmin;

@Service
@Transactional
public class TrackingItemService {

    private final TrackingItemRepository trackingItemRepository;

    public TrackingItemService(TrackingItemRepository trackingItemRepository) {
        this.trackingItemRepository = trackingItemRepository;
    }

    public UUID createTrackingItem(CreateTrackingItemDto createTrackingItem, String remoteAddress) {

        TrackingItem trackingItem = new TrackingItem.Builder()
            .setTrackingId(createTrackingItem.getTrackingId())
            .setEventName(createTrackingItem.getEventName())
            .setEventData(createTrackingItem.getEventData().toString())
            .setIpHash(hashIp(remoteAddress))
            .setUserRoles(getUserRoles())
            .setLocale(createTrackingItem.getLocale())
            .build();

        return this.trackingItemRepository.save(trackingItem).getId();
    }


    @IsSysAdmin
    @Transactional(readOnly = true)
    public Page<TrackingItem> findAll(PageRequest pageRequest) {
        return trackingItemRepository.findAll(pageRequest);
    }

    @IsSysAdmin
    @Transactional(readOnly = true)
    public TrackingItem findOne(UUID id) {
        return trackingItemRepository.findById(id)
            .orElseThrow(() -> new TrackingItemNotFoundException(id.toString()));
    }

    @IsSysAdmin
    public void delete(UUID id) {
        trackingItemRepository.deleteById(id);
    }


    private String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }

        return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }
}
