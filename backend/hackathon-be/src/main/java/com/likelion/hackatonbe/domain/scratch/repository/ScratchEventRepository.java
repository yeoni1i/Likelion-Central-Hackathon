package com.likelion.hackatonbe.domain.scratch.repository;

import com.likelion.hackatonbe.domain.scratch.entity.ScratchEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScratchEventRepository extends JpaRepository<ScratchEvent, Long> {

    List<ScratchEvent> findAllByUserIdAndClientEventIdIn(Long userId, Collection<String> clientEventIds);

    List<ScratchEvent> findAllByUserIdAndStartTsGreaterThanEqualAndStartTsLessThan(
            Long userId,
            Instant from,
            Instant to
    );
}
