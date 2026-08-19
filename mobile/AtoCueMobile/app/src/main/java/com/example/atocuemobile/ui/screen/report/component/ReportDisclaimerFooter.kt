package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportDisclaimerFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp, vertical = 20.dp) // 좌우 24dp 패딩
    ) {
        Text(
            text = "*AI 리포트는 아이의 상태를 기록하고 관리하는 데 도움을 주는 보조 자료이며, 전문의 진단이나 전문적인 소견을 대신하지 않습니다.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "*리포트 내용을 바탕으로 의료진에게 질문하거나 진료 시 참고할 수 있지만, 증상이 지속되거나 악화될 경우 반드시 전문의와 상담해 주세요.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 18.sp
        )
    }
}