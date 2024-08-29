package com.finance.trade_learn.view.loginscreen.forgotpassword

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.view.LocalForgotPasswordViewModel
import com.finance.trade_learn.view.LocalSingUpViewModel
import com.finance.trade_learn.view.commonui.SimpleBackButtonHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onResetPassword: () -> Unit, onBackToLogin: () -> Unit) {

    val coroutines = rememberCoroutineScope()
    val context = LocalContext.current

    val viewModel = LocalForgotPasswordViewModel.current
    val forgotPasswordViewState by viewModel.forgotPasswordViewState.collectAsState()
    val sendCodeResponse by viewModel.sendCodeResponse.collectAsState()


    LaunchedEffect(sendCodeResponse.success){
        if (sendCodeResponse.success == true) {
            Toast.makeText(context, sendCodeResponse.message, Toast.LENGTH_LONG).show()
            coroutines.launch {
                onResetPassword.invoke()
                viewModel.clearCodeResponse()
            }
        } else if (sendCodeResponse.success == false){
            Toast.makeText(context, sendCodeResponse.message ?: sendCodeResponse.error?.message ?: "error", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            SimpleBackButtonHeader(
                title = "Reset Pasword",
                onBackClick = {
                    onBackToLogin.invoke()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Reset Password",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = forgotPasswordViewState.email,
                    onValueChange = {
                        viewModel.changeEmailText(it)
                    },
                    placeholder = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
                    singleLine = true,

                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.Gray,
                        focusedLabelColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray,
                        textColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için
                        unfocusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için,

                        placeholderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.sendResetPasswordCode()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)) // Mavi tonunda buton
                ) {
                    Text("Reset Password", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Back to Login",
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable { onBackToLogin() },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }



}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(onResetPassword = {}, onBackToLogin = {})
}
