package com.souqrodina.accounting.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
fun InventoryScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    var selectedCategory by remember { mutableStateOf("all") }

    val categories = remember(state.products) {
        listOf("all") + state.products.map { it.category }.distinct()
    }

    val filteredProducts = remember(state.products, selectedCategory, state.searchQuery) {
        state.products.filter { product ->
            val matchCategory = selectedCategory == "all" || product.category == selectedCategory
            val matchSearch = state.searchQuery.isEmpty() ||
                    product.nameAr.contains(state.searchQuery, ignoreCase = true) ||
                    product.nameEn.contains(state.searchQuery, ignoreCase = true) ||
                    product.sku.contains(state.searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCoolGray)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Header Row with Add Product Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (isArabic) "إدارة المخزون والأصناف" else "Inventory & Stock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryNavyDark
                    )
                    Text(
                        "${filteredProducts.size} " + (if (isArabic) "صنف مسجل" else "items listed"),
                        fontSize = 11.sp,
                        color = TextSlateMuted
                    )
                }

                Button(
                    onClick = { /* Open Add Product Dialog */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "إضافة صنف" else "Add Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                if (cat == "all") (if (isArabic) "الكل" else "All") else cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryNavy,
                            selectedLabelColor = SurfaceWhite,
                            containerColor = SurfaceWhite,
                            labelColor = PrimaryNavyDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) PrimaryNavy else BorderSlate,
                            selectedBorderColor = PrimaryNavy,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Product Items List
        items(filteredProducts) { product ->
            ProductCardItem(
                product = product,
                currency = state.currency.symbol,
                isArabic = isArabic,
                onQuickSell = { viewModel.executeSale(product, 1, null) },
                onDelete = { viewModel.deleteProduct(product) }
            )
        }
    }
}

@Composable
fun ProductCardItem(
    product: ProductEntity,
    currency: String,
    isArabic: Boolean,
    onQuickSell: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (isArabic) product.nameAr else product.nameEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavyDark
                    )
                    Text(
                        "${product.sku} • ${product.category}",
                        fontSize = 10.sp,
                        color = TextSlateMuted
                    )
                }

                // Stock Badge
                val isLow = product.quantity in 1..product.minStockAlert
                val isOut = product.quantity <= 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isOut -> DangerRoseLight
                                isLow -> WarningAmberLight
                                else -> AccentEmeraldLight
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        when {
                            isOut -> if (isArabic) "نفذت الكمية" else "Out of Stock"
                            isLow -> if (isArabic) "مخزون منخفض (${product.quantity})" else "Low (${product.quantity})"
                            else -> "${product.quantity} " + (if (isArabic) "قطعة" else "in stock")
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isOut -> DangerRose
                            isLow -> WarningAmber
                            else -> AccentEmerald
                        }
                    )
                }
            }

            // Price & Profit Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text(if (isArabic) "سعر الشراء" else "Cost", fontSize = 9.sp, color = TextSlateMuted)
                        Text(formatCurrency(product.purchasePrice, currency), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSlateDark)
                    }
                    Column {
                        Text(if (isArabic) "سعر البيع" else "Sell Price", fontSize = 9.sp, color = TextSlateMuted)
                        Text(formatCurrency(product.salePrice, currency), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    }
                    Column {
                        Text(if (isArabic) "صافي الربح" else "Profit", fontSize = 9.sp, color = TextSlateMuted)
                        Text(formatCurrency(product.salePrice - product.purchasePrice, currency), fontSize = 12.sp, fontWeight = FontWeight.Black, color = AccentEmerald)
                    }
                }

                // Quick Action Button
                Button(
                    onClick = onQuickSell,
                    enabled = product.quantity > 0,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Icon(Icons.Outlined.PointOfSale, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "بيع سريع (-1)" else "Sell 1", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}