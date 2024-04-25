package com.graduate.work.sporterapp.utils.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RoundIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick, modifier = modifier
            .width(72.dp)
            .height(72.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun RoundIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick, modifier = modifier
            .width(64.dp)
            .height(64.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
    }
}