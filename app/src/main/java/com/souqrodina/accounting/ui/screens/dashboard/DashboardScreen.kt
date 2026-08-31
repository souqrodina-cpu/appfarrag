package com.souqrodina.accounting.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.souqrodina.accounting.data.local.db.entities.TransactionEntity
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.data.model.TransactionType
import com.souqrodina.accounting.ui.theme.*
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    var showPinDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCoolGray)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Omnibar Search Field
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                placeholder = {
                    Text(
                        if (isArabic) "بحث عام (أصناف، فواتير، عملاء)..." else "Search products, invoices, clients...",
                        fontSize = 12.sp,
                        color = TextSlateMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = PrimaryNavy)
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = TextSlateMuted)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = PrimaryNavy,
                    unfocusedBorderColor = BorderSlate
                ),
                singleLine = true
            )
        }

        // 2. Capital Security-Locked Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryNavyLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = PrimaryNavy)
                            }
                            Column {
                                Text(
                                    if (isArabic) "رأس المال المستثمر" else "Invested Capital",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSlateMuted
                                )
                                Text(
                                    if (state.isCapitalUnlocked) formatCurrency(state.capitalInvested, state.currency.symbol) else "••••••••",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryNavyDark
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (state.isCapitalUnlocked) viewModel.lockCapital() else showPinDialog = true
                            }
                        ) {
                            Icon(
                                if (state.isCapitalUnlocked) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = if (state.isCapitalUnlocked) AccentEmerald else PrimaryNavy
                            )
                        }
                    }
                }
            }
        }

        // 3. Live Financial Counters
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Total Sales
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(if (isArabic) "إجمالي المبيعات" else "Total Sales", fontSize = 10.sp, color = TextSlateMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatCurrency(state.totalSales, state.currency.symbol), fontSize = 15.sp, fontWeight = FontWeight.Black, color = PrimaryNavy)
                    }
                }

                // Net Profit (Emerald)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentEmeraldLight)
                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(if (isArabic) "صافي الأرباح" else "Net Profit", fontSize = 10.sp, color = AccentEmeraldDark, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatCurrency(state.netProfit, state.currency.symbol), fontSize = 15.sp, fontWeight = FontWeight.Black, color = AccentEmerald)
                    }
                }

                // Total Expenses
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(if (isArabic) "المصروفات" else "Expenses", fontSize = 10.sp, color = TextSlateMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatCurrency(state.totalExpenses, state.currency.symbol), fontSize = 15.sp, fontWeight = FontWeight.Black, color = WarningAmber)
                    }
                }
            }
        }

        // 4. Recent Transactions Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isArabic) "أحدث المعاملات" else "Recent Transactions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavyDark
                )
                Text(
                    "${state.transactions.size} " + (if (isArabic) "عملية" else "records"),
                    fontSize = 11.sp,
                    color = TextSlateMuted
                )
            }
        }

        if (state.transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isArabic) "لا توجد معاملات مسجلة في هذا الموسم" else "No transactions in this season",
                        fontSize = 12.sp,
                        color = TextSlateMuted
                    )
                }
            }
        } else {
            items(state.transactions.take(15)) { tx ->
                TransactionRowItem(tx = tx, currency = state.currency.symbol, isArabic = isArabic, onVoid = { viewModel.voidTransaction(tx) })
            }
        }
    }
}

@Composable
fun TransactionRowItem(tx: TransactionEntity, currency: String, isArabic: Boolean, onVoid: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (tx.type) {
                                TransactionType.SALE -> AccentEmeraldLight
                                TransactionType.EXPENSE -> WarningAmberLight
                                else -> PrimaryNavyLight
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (tx.type) {
                            TransactionType.SALE -> Icons.Outlined.ArrowUpward
                            TransactionType.EXPENSE -> Icons.Outlined.ArrowDownward
                            else -> Icons.Outlined.SwapHoriz
                        },
                        contentDescription = null,
                        tint = when (tx.type) {
                            TransactionType.SALE -> AccentEmerald
                            TransactionType.EXPENSE -> WarningAmber
                            else -> PrimaryNavy
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        if (isArabic) tx.titleAr else tx.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tx.isVoided) TextSlateMuted else PrimaryNavyDark
                    )
                    Text(
                        "${tx.date} • ${tx.time}" + (if (tx.invoiceNumber != null) " • #${tx.invoiceNumber}" else ""),
                        fontSize = 10.sp,
                        color = TextSlateMuted
                    )
                }
            }

            Text(
                formatCurrency(tx.amount, currency),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = when {
                    tx.isVoided -> TextSlateMuted
                    tx.type == TransactionType.SALE -> AccentEmerald
                    else -> WarningAmber
                }
            )
        }
    }
}

fun formatCurrency(amount: Double, currencyCode: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0
    return "${formatter.format(amount)} $currencyCode"
}