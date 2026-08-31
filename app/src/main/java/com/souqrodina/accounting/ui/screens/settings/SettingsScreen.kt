package com.souqrodina.accounting.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.souqrodina.accounting.data.model.Currency
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.ui.theme.*
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel

@Composable
fun SettingsScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCoolGray)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Google Drive Cloud Sync Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.CloudSync, contentDescription = null, tint = PrimaryNavy)
                            }
                            Column {
                                Text(
                                    if (isArabic) "المزامنة السحابية عبر Google Drive" else "Google Drive Cloud Backup",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavyDark
                                )
                                Text(
                                    if (isArabic) "مزامنة تلقائية صامتة عند كل معاملة" else "Silent Auto-Sync on every transaction",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { /* Launch Google Drive Sync */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                    ) {
                        Icon(Icons.Outlined.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isArabic) "مزامنة قاعدة البيانات الآن" else "Sync Database Now")
                    }
                }
            }
        }

        // 2. Business Profile & Localization Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (isArabic) "تخصيص النشاط والعملة واللغة" else "Business, Currency & Language",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavyDark
                    )

                    // Currency Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Currency.values().forEach { curr ->
                            OutlinedButton(
                                onClick = { viewModel.setCurrency(curr) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (state.currency == curr) PrimaryNavy else Color(0xFFF8FAFC),
                                    contentColor = if (state.currency == curr) SurfaceWhite else PrimaryNavyDark
                                )
                            ) {
                                Text(curr.code, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Language Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setLanguage(Language.AR) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (state.language == Language.AR) PrimaryNavy else Color(0xFFF8FAFC),
                                contentColor = if (state.language == Language.AR) SurfaceWhite else PrimaryNavyDark
                            )
                        ) {
                            Text("العربية (RTL)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.setLanguage(Language.EN) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (state.language == Language.EN) PrimaryNavy else Color(0xFFF8FAFC),
                                contentColor = if (state.language == Language.EN) SurfaceWhite else PrimaryNavyDark
                            )
                        ) {
                            Text("English (LTR)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Danger Zone: Factory Reset / Financial Season Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DangerRoseLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.RestartAlt, contentDescription = null, tint = DangerRose)
                        }
                        Column {
                            Text(
                                if (isArabic) "بدء موسم مالي جديد" else "Start New Financial Season",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF881337)
                            )
                            Text(
                                if (isArabic) "تصفير المعاملات وبدء دورة محاسبية جديدة" else "Archive transactions and start fresh season",
                                fontSize = 10.sp,
                                color = Color(0xFF9F1239)
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.resetSeason() },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRose),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isArabic) "تصفير" else "Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}