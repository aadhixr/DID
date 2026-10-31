package com.diskwala.recreated.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.diskwala.recreated.ui.theme.DarkCard
import com.diskwala.recreated.ui.theme.DarkSurface
import com.diskwala.recreated.ui.theme.TextPrimary
import com.diskwala.recreated.ui.theme.TextSecondary

@Composable
fun StreamInputScreen(
    onStreamUrl: (String) -> Unit
) {
    var inputUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DiskWala Player",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = "Paste any video or DiskWala link to start streaming directly",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = {
                        inputUrl = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Stream URL or Link") },
                    placeholder = { Text("https://www.diskwala.com/app/...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmed = inputUrl.trim()
                        if (trimmed.isEmpty()) {
                            errorMessage = "Please enter or paste a valid link"
                        } else {
                            // Extract ID if a full web URL was pasted
                            val resolvedTarget = if (trimmed.contains("/app/")) {
                                trimmed.substringAfter("/app/").substringBefore("?").substringBefore("/")
                            } else {
                                trimmed
                            }
                            onStreamUrl(resolvedTarget)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Stream Now", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick sample test button
        TextButton(
            onClick = {
                onStreamUrl("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            }
        ) {
            Text("Test with Sample Video Stream", color = TextSecondary)
        }
    }
}
