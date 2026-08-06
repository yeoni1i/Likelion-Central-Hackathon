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

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final ChildRepository childRepository;
    private final S3Service s3Service;

    @Transactional
    public DailyLogDto.Response createDailyLog(Long userId, MultipartFile image, DailyLogDto.CreateRequest request) {
        Child child = childRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이 정보를 찾을 수 없습니다."));

        String imageUrl = s3Service.uploadImage(image);

        DailyLog dailyLog = DailyLog.builder()
                .child(child)
                .mealType(request.getMealType())
                .foods(request.getFoods())
                .imageUrl(imageUrl)
                .date(request.getDate())
                .build();

        DailyLog savedLog = dailyLogRepository.save(dailyLog);

        return new DailyLogDto.Response(
                savedLog.getId(),
                savedLog.getMealType(),
                savedLog.getFoods(),
                savedLog.getImageUrl(),
                savedLog.getDate()
        );
    }
}