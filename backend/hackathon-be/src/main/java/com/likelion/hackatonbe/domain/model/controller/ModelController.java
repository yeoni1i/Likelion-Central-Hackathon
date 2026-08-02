package com.likelion.hackatonbe.domain.model.controller;

import com.likelion.hackatonbe.core.time.TimeProvider;
import com.likelion.hackatonbe.domain.model.dto.EventFeedbackRequest;
import com.likelion.hackatonbe.domain.model.dto.ModelManifestResponse;
import com.likelion.hackatonbe.domain.model.entity.EventFeedback;
import com.likelion.hackatonbe.domain.model.repository.EventFeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ModelController {
    private final EventFeedbackRepository feedbacks;
    private final TimeProvider timeProvider;
    public ModelController(EventFeedbackRepository feedbacks, TimeProvider timeProvider) {
        this.feedbacks=feedbacks; this.timeProvider=timeProvider;
    }
    @GetMapping("/models/scratch/latest")
    public ModelManifestResponse latest() {
        return new ModelManifestResponse(
                "prototype-rule-1", "/models/scratch-prototype.tflite",
                "REPLACE_WITH_RELEASE_SHA256", 184320, 0.60, 3,
                "prototype", 50, 120, "int8"
        );
    }
    @PostMapping("/feedback/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void feedback(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody EventFeedbackRequest request
    ) {
        feedbacks.save(new EventFeedback(
                userId, request.clientEventId(), request.label(), request.context(),
                request.rawWindowIncluded(), timeProvider.now()
        ));
    }
}
