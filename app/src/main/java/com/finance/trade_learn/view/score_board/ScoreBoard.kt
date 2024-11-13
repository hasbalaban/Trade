package com.finance.trade_learn.view.score_board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.base.BaseViewModel.Companion.setLockMainActivityStatus
import com.finance.trade_learn.models.ScoreBoardItem
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.view.wallet.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScoreBoardViewModel() : ViewModel(){
    private val _scoreBoardItems = MutableStateFlow<List<ScoreBoardItem>>(emptyList())
    val scoreBoardItems = _scoreBoardItems.asStateFlow()
    init {
        getAllCrypto()
    }

    private fun getAllCrypto() {
        viewModelScope.launch {
            setLockMainActivityStatus(true)
            val response = cryptoService().getScoreBoard(
                requestedUserId = BaseViewModel.userInfo.value.data?.userId
            )
            setLockMainActivityStatus(false)

            when(response.isSuccessful){
                true -> {
                    response.body()?.data?.let {newList ->
                        _scoreBoardItems.emit(newList)
                    }
                }
                false ->  _scoreBoardItems.emit(emptyList())
            }
        }
    }
}

@Composable
fun ScoreBoard(viewModel: ScoreBoardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    val list by viewModel.scoreBoardItems.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header()
        // ScoreBoard kısmı
        ScoreBoard(userList = list)
    }
}


@Composable
fun Header() {
    // Başlık kutusu

    Box(
        modifier = Modifier
            .background(Color(0xFF1E88E5))
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Başlık içeriği
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Financial Titans - Top Earners",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

        }
    }
}

@Composable
fun ScoreBoard(userList: List<ScoreBoardItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Koyu gri arka plan
    ) {
        itemsIndexed(userList.sortedByDescending { it.totalBalance }) { index, user ->
            val backgroundColor = when {
                index == 0 -> Color(0xFF4CAF50) // 1. sıra için yeşil ton
                index == 1 -> Color(0xFF1976D2) // 2. sıra için mavi ton
                index == 2 -> Color(0xFF7B1FA2) // 3. sıra için mor ton
                user.isYou -> Color(0xfffbbf24)
                index % 2 == 0 -> Color(0xFFEEEEEE) // Çift sıralar için açık gri
                else -> Color.White // Tek sıralar için beyaz
            }

            val textColor =
                if(user.isYou || index < 3) Color.White
                else Color(0xFF212121) // İlk üç sırada beyaz, diğer sıralarda siyah

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                // Sıralama Numarası
                Text(
                    text = "${index + 1}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.width(40.dp)
                )

                // Kullanıcı İsmi
                Text(
                    text = user.nameAndUsername,
                    fontWeight = if (user.isYou || index < 3) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                // Sıralama İkonları
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val stars = when (index) {
                        0 -> 3
                        1 -> 2
                        2 -> 1
                        else -> 0
                    }

                    repeat(stars) {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Outlined.Star // 1. sırada dolu yıldız
                                1 -> Icons.Filled.Star // 2. sırada dolu yıldız
                                else -> Icons.AutoMirrored.Outlined.StarHalf // 3. sırada yarım yıldız
                            },
                            contentDescription = "Rank Icon",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bakiye Bilgisi
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${user.totalBalance.format(2)}$",
                        fontSize = when{
                            index in listOf(0, 1, 2) -> 20.sp
                            user.isYou -> 20.sp
                            else -> 16.sp
                        },
                        color = textColor,
                        fontWeight = if (index < 3) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}





@Preview
@Composable
private fun Preview(){
    val list = ArrayList<ScoreBoardItem>()
    for (i in 0..15){
        val item = ScoreBoardItem(
            isYou = i == 6,
            nameAndUsername = "User-$i",
            totalBalance = i.toDouble()
        )
        list.add(item)
    }

    FinanceAppTheme {
        ScoreBoard(userList = list)
    }
}

data class Deneme(
    val name : String,
    val totalBalance : Double
)