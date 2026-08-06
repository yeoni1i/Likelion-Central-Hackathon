package com.example.atocuemobile.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.atocuemobile.R


import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview


enum class BottomNavTab(val label: String, val iconRes: Int) {
    HOME("홈", R.drawable.ic_home),
    TIMELINE("타임라인", R.drawable.ic_calendar),
    REPORT("리포트", R.drawable.ic_document),
    MY("마이", R.drawable.ic_user)
}

private val SelectedColor = Color(0xFF5398FF)
private val UnselectedColor = Color(0xFFB0B5C1)

@Composable
fun BottomNavigationBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        BottomNavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SelectedColor,
                    selectedTextColor = SelectedColor,
                    unselectedIconColor = UnselectedColor,
                    unselectedTextColor = UnselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }
    BottomNavigationBar(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it }
    )
}