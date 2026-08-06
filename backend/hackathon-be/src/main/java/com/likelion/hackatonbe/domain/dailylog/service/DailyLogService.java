package com.likelion.hackatonbe.domain.dailylog.service;

import com.likelion.hackatonbe.domain.dailylog.dto.DailyLogDto;
import com.likelion.hackatonbe.domain.dailylog.entity.DailyLog;
import com.likelion.hackatonbe.domain.dailylog.repository.DailyLogRepository;
import com.likelion.hackatonbe.domain.user.entity.Child;
import com.likelion.hackatonbe.domain.user.repository.ChildRepository;
import com.likelion.hackatonbe.global.S3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final ChildRepository childRepository;
    private final S3Service s3Service;

    public DailyLogDto.Response createDailyLog(
            Long userId,
            MultipartFile image,
            DailyLogDto.CreateRequest request
    ) {
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이 정보를 찾을 수 없습니다."));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadImage(image);
        }

        DailyLog dailyLog = DailyLog.builder()
                .child(child)
                .date(request.getDate())
                .mealType(request.getMealType())
                .foods(request.getFoods())
                .imageUrl(imageUrl)
                .showerCount(request.getShowerCount())
                .moisturizerCount(request.getMoisturizerCount())
                .symptoms(request.getSymptoms())
                .memo(request.getMemo())
                .build();

        DailyLog savedLog = dailyLogRepository.save(dailyLog);

        return convertToResponse(savedLog);
    }

    @Transactional(readOnly = true)
    public List<DailyLogDto.Response> getDailyLogsByDate(Long userId, LocalDate date) {
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이 정보를 찾을 수 없습니다."));

        List<DailyLog> dailyLogs = dailyLogRepository.findByChildIdAndDate(child.getId(), date);

        return dailyLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DailyLogDto.Response convertToResponse(DailyLog log) {
        return new DailyLogDto.Response(
                log.getId(),
                log.getMealType(),
                log.getFoods(),
                log.getImageUrl(),
                log.getShowerCount(),
                log.getMoisturizerCount(),
                log.getSymptoms(),
                log.getMemo(),
                log.getDate()
        );
    }

    public DailyLogDto.Response updateDailyLog(
            Long userId,
            Long dailyLogId,
            MultipartFile image,
            DailyLogDto.CreateRequest request
    ) {
        DailyLog dailyLog = dailyLogRepository.findById(dailyLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일상 기록이 존재하지 않습니다."));

        if (!dailyLog.getChild().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        String imageUrl = dailyLog.getImageUrl();
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadImage(image);
        }

        dailyLog.update(
                request.getMealType(),
                request.getFoods(),
                imageUrl,
                request.getShowerCount(),
                request.getMoisturizerCount(),
                request.getSymptoms(),
                request.getMemo()
        );

        return convertToResponse(dailyLog);
    }

    public void deleteDailyLog(Long userId, Long dailyLogId) {
        DailyLog dailyLog = dailyLogRepository.findById(dailyLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일상 기록이 존재하지 않습니다."));

        if (!dailyLog.getChild().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        dailyLogRepository.delete(dailyLog);
    }
}