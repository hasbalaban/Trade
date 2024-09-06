package com.finance.trade_learn.view.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.clearSpecificPreference
import com.finance.trade_learn.view.LocalProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogOut: () -> Unit,
    goTransactionScreen: () -> Unit,
    goWalletScreen: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    val coroutines = rememberCoroutineScope()
    val viewModel = LocalProfileViewModel.current

    val userInfo by BaseViewModel.userInfo.collectAsState()
    val signUpViewState by viewModel.profileViewState.collectAsState()
    val accountDeletingResponse by viewModel.accountDeletingResponse.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUserEmail(context)
    }

    LaunchedEffect(accountDeletingResponse.success) {
        if (accountDeletingResponse.success == true) {
            coroutines.launch {
                context.clearSpecificPreference(DataStoreKeys.StringKeys.email)
                context.clearSpecificPreference(DataStoreKeys.StringKeys.password)
            }

            Toast.makeText(context, accountDeletingResponse.message, Toast.LENGTH_LONG).show()
            onLogOut.invoke()
        } else if (accountDeletingResponse.success == false) {
            Toast.makeText(context, accountDeletingResponse.message, Toast.LENGTH_LONG).show()
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary), contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                imageVector = Icons.Filled.AccountCircle, // Replace with actual image resource
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userInfo.data?.nameAndSurname ?: "",
                color = MaterialTheme.colors.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userInfo.data?.email ?: "",
                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {

                ProfileItems(
                    icon = Icons.Filled.SwapHoriz,
                    text = stringResource(id = R.string.transactions),
                    onClickActionButton = {
                        goTransactionScreen.invoke()
                    }
                )
                ProfileItems(
                    icon = Icons.Filled.Wallet,
                    text = stringResource(id = R.string.Wallet),
                    onClickActionButton = {
                        goWalletScreen.invoke()
                    }
                )

                ProfileItems(
                    icon = Icons.Default.Star,
                    text = stringResource(id = R.string.watchlist_text),
                    onClickActionButton = {
                        navigateToHome.invoke()
                    }
                )

            }


            Spacer(modifier = Modifier.weight(1f))


            Row(modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()) {

                ActionButton(
                    text = "Delete Account"
                ) {
                    viewModel.deleteAccount()
                }

                Spacer(modifier = Modifier.weight(1f))

                ActionButton(
                    text = "Logout"
                ) {
                    coroutines.launch {
                        context.clearSpecificPreference(DataStoreKeys.StringKeys.email)
                        context.clearSpecificPreference(DataStoreKeys.StringKeys.password)
                        onLogOut.invoke()
                    }
                }
            }

            if (signUpViewState.isAccountDeleting) {
                CircularProgressIndicator(
                    color = Color(0xff3B82F6),
                    strokeWidth = 4.dp
                )
            }
        }
    }

}

@Composable
fun ProfileItems(
    icon : ImageVector,
    text: String,
    onClickActionButton : () -> Unit,
) {
    Column(modifier = Modifier
        .clickable {
            onClickActionButton.invoke()
        }.fillMaxWidth()){

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {

            Image(
                imageVector = icon,
                contentDescription = text,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
            )


            androidx.compose.material3.Card(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface
                )
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        HorizontalDivider(modifier = Modifier
            .alpha(0.5f)
            .padding(vertical = 4.dp))
    }


}


@Composable
fun ActionButton(text: String, onClickActionButton: () -> Unit) {

    Button(
        onClick = onClickActionButton,
        modifier = Modifier
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary)
    ) {
        Text(text = text, fontSize = 18.sp, color = MaterialTheme.colors.primary)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogOut = {}, goTransactionScreen = {}, goWalletScreen = {}, navigateToHome = {})
}
