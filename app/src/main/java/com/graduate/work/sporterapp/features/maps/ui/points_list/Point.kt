package com.graduate.work.sporterapp.features.maps.ui.points_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.ext.roundTo6
import com.mapbox.geojson.Point

object PointConstants {
    const val POINT_HEIGHT = 56
}

@Composable
fun Point(
    modifier: Modifier,
    text: String,
    point: Point,
    onPointClick: () -> Unit,
    onDeletePointClick: () -> Unit,
) {
    Box(modifier = modifier
        .height(56.dp)
        .fillMaxWidth()
        .clickable { onPointClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.draggable_point),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                modifier = Modifier
                    .paint(painter = painterResource(id = R.drawable.ic_checkpoint))
                    .wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "${point.longitude().roundTo6()} ;  ${point.latitude().roundTo6()}")
        }
        IconButton(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.CenterEnd), onClick = onDeletePointClick
        ) {
            Icon(
                Icons.Default.RemoveCircleOutline,
                contentDescription = stringResource(R.string.remove_point),
            )
        }
    }
}