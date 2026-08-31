package com.souqrodina.accounting.ui.screens.directories

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
import com.souqrodina.accounting.data.local.db.entities.ContactEntity
import com.souqrodina.accounting.data.model.ContactType
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.ui.screens.dashboard.formatCurrency
import com.souqrodina.accounting.ui.theme.*
import com.souqrodina.accounting.ui.viewmodel.AccountingViewModel

@Composable
fun DirectoriesScreen(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val isArabic = state.language == Language.AR

    var selectedTab by remember { mutableStateOf(ContactType.CUSTOMER) }

    val filteredContacts = remember(state.contacts, selectedTab, state.searchQuery) {
        state.contacts.filter { contact ->
            val matchType = contact.type == selectedTab
            val matchSearch = state.searchQuery.isEmpty() ||
                    contact.name.contains(state.searchQuery, ignoreCase = true) ||
                    contact.phone.contains(state.searchQuery)
            matchType && matchSearch
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
        // Tab Selector Row (Customers vs Suppliers)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { selectedTab = ContactType.CUSTOMER },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == ContactType.CUSTOMER) PrimaryNavy else SurfaceWhite,
                        contentColor = if (selectedTab == ContactType.CUSTOMER) SurfaceWhite else PrimaryNavyDark
                    )
                ) {
                    Text(if (isArabic) "دليل العملاء" else "Customers", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { selectedTab = ContactType.SUPPLIER },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == ContactType.SUPPLIER) PrimaryNavy else SurfaceWhite,
                        contentColor = if (selectedTab == ContactType.SUPPLIER) SurfaceWhite else PrimaryNavyDark
                    )
                ) {
                    Text(if (isArabic) "دليل الموردين" else "Suppliers", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Contact Items
        items(filteredContacts) { contact ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                        Text(contact.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryNavyDark)
                        Text(contact.phone, fontSize = 10.sp, color = TextSlateMuted)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isArabic) "الرصيد / المستحق" else "Balance", fontSize = 9.sp, color = TextSlateMuted)
                        Text(formatCurrency(contact.balance, state.currency.symbol), fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (contact.balance > 0) DangerRose else AccentEmerald)
                    }
                }
            }
        }
    }
}