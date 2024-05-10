package com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun RouteBuilderDropDownMenu(
    showDropdownActionsMenu: Boolean,
    onShowDropdownActionsMenuChange: (Boolean) -> Unit,
    onClick: (RouteBuilderDropDownMenuState) -> Unit,
) {
    DropdownMenu(
        expanded = showDropdownActionsMenu,
        onDismissRequest = { onShowDropdownActionsMenuChange(false) }) {
        RouteBuilderDropDownMenu.entries.forEach {
            DropdownMenuItem(
                modifier = Modifier.padding(4.dp),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            it.icon,
                            contentDescription = stringResource(id = it.titleId)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(id = it.titleId))
                    }
                }, onClick = {
                    onShowDropdownActionsMenuChange(false)
                    onClick(it.state)
                })
            HorizontalDivider()
        }
    }
}