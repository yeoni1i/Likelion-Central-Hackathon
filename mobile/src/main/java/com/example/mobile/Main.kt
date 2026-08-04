package com.example.mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // 하단바 View 불러오기
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 탭 클릭 이벤트 처리
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // TODO: 홈 화면 Fragment 연결
                    true
                }
                R.id.navigation_timeline -> {
                    // TODO: 타임라인 화면 Fragment 연결
                    true
                }
                R.id.navigation_report -> {
                    // TODO: 리포트 화면 Fragment 연결
                    true
                }
                R.id.navigation_my -> {
                    // TODO: 마이페이지 화면 Fragment 연결
                    true
                }
                else -> false
            }
        }
    }
}