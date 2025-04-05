package com.example.testwalletbinance.presentation.screen_wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import com.example.testwalletbinance.utils.DateUtils.formatTimestamp

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.category?:"", fontWeight = FontWeight.Bold)
                Text(text = formatTimestamp(transaction.timestamp), fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${transaction.amount} BTC",
                color = if (transaction.category == TransactionCategory.TOP_UP.displayName) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
