package com.finance.trade_learn.view.trade

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.R
import com.finance.trade_learn.models.FilterType

@Composable
fun FilterAndSortButtons(
    selectedFilter : FilterType = FilterType.Default,
    onClickFilter : (FilterType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
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
            Text(text =  stringResource(id = selectedFilter.text))
        }
    }
}

@Composable
private fun FilterItem(filterType : FilterType, onClickFilter: () -> Unit){
    DropdownMenuItem(onClick = {
        onClickFilter.invoke()
    }) {
        Text(
            text = stringResource(id = filterType.text),
            color = MaterialTheme.colors.onPrimary
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewFilterAndSortButtons() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        FilterAndSortButtons(
            selectedFilter = FilterType.Default,
            onClickFilter = {}
        )
    }
}
