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
            String hourlyScratch,
            String weeklyScratch,
            String meals,
            String weeklyMeals,
            String dailyNotes,
            Double temperature,
            Integer humidity,
            String airQuality
    ){

        String prompt = """
                당신은 피부 상태와 생활 패턴을 함께 살펴보는 개인 건강 리포트 AI입니다.
                
                        사용자의 긁음 데이터만을 중심으로 분석하지 말고,
                        식단, 날씨, 샤워, 보습, 긁음 기록을 동일한 중요도로 종합하여
                        오늘 사용자에게 가장 의미 있는 생활 패턴 또는 관리 포인트를 찾아주세요.
                
                        목표는 특정 자극원을 반드시 찾아내는 것이 아닙니다.
                        명확한 연관성이 없으면 자극원을 억지로 제시하지 말고,
                        현재 상태를 자연스럽게 요약하고 오늘 실천할 수 있는 관리 행동을 제안하세요.

                [오늘 긁음 기록]
                
                긁음 횟수: %d회
                총 긁음 시간: %.1f초
                
                [오늘 시간대별 긁음]
                %s
                
                [최근 7일 긁음 기록]
                %s
                
                [오늘 식단]
                %s
                
                [최근 7일 식단]
                %s
                
                [오늘 생활 기록]
                %s
                
                [오늘 환경 정보]
                
                기온: %s
                습도: %s
                미세먼지 상태: %s
                
                [분석 방법]
                
                - 먼저 오늘의 긁음 횟수와 총 긁음 시간을 최근 7일 기록과 비교하세요.
                - 오늘 긁음이 평소보다 증가했는지 확인하세요.
                - 특정 시간대에 긁음이 집중되었는지도 확인하세요.
                - 식단을 분석하세요.
                  오늘 먹은 음식이 최근 7일 중 다른 날짜에도 등장했는지 확인하세요.
                  같은 음식을 먹은 날의 긁음 기록을 비교하세요.
                  특정 음식이 긁음이 많았던 날 반복적으로 등장하는 경우에만 후보로 고려하세요.
                  단 한 번 같이 등장한 음식은 의미 있는 자극 요인으로 판단하지 마세요.
                - 환경 정보를 분석하세요.
                  오늘의 기온, 습도, 미세먼지 상태를 확인하세요.
                  단, 현재 제공된 데이터만으로 과거 환경과 비교할 수 없다면
                   최근 7일 동안 같은 환경이 지속되었다고 추정하지 마세요.
                - 생활 기록을 확인하세요.
                  생활 기록에 샤워, 보습 등 피부 관리와 관련된 정보가 있다면 함께 고려하세요.
                -기록에 없는 행동이나 조건은 추측하지 마세요.
                -각 후보에 대해 다음 기준을 고려하세요.
                 긁음 증가와 시간적으로 함께 나타났는가?
                 동일한 패턴이 여러 날 반복되었는가?
                 긁음이 적었던 날과 비교했을 때 차이가 있는가?
                 다른 요인도 동시에 변하여 하나의 요인만으로 설명하기 어려운가?
                - 근거가 가장 강한 요인을 최대 1개만 선정하세요.
                - 충분한 근거가 없다면 억지로 자극 요인을 선정하지 마세요.
   
                [중요한 분석 원칙]
                
                        상관관계와 인과관계를 구분하세요.
                        음식이나 환경 요인을 긁음의 직접적인 원인이라고 단정하지 마세요.
                        한 번의 동시 발생만으로 자극 요인이라고 판단하지 마세요.
                        데이터에 존재하지 않는 정보를 추측하거나 만들어내지 마세요.
                        의학적 진단, 알레르기 진단 또는 질환 판단을 하지 마세요.
                        데이터가 부족하면 "현재 기록만으로 뚜렷한 연관성을 판단하기 어렵습니다."라고 명시하세요.
                        여러 후보를 억지로 나열하지 마세요.
                        사용자가 이해하기 쉽도록 간결하게 설명하세요.
                        
                [분석 우선순위]
                
                                                            1. 최근 7일의 전체 생활 패턴을 먼저 살펴보세요.
                                                               - 날씨 변화
                                                               - 식단 변화
                                                               - 샤워 횟수 변화
                                                               - 보습 횟수 변화
                                                               - 긁음 패턴 변화
                
                                                            2. 긁음 횟수와 시간은 상태를 판단하는 하나의 지표로만 사용하세요.
                                                               분석의 대부분을 긁음 숫자 설명에 사용하지 마세요.
                
                                                            3. 생활 데이터 중 평소와 달라진 부분이 있는지 확인하세요.
                
                                                            4. 특정 음식, 날씨, 샤워, 보습 변화가 긁음 증가와 반복적으로 같이 나타났다면
                                                               이를 '주목할 요인'으로 제시할 수 있습니다.
                
                                                            5. 반복적인 연관성이 없다면
                                                               '뚜렷한 자극 요인은 확인되지 않았다'고 간단히 설명하고
                                                               대신 오늘의 관리 포인트를 제안하세요.
                
                                                            6. 최근 7일의 모든 숫자를 사용자에게 나열하지 마세요.
                                                               숫자는 판단 근거로 내부적으로 활용하고,
                                                               실제 출력에는 꼭 필요한 숫자만 사용하세요.
                
                                                            [출력 스타일]
                
                                                            의료 보고서처럼 딱딱하게 작성하지 마세요.
                                                            모바일 건강 관리 앱의 일일 리포트처럼
                                                            짧고 자연스럽고 이해하기 쉬운 문장으로 작성하세요.
                
                                                            사용자에게 불안감을 줄 수 있는 표현,
                                                            질환 진단,
                                                            특정 음식이나 환경을 직접적인 원인으로 단정하는 표현은 사용하지 마세요.
                
                                                            [출력 형식]
                
                                                            오늘의 한줄 요약:
                                                            [오늘 상태를 자연어로 1문장]
                
                                                            주목할 패턴:
                                                            [식단 / 날씨 / 샤워 / 보습 / 긁음 중 가장 의미 있는 변화 1개]
                
                                                            오늘의 관리 포인트:
                                                            [사용자가 오늘 실천할 수 있는 행동 1개]
                
                                                            필요한 경우에만:
                                                            자극 요인 후보:
                                                            [반복적인 연관성이 있을 경우에만 1개]
                        
        """.formatted(
                scratchCount,
                totalScratchSeconds,
                hourlyScratch,
                weeklyScratch,
                meals,
                weeklyMeals,
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