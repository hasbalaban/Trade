package com.finance.trade_learn.view.loginscreen.login

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
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
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.FirebaseLogEvents
import com.finance.trade_learn.utils.saveStringPreference
import com.finance.trade_learn.view.LocalLoginViewModel
import com.finance.trade_learn.view.LocalSingUpViewModel
import com.finance.trade_learn.view.commonui.SimpleBackButtonHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    goBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutines = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }

    val viewModel = LocalLoginViewModel.current

    val loginViewState by viewModel.loginViewState.collectAsState()
    val userLoginResponse by viewModel.userLoginResponse.collectAsState()

    LaunchedEffect(userLoginResponse.success){
        if (userLoginResponse.success == true) {
            Toast.makeText(context, userLoginResponse.message, Toast.LENGTH_LONG).show()
            coroutines.launch {
                context.saveStringPreference(DataStoreKeys.StringKeys.email, loginViewState.email)
                context.saveStringPreference(DataStoreKeys.StringKeys.password, loginViewState.password)

                BaseViewModel.updateUserInfo(userLoginResponse)
                BaseViewModel.updateUserLoginStatus(true)

                onLogin.invoke()
            }
        } else if (userLoginResponse.success == false){

            Toast.makeText(context, userLoginResponse.message ?: userLoginResponse.error?.message ?: "error", Toast.LENGTH_LONG).show()
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            SimpleBackButtonHeader(
                title = stringResource(id = R.string.login),
                onBackClick = {
                    goBack.invoke()
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
                    text =  stringResource(id = R.string.welcome_back),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
                    value = loginViewState.email,
                    onValueChange = {
                        viewModel.changeEmail(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.email_address)) },
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
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )


                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = loginViewState.password,
                    onValueChange = {
                        viewModel.changePasswordText(it)
                    },
                    placeholder = { Text(stringResource(id = R.string.password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
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
                        FirebaseLogEvents.logEvent("logClickLoginEvent")
                        viewModel.login()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1E88E5),
                        disabledBackgroundColor = Color(0xFF1E88E5).copy(alpha = 0.5f)
                    ),
                    enabled = loginViewState.credentialsIsValid
                ) {
                    Text(stringResource(id = R.string.login), color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text =  stringResource(id = R.string.forgot_password),
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable { onForgotPassword() },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text =  stringResource(id = R.string.dont_have_an_accounr), color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text =  stringResource(id = R.string.sign_up),
                        color = Color(0xFF1E88E5),
                        modifier = Modifier.clickable {
                            FirebaseLogEvents.logEvent("logClickSignUpEvent")
                            onSignUp()
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLogin = {}, onSignUp = {}, onForgotPassword = {}, goBack = {})
}