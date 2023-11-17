package com.example.soapcropmap.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soapcropmap.FormViewData
import com.example.soapcropmap.ScreenType
import com.example.soapcropmap.UnitType
import com.example.soapcropmap.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputPointsFormScreen(
    unitType: UnitType,
    viewModel: ViewModel,
    navController: NavController,
) {
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    var viewData by remember { mutableStateOf(
        when (unitType) {
            UnitType.PIXEL -> FormViewData(
                (viewModel.formPixelCoordinates()?.x1 ?: "").toString(),
                (viewModel.formPixelCoordinates()?.y1 ?: "").toString(),
                (viewModel.formPixelCoordinates()?.x2 ?: "").toString(),
                (viewModel.formPixelCoordinates()?.y2 ?: "").toString()
            )
            UnitType.COORDINATES -> FormViewData(
                (viewModel.formGpsCoordinates()?.lat1 ?: "").toString(),
                (viewModel.formGpsCoordinates()?.lon1 ?: "").toString(),
                (viewModel.formGpsCoordinates()?.lat2 ?: "").toString(),
                (viewModel.formGpsCoordinates()?.lon2 ?: "").toString(),
            )
        }
    ) }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                { navController.navigate(ScreenType.HOME.name) }
            ) {
                Text(text = "Back")
            }
            Button(
                {
                    when(unitType) {
                        UnitType.COORDINATES -> {
                            val mapped = viewData.mapToGpsModel()
                            if (null != mapped) {
                                viewModel.gpsCoordinates = mapped
                            }
                        }
                        UnitType.PIXEL -> {
                            val mapped = viewData.mapToPixelModel()
                            if (null != mapped) {
                                viewModel.pixelCoordinates = mapped
                            }
                        }
                    }
                    navController.navigate(ScreenType.HOME.name)
                },
            ) {
                Text("Crop")
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewData.x1,
            onValueChange = { new -> viewData = viewData.copy(x1 = new) },
            label = {
                when (unitType) {
                    UnitType.COORDINATES -> Text("Upper-left corner latitude max(${viewModel.maxGpsCoordinates?.lat1 ?: "?"})")
                    UnitType.PIXEL -> Text("Upper-left corner horizontal [pixel] min(${viewModel.maxPixelCoordinates?.x1 ?: "?"})")
                }
            },
            isError = !(isValid(viewData.x1, unitType)),
            keyboardOptions = keyboardOptions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewData.x2,
            onValueChange = { new -> viewData = viewData.copy(x2 = new) },
            label = {
                when (unitType) {
                    UnitType.COORDINATES -> Text("Upper-left corner longitude min(${viewModel.maxGpsCoordinates?.lon1 ?: "?"})")
                    UnitType.PIXEL -> Text("Upper-left corner vertical [pixel] min(${viewModel.maxPixelCoordinates?.y1 ?: "?"})")
                }
            },
            isError = !isValid(viewData.x2, unitType),
            keyboardOptions = keyboardOptions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewData.x3,
            onValueChange = { new -> viewData = viewData.copy(x3 = new) },
            label = {
                    when (unitType) {
                        UnitType.COORDINATES -> Text("Lower-right corner latitude min(${viewModel.maxGpsCoordinates?.lat2 ?: "?"})")
                        UnitType.PIXEL -> Text("Lower-right corner horizontal [pixel] max(${viewModel.maxPixelCoordinates?.x2 ?: "?"})")
                    }
            },
            isError = !(isValid(viewData.x3, unitType)),
            keyboardOptions = keyboardOptions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewData.x4,
            onValueChange = {new -> viewData = viewData.copy(x4 = new) },
            label = {
                    when (unitType) {
                        UnitType.COORDINATES -> Text("Lower-right corner longitude max(${viewModel.maxGpsCoordinates?.lon2 ?: "?"})")
                        UnitType.PIXEL -> Text("Lower-right corner vertical [pixel] max(${viewModel.maxPixelCoordinates?.y2 ?: "?"})")
                    }
            },
            isError = !isValid(viewData.x4, unitType),
            keyboardOptions = keyboardOptions,
        )
    }
}


fun isValid(input: String, unitType: UnitType): Boolean {
    return try {
        when(unitType) {
            UnitType.COORDINATES -> input.toDouble()
            UnitType.PIXEL -> input.toUInt()
        }
        true
    } catch (e: NumberFormatException) {
        false
    }
}