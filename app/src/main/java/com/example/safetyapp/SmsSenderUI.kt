package com.example.safetyapp

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.filled.Mic

@Composable
fun SmsSenderUI(
    onSendSmsClick: (String) -> Unit,
    onMicClick: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Safety App", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Enter Phone Number") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = {
                    isLoading = true
                    onSendSmsClick(phoneNumber)
                    Handler(Looper.getMainLooper()).postDelayed({
                        isLoading = false
                    }, 1000)
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Send SMS")
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = onMicClick) {
                Icon(Icons.Default.Mic, contentDescription = "Mic")
            }
        }
    }
}