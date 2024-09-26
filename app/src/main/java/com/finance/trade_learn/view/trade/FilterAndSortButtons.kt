package com.finance.trade_learn.view.trade

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finance.trade_learn.R
import com.finance.trade_learn.models.FilterType
import com.finance.trade_learn.viewModel.MarketViewModel

@Composable
fun FilterAndSortButtons(
    onClickFilter : (FilterType) -> Unit,
    viewModel : MarketViewModel = hiltViewModel<MarketViewModel>()
) {
    val searchBarViewState by viewModel.searchBarViewState.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp)
    ) {
        // Filter Button

        Box(
            contentAlignment = Alignment.TopStart
        ) {
            Button(
                onClick = {
                    expanded = true
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary.copy(0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter), // Replace with actual filter icon resource
                    contentDescription = stringResource(id = R.string.filter),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =  stringResource(id = R.string.filter))
            }

            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colors.background),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {


                FilterType.entries.forEach {
                    FilterItem(
                        isSelected = searchBarViewState.filterType == it,
                        filterType = it,
                        onClickFilter = {
                            onClickFilter.invoke(it)
                            expanded = false
                        }
                    )
                }
            }

        }
        // Lowest Price Button
        Button(
            onClick = {
                expanded = true
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF007AFF), // Replace with your blue color
                contentColor = Color.White
            ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_filter), // Replace with actual sort icon resource
                contentDescription = "Sort",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text =  stringResource(id = searchBarViewState.filterType.text))
        }
    }
}

@Composable
private fun FilterItem(
    isSelected: Boolean,
    filterType: FilterType,
    onClickFilter: () -> Unit
) {
    DropdownMenuItem(onClick = {
        onClickFilter.invoke()
    }) {
        Text(
            text = stringResource(id = filterType.text),
            fontSize =  if (isSelected) 18.sp else TextUnit.Unspecified,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF4CAF50) else MaterialTheme.colors.onPrimary
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewFilterAndSortButtons() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        FilterAndSortButtons(onClickFilter = {})
    }
}
