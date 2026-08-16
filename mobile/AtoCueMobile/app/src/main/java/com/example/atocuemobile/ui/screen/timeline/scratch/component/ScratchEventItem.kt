package com.example.atocuemobile.ui.screen.timeline.scratch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.CardBackground
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent

@Composable
fun ScratchEventItem(event: ScratchEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = event.level.iconRes),
                    contentDescription = event.level.label,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.level.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = event.level.color
                )
            }
            Text(
                text = "발생시각 | ${event.startTime}~${event.endTime}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${event.durationMinutes}분",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "지속",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}