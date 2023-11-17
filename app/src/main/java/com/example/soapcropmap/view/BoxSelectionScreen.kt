package com.example.soapcropmap.view

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.soapcropmap.PixelCoordinates
import com.example.soapcropmap.ScreenType
import com.example.soapcropmap.ViewModel
import java.lang.Float.max
import java.lang.Float.min

@Composable
fun BoxSelectionScreen(nacController: NavController, model: ViewModel) {
    var img by remember { mutableStateOf<Bitmap?>(null) }
    var startDragPoint by remember { mutableStateOf<Offset?>(null) }
    var endDragPoint by remember { mutableStateOf<Offset?>(null) }
    var coordinates by remember { mutableStateOf<PixelCoordinates?>(null) }

    DisposableEffect(model) {
        val imgObserver = Observer<Bitmap?> { bitmap ->
            img = bitmap
        }
        model.fullImg.observeForever(imgObserver)
        onDispose {
            model.fullImg.removeObserver(imgObserver)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        img?.let { bitmap ->
            val offsetX = (this.constraints.maxWidth - bitmap.width) / 2
            val offsetY = (this.constraints.maxHeight - bitmap.height) / 2
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Map image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startDragPoint = it },
                            onDrag = { change, _ ->
                                endDragPoint = change.position
                            }
                        )
                    },
            )

            startDragPoint?.let { start ->
                endDragPoint?.let { end ->
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val isAbove = end.y <= 0
                        val isBelow = end.y >= bitmap.height
                        val isRightOut = end.x >= bitmap.width
                        val isLeftOut = end.x <= 0
                        val height: Float = if (isBelow) {
                             bitmap.height.toFloat() - start.y
                        } else if (isAbove) {
                            0 - start.y
                        } else {
                            end.y - start.y

                        }
                        val width: Float = if (isRightOut) {
                            bitmap.width.toFloat() - start.x
                        } else if (isLeftOut) {
                            0 - start.x
                        } else {
                            end.x - start.x
                        }

                        drawRect(
                            color = Color.Red,
                            topLeft = start.copy(x = start.x + offsetX, y = start.y + offsetY),
                            size = Size(width = width, height = height),
                            alpha = 0.2f,
                        )

                        val topLeft = Offset(min(start.x, end.x), min(start.y, end.y))
                        val bottomRight = Offset(max(start.x, end.x), max(start.y, end.y))

                        coordinates = PixelCoordinates(topLeft.x.toInt(), topLeft.y.toInt(), bottomRight.x.toInt(), bottomRight.y.toInt())
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 5.dp),
                onClick = { nacController.navigate(ScreenType.HOME.name) },
                containerColor = Color.Red,
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel cropping by selection",
                    modifier = Modifier
                        .size(20.dp),
                )
            }
            FloatingActionButton(
                modifier = Modifier,
                onClick = {
                    coordinates?.let { c ->
                        model.pixelCoordinates = c
                        nacController.navigate(ScreenType.HOME.name)
                    }
                },
                containerColor = Color.Green,
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Confirm crop by selection",
                    modifier = Modifier
                        .size(20.dp),
                )
            }
        }
    }
}