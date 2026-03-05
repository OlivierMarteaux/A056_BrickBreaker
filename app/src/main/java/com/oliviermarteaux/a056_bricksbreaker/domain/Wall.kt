package com.oliviermarteaux.a056_bricksbreaker.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class Wall(
    val topLeft: Offset,
    val size: Size
)