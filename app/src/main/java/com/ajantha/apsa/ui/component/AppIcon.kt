package com.ajantha.apsa.ui.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

@Composable
fun AppIcon(modifier: Modifier = Modifier, drawable: Drawable?) {
    val bitmap = remember(drawable) {
        drawable?.toBitmap()?.asImageBitmap()
    }

    bitmap?.let {
        Image(
            modifier = modifier,
            bitmap = it,
            contentDescription = null
        )
    }
}