package com.likelion.hackatonbe.domain.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.hackatonbe.domain.analysis.dto.ReportAiAnalysisDto;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    public OpenAIService(
            OpenAIClient openAIClient,
            ObjectMapper objectMapper
    ) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
    }

    public ReportAiAnalysisDto analyze(
            int scratchCount,
            double totalScratchSeconds,
            String hourlyScratch,
            String weeklyScratch,
            String meals,
            String weeklyMeals,
            String dailyNotes,
            Double temperature,
            Integer humidity,
            String airQuality,
            List<String> triggerCandidates
    ) {
        String triggerCandidatesText;

        if (triggerCandidates == null || triggerCandidates.isEmpty()) {

            triggerCandidatesText = "후보 없음";

        } else {

            triggerCandidatesText =
                    IntStream.range(
                                    0,
                                    triggerCandidates.size()
                            )
                            .mapToObj(index -> {

                                String raw =
                                        triggerCandidates.get(index);

                                String[] parts =
                                        raw.split("\\|", 3);

                                String type =
                                        parts.length > 0
                                                ? parts[0]
                                                : "UNKNOWN";

                                String factor =
                                        parts.length > 1
                                                ? parts[1]
                                                : raw;

                                String score =
                                        parts.length > 2
                                                ? parts[2]
                                                : "0";
                                return String.format(
                                        "%d순위 | type=%s | factor=%s | score=%s",
                                        index + 1,
                                        type,
                                        factor,
                                        score
                                );        })


                            .collect(
                                    Collectors.joining("\n")
                            );
        }

        String prompt = """
                당신은 아토피 피부 관리 서비스의 일간 리포트를 작성하는 AI입니다.

                사용자의 오늘 데이터와 최근 7일 데이터를 함께 분석하여
                사용자가 이해하기 쉬운 일간 리포트 문구를 생성하세요.


                [중요 원칙]

                1. 제공되지 않은 데이터는 절대 추측하지 마세요.

                2. 긁음 횟수, 긁음 시간, 식단, 생활 기록, 환경 정보를
                   함께 고려하여 분석하세요.

                3. 긁음 데이터만 지나치게 강조하지 마세요.

                4. 데이터가 부족한 경우
                   "현재 기록만으로 뚜렷한 연관성을 판단하기 어렵습니다."
                   와 같이 데이터 부족 사실을 명확히 표현하세요.

                5. 특정 음식, 날씨, 생활 습관을
                   긁음의 직접적인 원인이라고 단정하지 마세요.

                6. 의학적 진단, 알레르기 진단,
                   질환에 대한 판단을 하지 마세요.

                7. 동일한 내용을 summary, pattern, carePoint에서
                   반복하지 마세요.

                8. 사용자가 실제로 이해하고 행동할 수 있는
                   짧고 자연스러운 문장으로 작성하세요.


                [오늘 긁음 기록]

                긁음 횟수: %d회
                총 긁음 시간: %.1f초


                [오늘 시간대별 긁음 기록]

                %s


                [최근 7일 긁음 기록]

                %s


                [오늘 식단]

                %s


                [최근 7일 식단]

                %s
                
                [백엔드 데이터 분석으로 선정된 자극 요인 후보]
                
                %s


                [오늘 생활 기록]

                %s


                [오늘 환경 정보]

                기온: %s
                습도: %s
                미세먼지 상태: %s


                [분석 방법]

                먼저 오늘의 전체적인 상태를 파악하세요.

                최근 7일 긁음 기록과 오늘의 긁음 기록을 비교하여
                평소와 다른 변화가 있는지 확인하세요.

                오늘 긁음이 존재하는 경우
                특정 시간대에 긁음이 집중되었는지 확인하세요.

                식단의 경우,
                오늘 섭취한 음식이 최근 7일 동안 다른 날짜에도 등장했는지 확인하세요.

                특정 음식이 등장한 여러 날짜에서
                긁음 증가가 반복적으로 함께 관찰되는 경우에만
                자극 요인 후보로 고려하세요.

                단 한 번 음식과 긁음 증가가 같이 나타난 것만으로는
                자극 요인 후보로 선정하지 마세요.

                오늘 식단만 존재하고 과거 식단 데이터가 부족하다면
                음식과 긁음 사이의 연관성을 판단하지 마세요.

                생활 기록에 샤워 횟수 또는 보습제 사용 횟수가 있다면
                현재 피부 관리 패턴을 설명하는 데 함께 활용하세요.

                환경 정보는 오늘의 상태를 설명하는 참고 정보로 사용할 수 있습니다.

                하지만 과거 환경 데이터가 입력되지 않은 경우
                최근 7일 동안 같은 날씨였다고 추측해서는 안 됩니다.


                [summary 작성 규칙]

                오늘의 전체적인 상태를 1~2문장으로 설명하세요.

                오늘 긁음이 적거나 없고 최근 기록도 안정적이라면
                안정적인 흐름이라고 설명할 수 있습니다.

                반대로 오늘 긁음이 최근 기록보다 눈에 띄게 많다면
                평소보다 긁음이 증가했다고 설명할 수 있습니다.

                단순히 입력된 숫자를 나열하지 말고
                사용자가 이해할 수 있도록 상태를 해석하세요.


                [pattern 작성 규칙]

                오늘과 최근 기록을 비교했을 때
                가장 의미 있는 패턴 하나를 설명하세요.

                예를 들어 다음과 같은 패턴을 확인할 수 있습니다.

                - 특정 시간대에 긁음 집중
                - 최근 7일 평균과 비교한 긁음 변화
                - 특정 음식이 등장한 날의 반복적인 긁음 증가
                - 생활 관리 기록의 변화

                특별한 변화가 확인되지 않는다면
                "뚜렷한 변화는 확인되지 않았습니다."
                와 같이 자연스럽게 작성하세요.

                오늘 긁음 기록이 0회라면
                존재하지 않는 시간대별 긁음 패턴을 만들어내지 마세요.


                [carePoint 작성 규칙]

                오늘 사용자가 실천할 수 있는 관리 행동 하나를 제안하세요.

                현재 데이터와 관련된 행동이어야 합니다.

                예를 들어 생활 기록이 부족하다면
                샤워나 보습 기록을 추가하도록 제안할 수 있습니다.

                보습 기록이 없다고 해서
                사용자가 실제로 보습을 하지 않았다고 단정하지 마세요.

                "건강을 관리하세요",
                "피부를 잘 관리하세요"와 같이
                지나치게 일반적인 표현은 피하세요.


                                [자극 요인 후보 작성 규칙]
                
                                백엔드에서 실제 최근 7일 데이터를 계산하여
                                자극 요인 후보와 순위를 제공합니다.
                                
                                reason은 최종 사용자에게 직접 보여주는 문장입니다.
                
                                따라서 reason에는 시스템 내부 구현이나 분석 과정에 관한 표현을
                                절대 포함하지 마세요.
                
                                다음과 같은 표현은 사용하지 마세요:
                                - "백엔드에서"
                                - "백엔드 분석 결과"
                                - "후보로 잡혔고"
                                - "시스템에서"
                                - "알고리즘에서"
                                - "점수에 따라"
                                - "AI가 분석한 결과"
                                - "데이터베이스에서"
                
                                사용자에게 관찰된 생활 기록과 긁음 변화만 자연스럽게 설명하세요.
                
                                예:
                                잘못된 표현:
                                "백엔드에서 보습 기록 감소가 주요 후보로 잡혔고..."
                
                                올바른 표현:
                                "최근 보습제 사용 횟수가 줄어든 기간에 긁음 횟수가 함께 증가하는 흐름이 관찰됐어요."
                
                                후보 선정과 순위 결정은 이미 백엔드에서 완료되었습니다.
                
                                절대 새로운 후보를 추가하지 마세요.
                                후보를 삭제하지 마세요.
                                후보 순서를 변경하지 마세요.
                                factor 이름과 type 값을 변경하지 마세요.
                
                                당신의 역할은 각 후보가 왜 선택되었는지
                                제공된 최근 7일 데이터를 바탕으로 설명하는 것입니다.
                
                                각 후보마다 reason을 한 문장으로 작성하세요.
                
                                FOOD 후보라면
                                최근 7일 식단 기록과 날짜별 긁음 횟수를 함께 확인하세요.
                
                                LIFESTYLE 후보라면
                                생활 기록 변화와 날짜별 긁음 횟수를 함께 확인하세요.
                
                                ENVIRONMENT 후보가 향후 제공되는 경우
                                실제 제공된 환경 수치만 사용하세요.
                
                                reason은 반드시 제공된 데이터에서 확인 가능한 사실만 사용하세요.
                
                                예:
                                "최근 4일 동안 초콜릿 섭취가 반복되었고,
                                같은 기간 긁음 횟수도 증가하는 흐름이 함께 관찰됐어요."
                
                                또는
                
                                "최근 보습제 사용 횟수가 3회에서 1회로 감소하는 동안
                                긁음 횟수가 함께 증가했어요."
                
                                특정 요인이 긁음의 직접적인 원인이라고 단정하지 마세요.
                
                                후보가 존재하는 경우:
                                - triggerFactor는 1순위 factor
                                - triggerFactors에는 모든 후보를 전달받은 순서대로 반환
                                - rank는 1부터 시작
                                - type은 제공받은 값을 그대로 반환
                                - factor는 제공받은 값을 그대로 반환
                                - reason만 실제 데이터를 기반으로 생성
                
                                후보가 "후보 없음"인 경우:
                                - triggerFactor는 null
                                - triggerFactors는 빈 배열 []
                                
                                후보의 rank는 백엔드가 실제 최근 7일 데이터를 기반으로
                                계산한 연관 점수(score)를 높은 순서대로 정렬하여 결정했습니다.
                
                                FOOD 후보의 점수는
                                - 최근 7일 반복 등장 빈도
                                - 해당 음식이 기록된 날의 긁음 증가 정도
                
                                를 함께 반영합니다.
                
                                LIFESTYLE 후보의 점수는
                                - 최근 생활습관 변화 정도
                                - 같은 기간 긁음 증가 정도
                
                                를 함께 반영합니다.
                
                                score가 높은 후보가 더 높은 rank를 가집니다.
                
                                이 rank와 score는 백엔드에서 계산된 값이므로
                                절대 순서를 변경하지 마세요.
                
                                reason에는 score, rank 계산 방식, 후보 선정 과정 등
                                내부 분석 절차를 절대 언급하지 마세요.
                
                                reason은 사용자가 기록한 정보에서 직접 관찰할 수 있는
                                사실과 변화만 설명하세요.
                
                                문장은 "왜 이 요인이 후보가 되었는지"가 아니라
                                "어떤 기록과 변화가 함께 관찰되었는지"를 설명하는 방식으로 작성하세요.
                      

                               [응답 형식]
                
                                 반드시 아래 JSON 구조로만 응답하세요.
                
                                 JSON 앞뒤에 설명을 추가하지 마세요.
                                 코드 블록을 사용하지 마세요.
                                 마크다운을 사용하지 마세요.
                
                                 후보가 존재하는 경우 예시:
                
                                 {
                                   "summary": "오늘 상태 요약",
                                   "pattern": "가장 의미 있는 패턴",
                                   "carePoint": "오늘의 관리 포인트",
                                   "triggerFactor": "초콜릿",
                                   "triggerFactors": [
                                     {
                                       "rank": 1,
                                       "type": "FOOD",
                                       "factor": "초콜릿",
                                       "reason": "최근 기록을 기반으로 생성한 설명"
                                     },
                                     {
                                       "rank": 2,
                                       "type": "LIFESTYLE",
                                       "factor": "보습 기록 감소",
                                       "reason": "최근 기록을 기반으로 생성한 설명"
                                     }
                                   ]
                                 }
                
                                 후보가 없는 경우:
                
                                 {
                                   "summary": "오늘 상태 요약",
                                   "pattern": "가장 의미 있는 패턴",
                                   "carePoint": "오늘의 관리 포인트",
                                   "triggerFactor": null,
                                   "triggerFactors": []
                                 }
                                 
                                 reason은 카드 UI에 표시되므로
                                 한 문장으로 작성하고 지나치게 길게 작성하지 마세요.
                                 가능하면 70자 이내로 작성하세요
                """.formatted(
                scratchCount,
                totalScratchSeconds,
                hourlyScratch,
                weeklyScratch,
                meals,
                weeklyMeals,
                triggerCandidatesText,
                dailyNotes,
                temperature != null
                        ? temperature + "℃"
                        : "기록 없음",
                humidity != null
                        ? humidity + "%"
                        : "기록 없음",
                airQuality != null
                        ? airQuality
                        : "기록 없음"
        );

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_5_2)
                        .addUserMessage(prompt)
                        .build();

        var response = openAIClient
                .chat()
                .completions()
                .create(params);

        String content = response
                .choices()
                .stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "AI 분석 결과가 없습니다."
                        )
                );

        // 혹시 모델이 코드블록을 붙였을 때를 대비한 방어 처리
        content = content
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {

            return objectMapper.readValue(
                    content,
                    ReportAiAnalysisDto.class
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "AI 분석 JSON 파싱에 실패했습니다. AI 응답: "
                            + content,
                    e
            );
        }
    }
}