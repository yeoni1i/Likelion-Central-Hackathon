package com.likelion.hackatonbe.domain.analysis.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    private final OpenAIClient openAIClient;

    public OpenAIService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String analyze(
            int scratchCount,
            double totalScratchSeconds,
            String meals,
            String dailyNotes,
            Double temperature,
            Integer humidity,
            String airQuality
    ) {

        String prompt = """
        사용자의 하루 긁음 기록, 생활 기록, 환경 정보를 종합하여 분석해주세요.

        [긁음 기록]
        긁음 횟수: %d회
        총 긁음 시간: %.1f초

        [식단]
        %s

        [일상 특이사항]
        %s

        [환경 정보]
        기온: %s
        습도: %s
        미세먼지 상태: %s

        다음 내용을 간단하고 명확하게 분석해주세요.

        1. 오늘 상태 요약
        - 오늘의 긁음 횟수와 시간을 중심으로 상태를 요약해주세요.

        2. 긁음과 관련 가능성이 있는 자극 요인
        - 식단, 생활 기록, 기온, 습도, 미세먼지 정보를 함께 고려해주세요.
        - 입력 데이터에서 관찰되는 요인만 언급해주세요.
        - 근거가 부족한 경우 관련성이 있다고 추측하지 마세요.

        3. 다음 날 참고할 생활 관리 조언
        - 오늘 기록을 바탕으로 사용자가 관찰하거나 관리할 수 있는 내용을 제안해주세요.

        주의:
        - 특정 음식, 행동 또는 환경 요인이 긁음의 직접적인 원인이라고 단정하지 마세요.
        - 상관관계와 인과관계를 구분해주세요.
        - 의료 진단을 하지 마세요.
        - 입력되지 않은 정보를 임의로 만들어내지 마세요.
        """.formatted(
                scratchCount,
                totalScratchSeconds,
                meals,
                dailyNotes,
                temperature != null ? temperature + "℃" : "기록 없음",
                humidity != null ? humidity + "%" : "기록 없음",
                airQuality != null ? airQuality : "기록 없음"
        );

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_5_2)
                        .addUserMessage(prompt)
                        .build();

        var response = openAIClient.chat()
                .completions()
                .create(params);

        return response.choices()
                .stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElse("AI 분석 결과가 없습니다.");
    }
}