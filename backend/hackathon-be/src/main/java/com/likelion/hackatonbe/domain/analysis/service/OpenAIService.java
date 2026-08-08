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
            String dailyNotes
    ) {

        String prompt = """
                사용자의 하루 생활 기록과 긁음 기록을 분석해주세요.

                [긁음 기록]
                긁음 횟수: %d회
                총 긁음 시간: %.1f초

                [식단]
                %s

                [일상 특이사항]
                %s

                다음 내용을 간단하고 명확하게 분석해주세요.

                1. 오늘 상태 요약
                2. 긁음 증가와 관련 가능성이 있는 요인
                3. 사용자가 다음 날 참고할 생활 관리 조언

                주의:
                - 입력된 데이터만 근거로 판단하세요.
                - 특정 음식이나 행동이 원인이라고 단정하지 마세요.
                - 의료 진단을 하지 마세요.
                """.formatted(
                scratchCount,
                totalScratchSeconds,
                meals,
                dailyNotes
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