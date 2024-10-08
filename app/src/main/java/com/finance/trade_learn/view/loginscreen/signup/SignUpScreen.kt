package com.finance.trade_learn.view.loginscreen.signup

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.view.LocalSingUpViewModel
import com.finance.trade_learn.view.commonui.SimpleBackButtonHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(onSignUpCompleted: () -> Unit, onBackToLogin: () -> Unit) {
    val coroutines = rememberCoroutineScope()


    val context = LocalContext.current

    val viewModel = LocalSingUpViewModel.current
    val signUpViewState by viewModel.signUpViewState.collectAsState()
    val userSignUpResponse by viewModel.userSignUpResponse.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(userSignUpResponse.success){
        if (userSignUpResponse.success == true) {
            Toast.makeText(context, userSignUpResponse.message, Toast.LENGTH_LONG).show()
            coroutines.launch {
                delay(1000)
                onSignUpCompleted.invoke()
            }
        } else if (userSignUpResponse.success == false){
            Toast.makeText(context, userSignUpResponse.message ?: userSignUpResponse.error?.message ?: "error", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            SimpleBackButtonHeader(
                title = stringResource(id = R.string.create_account_text),
                onBackClick = {
                    onBackToLogin.invoke()
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, Color.Yellow, RoundedCornerShape(46))
                    .background(Color(0xFFFFF9C4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️ " + stringResource(id = R.string.create_account_information),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.create_account_text),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = signUpViewState.email,
                    onValueChange = {
                        viewModel.changeEmail(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.email_address)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, signUpViewState.emailBorderColor, RoundedCornerShape(6.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.Gray,
                        textColor = MaterialTheme.colors.onPrimary,
                        focusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için
                        unfocusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için,

                        placeholderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = signUpViewState.nameAndSurname,
                    onValueChange = {
                        viewModel.changeUserNameAndSurname(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.name_and_surname_text)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            signUpViewState.nameAndSurnameBorder,
                            RoundedCornerShape(6.dp)
                        ),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.Gray,
                        textColor = MaterialTheme.colors.onPrimary,
                        focusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için
                        unfocusedIndicatorColor = Color.Transparent, // Border kalınlığını sabitlemek için,

                        placeholderColor = Color.Gray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))


                OutlinedTextField(
                    value = signUpViewState.password,
                    onValueChange = {
                        viewModel.changePasswordText(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.password)) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, signUpViewState.passwordBorderColor, RoundedCornerShape(6.dp)),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.Gray,

                        textColor = MaterialTheme.colors.onPrimary,
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
                    value = signUpViewState.confirmPassword,
                    onValueChange = {
                        viewModel.changeConfirmPasswordText(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.confirm_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            signUpViewState.confirmPasswordBorderColor,
                            RoundedCornerShape(6.dp)
                        ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.Gray,

                        textColor = MaterialTheme.colors.onPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                        placeholderColor = Color.Gray,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.signUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1E88E5),
                        disabledBackgroundColor = Color(0xFF1E88E5).copy(alpha = 0.5f)
                    ),
                    enabled = signUpViewState.credentialsIsValid && !signUpViewState.isLoading,
                ) {
                    Text(stringResource(id = R.string.sign_up), color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text =  stringResource(id = R.string.back_to_login),
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
fun SignUpScreenPreview() {
    SignUpScreen(onSignUpCompleted = {}, onBackToLogin = {})
}
