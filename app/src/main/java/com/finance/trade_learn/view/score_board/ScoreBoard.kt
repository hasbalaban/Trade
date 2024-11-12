package com.finance.trade_learn.view.score_board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.theme.FinanceAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScoreBoardViewModel() : ViewModel(){
    private val _users = MutableStateFlow(emptyList<Deneme>())
    val users = _users.asStateFlow()

    init {
        val list = ArrayList<Deneme>()
        for (i in 0..15){
            val item = Deneme(
                name = "User-$i",
                totalBalance = i.toDouble()
            )
            list.add(item)
        }
        viewModelScope.launch {
            _users.emit(list)
        }
    }
}

@Composable
fun ScoreBoard(viewModel: ScoreBoardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    val list by viewModel.users.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Arka plan
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
fun ScoreBoard(userList: List<Deneme>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Koyu gri arka plan
    ) {
        itemsIndexed(userList.sortedByDescending { it.totalBalance }) { index, user ->
            val backgroundColor =
                when {
                    index == 0 -> Color(0xFF388E3C)
                    index == 1 -> Color(0xFF005FA8)
                    index == 2 -> Color(0xff4a90e2)
                    index % 2 != 0 -> Color.White
                    else -> Color(0xfffafafa)
                }

            val textColor =
                when {
                    index == 0 -> Color.White
                    index == 1 -> Color.White
                    index == 2 -> Color.White
                    index % 2 != 0 -> Color.Black
                    else -> Color.Black
                }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {

                Text(
                    text = "${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )


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

                    for (i in 0..<stars) {
                        Icon(
                            imageVector = when(index){
                                0 -> Icons.Outlined.Stars
                                1 -> Icons.Outlined.Star
                                else -> Icons.AutoMirrored.Filled.StarHalf
                            },
                            contentDescription = "Rank Icon",
                            tint = when (index) {
                                0 -> Color.White
                                1 -> Color.White
                                2 -> Color.White
                                else -> Color(0xFFB0BEC5) // Diğer sıralar için gri
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }



                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${user.totalBalance} ₺",
                        fontSize = 16.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )

                }


            }
        }
    }
}




@Preview
@Composable
private fun Preview(){
    val list = ArrayList<Deneme>()
    for (i in 0..15){
        val item = Deneme(
            name = "User-$i",
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