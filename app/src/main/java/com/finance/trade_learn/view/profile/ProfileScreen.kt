package com.finance.trade_learn.view.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.clearSpecificPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogOut : () -> Unit
) {
    val context = LocalContext.current
    val coroutines = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E)) // Dark background to match cryptocurrency theme
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            imageVector = Icons.Filled.AccountCircle, // Replace with actual image resource
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "John Doe", // Replace with dynamic name
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "john.doe@example.com", // Replace with dynamic email
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = {
                coroutines.launch {
                    context.clearSpecificPreference(DataStoreKeys.StringKeys.email)
                    context.clearSpecificPreference(DataStoreKeys.StringKeys.password)
                    deleteAccount()
                    delay(2500)
                    Toast.makeText(context, "Account deleted", Toast.LENGTH_LONG).show()
                    onLogOut.invoke()
                }

            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Account", color = Color.White)
        }


        // Logout Button
        Button(
            onClick = {
                coroutines.launch {
                    context.clearSpecificPreference(DataStoreKeys.StringKeys.email)
                    context.clearSpecificPreference(DataStoreKeys.StringKeys.password)
                    onLogOut.invoke()
                }

            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

@Composable
fun ActionButton(text: String) {
    Button(
        onClick = { /* Handle button click */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2C2C2E))
    ) {
        Text(text, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogOut = {})
}


fun deleteAccount(){

}