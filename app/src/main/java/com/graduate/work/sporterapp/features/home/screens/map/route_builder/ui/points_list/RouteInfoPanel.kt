package com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.points_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.MapPoint
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.points_list.PointConstants.POINT_HEIGHT
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


enum class ExpandState {
    EXPANDED,
    COLLAPSED;

    fun next() = if (this == EXPANDED) COLLAPSED else EXPANDED
}

@Composable
fun RouteInfoPanel(
    modifier: Modifier = Modifier,
    points: List<MapPoint>? = null,
    onMove: (from: Int, to: Int) -> Unit,
    onPointClick: (index: Int) -> Unit,
    onPointDeleteClick: (index: Int) -> Unit,
    onListClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)
    var listExpandedState by remember { mutableStateOf(ExpandState.EXPANDED) }

    LaunchedEffect(points, listExpandedState) {
        points?.lastIndex?.let {
            if (it > 0) dragDropListState.lazyListState.animateScrollToItem(it)
        }
    }
    AnimatedContent(
        targetState = listExpandedState,
        transitionSpec = {
            expandVertically(animationSpec = tween(durationMillis = 150)) togetherWith
                    shrinkVertically(animationSpec = tween(durationMillis = 150)) using
                    SizeTransform { initialSize, targetSize ->
                        if (targetState == ExpandState.EXPANDED) {
                            keyframes {
                                IntSize(initialSize.width, initialSize.height) at 150
                                durationMillis = 300
                            }
                        } else {
                            keyframes {
                                IntSize(targetSize.width, targetSize.height) at 150
                                durationMillis = 300
                            }
                        }
                    }
        }, label = "RouteInfoPanel"
    ) { targetExpanded ->
        Card(modifier = modifier) {
            ConstraintLayout {
                val (
                    pointsListRef,
                    expandButtonRef,
                    closeButtonRef,
                ) = createRefs()
                if (points.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.no_points_added_yet),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.constrainAs(pointsListRef) {
                            top.linkTo(parent.top, margin = 4.dp)
                            bottom.linkTo(closeButtonRef.top, margin = 4.dp)
                            width = Dimension.matchParent
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .constrainAs(pointsListRef) {
                                top.linkTo(parent.top, margin = 4.dp)
                                bottom.linkTo(closeButtonRef.top)
                            }
                            .heightIn(min = POINT_HEIGHT.dp, max = POINT_HEIGHT.dp * 5)
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDrag = { change, offset ->
                                        change.consume()
                                        dragDropListState.onDrag(offset = offset)

                                        if (overScrollJob?.isActive == true)
                                            return@detectDragGesturesAfterLongPress

                                        dragDropListState
                                            .checkForOverScroll()
                                            .takeIf { it != 0f }
                                            ?.let {
                                                overScrollJob = scope.launch {
                                                    dragDropListState.lazyListState.scrollBy(it)
                                                }
                                            } ?: kotlin.run { overScrollJob?.cancel() }
                                    },
                                    onDragStart = { offset ->
                                        dragDropListState.onDragStart(
                                            offset
                                        )
                                    },
                                    onDragEnd = { dragDropListState.onDragInterrupted() },
                                    onDragCancel = { dragDropListState.onDragInterrupted() }
                                )
                            }
                            .fillMaxWidth(),
                        state = dragDropListState.lazyListState,
                    ) {
                        itemsIndexed(
                            items = if (targetExpanded == ExpandState.EXPANDED) points else points.takeLast(
                                1
                            ),
                            key = { index, _ -> index }) { index, mapPoint ->
                            Point(
                                text = mapPoint.name,
                                point = mapPoint.point,
                                modifier = Modifier.composed {
                                    val offsetOrNull =
                                        dragDropListState.elementDisplacement.takeIf {
                                            index == dragDropListState.currentIndexOfDraggedItem
                                        }
                                    Modifier.graphicsLayer {
                                        translationY = offsetOrNull ?: 0f
                                    }
                                },
                                onPointClick = {
                                    val currentIndex =
                                        if (targetExpanded == ExpandState.EXPANDED) index else points.lastIndex
                                    onPointClick(currentIndex)
                                },
                                onDeletePointClick = {
                                    val currentIndex =
                                        if (targetExpanded == ExpandState.EXPANDED) index else points.lastIndex
                                    onPointDeleteClick(currentIndex)
                                }
                            )
                        }
                    }
                }
                if ((points?.size ?: 0) > 1) {
                    IconButton(onClick = {
                        listExpandedState = listExpandedState.next()
                    }, Modifier.constrainAs(expandButtonRef) {
                        start.linkTo(parent.start, 4.dp)
                        bottom.linkTo(parent.bottom, 4.dp)
                    }) {
                        Icon(
                            imageVector = if (listExpandedState == ExpandState.EXPANDED) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = if (listExpandedState == ExpandState.EXPANDED) "Collapse" else "Expand"
                        )
                    }
                }
                IconButton(onClick = onListClose, Modifier.constrainAs(closeButtonRef) {
                    end.linkTo(parent.end, 4.dp)
                    bottom.linkTo(parent.bottom, 4.dp)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
    }
}
