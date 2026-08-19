package com.example.atocuemobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.OnboardingRequest
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    var registeredChildId by mutableStateOf<Long?>(null)
        private set

    // 1. 보호자 정보
    var parentName by mutableStateOf("")

    // 2. 아이 정보
    var childName by mutableStateOf("")
    var birthDate by mutableStateOf("") // YYYY-MM-DD 포맷
    var height by mutableStateOf("")
    var weight by mutableStateOf("")

    // 3. 피부 질환 목록
    val selectedConditions = mutableStateListOf<String>()

    // 4. 특이사항
    var specialNote by mutableStateOf("")

    // 백엔드 전송 상태
    var isLoading by mutableStateOf(false)

    fun submitOnboarding(
        jwtToken: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 💡 이미 요청 중이면 중복 실행 절대 불가 (DB Lock 방지 핵심)
        if (isLoading) return

        isLoading = true
        viewModelScope.launch {
            try {
                val actualToken = jwtToken.ifBlank { RetrofitClient.accessToken ?: "" }
                val tokenHeader = if (actualToken.startsWith("Bearer ")) actualToken else "Bearer $actualToken"

                val request = OnboardingRequest(
                    parentName = parentName,
                    childName = childName,
                    birthDate = birthDate,
                    height = height.toDoubleOrNull() ?: 0.0,
                    weight = weight.toDoubleOrNull() ?: 0.0,
                    skinConditions = selectedConditions.toList(),
                    specialNote = specialNote
                )

                val response = RetrofitClient.api.saveChildInfo(
                    token = tokenHeader,
                    request = request
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        registeredChildId = body.childId
                        onSuccess()
                    } else {
                        onError("아이 등록 응답이 비어 있습니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    onError("등록 실패 (${response.code()}): $errorBody")
                }
            } catch (e: Exception) {
                onError("네트워크 오류: ${e.localizedMessage}")
            } finally {
                // 💡 성공하든 실패하든 로딩 상태 해제 (단, 화면 전환이 성공하면 뷰모델이 유지되므로 상태 관리 유의)
                isLoading = false
            }
        }
    }
}