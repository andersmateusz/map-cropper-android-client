package com.example.soapcropmap.view

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.soapcropmap.R
import com.example.soapcropmap.ScreenType
import com.example.soapcropmap.ViewModel

@Composable
fun MainScreen(navController: NavController, model: ViewModel) {
    var img by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    DisposableEffect(model) {
        val imgObserver = Observer<Bitmap?> { bitmap ->
            img = bitmap
        }
        val loadingObserver = Observer<Boolean> { loading ->
            isLoading = loading
        }
        model.img.observeForever(imgObserver)
        model.isLoading.observeForever(loadingObserver)
        onDispose {
            model.img.removeObserver(imgObserver)
            model.isLoading.removeObserver(loadingObserver)
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        img?.let {bitmap ->
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Map image", modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize())
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 5.dp),
                onClick = { navController.navigate(ScreenType.GPS.name) },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location_crosshairs_solid),
                    contentDescription = "Crop by coordinates input",
                    modifier = Modifier
                        .size(20.dp),
                )
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 5.dp),
                onClick = { navController.navigate(ScreenType.PIXEL.name) },
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    painter = painterResource(id = R.drawable.image_regular),
                    contentDescription = "Crop by pixel input",
                )
            }
            FloatingActionButton(
                modifier = Modifier,
                onClick = { navController.navigate(ScreenType.BOX.name) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.expand_solid),
                    contentDescription = "Crop by coordinates input",
                    modifier = Modifier
                        .size(20.dp),
                )
            }
        }
    }
}