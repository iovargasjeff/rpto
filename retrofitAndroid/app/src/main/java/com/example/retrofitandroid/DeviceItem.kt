package com.example.retrofitandroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeviceItemView(device: Device, enabled: Boolean, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(text = "ID: ${device.id}")
            Text(text = device.name)
            Text(text = device.data?.color ?: "-")
            Text(text = device.data?.capacity ?: "-")
        }
        IconButton(onClick = onDelete, enabled = enabled) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar ${device.name}"
            )
        }
    }
    HorizontalDivider()
}
