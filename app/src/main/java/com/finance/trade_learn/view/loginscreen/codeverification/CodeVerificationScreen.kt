package com.finance.trade_learn.view.loginscreen.codeverification

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.view.LocalCodeVerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeVerificationScreen(
    userEmail : String,
    onVerifyCode: () -> Unit
) {
    val coroutines = rememberCoroutineScope()

    val context = LocalContext.current
    val viewModel = LocalCodeVerificationViewModel.current

    val verificationViewState by viewModel.verificationViewState.collectAsState()
    val verificationCodeResponse by viewModel.verificationCodeResponse.collectAsState()
    val sendCodeResponse by viewModel.sendCodeResponse.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        viewModel.changeEmail(email = userEmail)
    }

    LaunchedEffect(verificationCodeResponse.success){
        if (verificationCodeResponse.success == true) {
            Toast.makeText(context, verificationCodeResponse.message, Toast.LENGTH_LONG).show()
            onVerifyCode.invoke()
        } else if (verificationCodeResponse.success == false){
            Toast.makeText(context, verificationCodeResponse.message ?: verificationCodeResponse.error?.message ?: "error", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(sendCodeResponse.success){
        if (sendCodeResponse.success == true) {
            Toast.makeText(context, sendCodeResponse.data ?: sendCodeResponse.message, Toast.LENGTH_LONG).show()
        } else if (sendCodeResponse.success == false){
            Toast.makeText(context, verificationCodeResponse.message ?: verificationCodeResponse.error?.message ?: "error", Toast.LENGTH_LONG).show()
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Arka plan rengi
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Verification Code",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5) // Mavi tonlarında başlık rengi
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = verificationViewState.verificationCode,
                onValueChange = {
                    viewModel.changeVerificationCodeText(it)
                },
                placeholder = { Text("Verification Code") },
                modifier = Modifier.fillMaxWidth()
                    .border(
                        1.dp,
                        verificationViewState.codeBorderColor,
                        RoundedCornerShape(6.dp)
                    ),
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

            OutlinedTextField(
                value = verificationViewState.email,
                onValueChange = {
                    viewModel.changeEmail(it)
                },
                placeholder = { androidx.compose.material.Text("Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.Gray,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.Gray,
                    textColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için
                    unfocusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için,

                    placeholderColor = Color.Gray
                ),
                enabled = false
            )

            Spacer(modifier = Modifier.height(20.dp))




            OutlinedTextField(
                value = verificationViewState.password,
                onValueChange = {
                    viewModel.changePasswordText(it)
                },
                placeholder = { androidx.compose.material.Text("Password") },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        verificationViewState.passwordBorderColor,
                        RoundedCornerShape(6.dp)
                    ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.Gray,

                    textColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için
                    unfocusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için

                    placeholderColor = Color.Gray,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = verificationViewState.confirmPassword,
                onValueChange = {
                    viewModel.changeConfirmPasswordText(it)
                },
                placeholder = { androidx.compose.material.Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        verificationViewState.confirmPasswordBorderColor,
                        RoundedCornerShape(6.dp)
                    ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.Gray,

                    textColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    placeholderColor = Color.Gray,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    viewModel.onVerifyCode()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5)
                )
            ) {
                Text(text = "Verify", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Didn't receive the code?",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Resend Code",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5) // Mavi tonlarında yeniden gönderme metni
                ),
                modifier = Modifier
                    .clickable { viewModel.sendResetPasswordCode() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeVerificationScreenPreview() {
    CodeVerificationScreen(
        userEmail  = "",
        onVerifyCode = {}
    )
}