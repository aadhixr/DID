package com.diskwala.recreated.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.diskwala.recreated.data.local.DownloadEntity
import com.diskwala.recreated.data.local.RecentFileEntity
import com.diskwala.recreated.ui.theme.DarkCard
import com.diskwala.recreated.ui.theme.TextPrimary
import com.diskwala.recreated.ui.theme.TextSecondary

@Composable
fun DramaScreen(onPlayDrama: (String) -> Unit) {
    val sampleDramas = remember {
        listOf("Drama Series Episode 1", "Episode 2: The Encounter", "Episode 3: Climax")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sampleDramas) { title ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlayDrama(title) },
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Tap to stream", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadsScreen(downloads: List<DownloadEntity>, onPlayLocal: (String) -> Unit) {
    if (downloads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No downloaded files yet", color = TextSecondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(downloads) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayLocal(item.savedUri) },
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${item.sizeBytes / (1024 * 1024)} MB • ${item.ext}",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YouScreen(recentFiles: List<RecentFileEntity>, onClearHistory: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Recent Watch History", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (recentFiles.isEmpty()) {
            Text(text = "No watch history available", color = TextSecondary)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recentFiles) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = file.name, color = TextPrimary)
                        Text(text = "${file.views} views", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
