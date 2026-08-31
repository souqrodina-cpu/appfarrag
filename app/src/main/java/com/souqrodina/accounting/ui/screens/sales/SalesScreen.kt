package com.souqrodina.accounting.ui.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.souqrodina.accounting.data.local.db.entities.ProductEntity
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.ui.screens.dashboard.formatCurrency
import com.souqrodina.accounting.ui.theme.*
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel

@Composable
fun SalesScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantity by remember { mutableIntStateOf(1) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCoolGray)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (isArabic) "نقطة البيع السريعة (POS)" else "Quick POS Terminal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryNavyDark
                    )
                    Text(
                        if (isArabic) "اختر الصنف لتسجيل فاتورة بيع مباشرة وتحديث الأرباح فورياً" else "Select item to execute sale and update profit counters",
                        fontSize = 11.sp,
                        color = TextSlateMuted
                    )
                }
            }
        }

        items(state.products.filter { it.quantity > 0 }) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(if (isArabic) product.nameAr else product.nameEn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryNavyDark)
                        Text("${product.quantity} " + (if (isArabic) "قطعة متوفرة" else "in stock"), fontSize = 10.sp, color = AccentEmerald, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(formatCurrency(product.salePrice, state.currency.symbol), fontSize = 13.sp, fontWeight = FontWeight.Black, color = PrimaryNavy)
                        Button(
                            onClick = { viewModel.executeSale(product, 1, null) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                        ) {
                            Text(if (isArabic) "بيع الآن" else "Sell", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}