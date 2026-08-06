
package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 추천 제품/누적 데이터 모델
data class RecommendedProduct(
    val rank: Int,
    val name: String,   // 예: "유제품"
    val count: String   // 예: "00회"
)

@Composable
fun RecommendedProductList(
    products: List<RecommendedProduct>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text("유제품과 알가리 제품을 유의하는 걸 추천해요")
        Text("0월 00일 기준")

        // TODO: products.forEach로 실제 렌더링. 지금은 구조만
        products.forEach { product ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text("${product.rank}위")
                Text(product.name, modifier = Modifier.weight(1f))
                Text(product.count)
            }
        }
    }
}