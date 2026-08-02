package com.likelion.hackatonbe.domain.device.repository;

import com.likelion.hackatonbe.domain.device.entity.WatchDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchDeviceRepository
        extends JpaRepository<WatchDevice, Long> {

    Optional<WatchDevice> findByDeviceIdentifier(
            String deviceIdentifier
    );
}