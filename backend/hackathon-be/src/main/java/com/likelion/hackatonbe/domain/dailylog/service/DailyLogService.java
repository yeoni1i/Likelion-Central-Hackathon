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
}