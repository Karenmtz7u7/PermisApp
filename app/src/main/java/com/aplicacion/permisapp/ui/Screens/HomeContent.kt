package com.aplicacion.permisapp.ui.Screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import com.aplicacion.permisapp.R
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aplicacion.permisapp.ui.theme.btnMenuText
import com.aplicacion.permisapp.ui.theme.grismenubtn
import com.aplicacion.permisapp.ui.theme.headerText
import com.aplicacion.permisapp.ui.theme.helloTxt
import com.aplicacion.permisapp.ui.theme.userdefault
import com.aplicacion.permisapp.ui.theme.verde1
import com.aplicacion.permisapp.ui.theme.verde3
import com.aplicacion.permisapp.ui.theme.white

@Preview
@Composable
fun HomeContent() {
    background()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        cardUser()
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoteschi),
                    contentDescription = null,
                    modifier = Modifier.size(261.dp, 177.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                menuBtn()

                Spacer(modifier = Modifier.height(16.dp))

                historiesRCV()
            }
        }
    }
}


@Composable
fun cardUser() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .padding(top = 5.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(9.dp))
        Column(modifier = Modifier.padding(top = 10.dp, start = 10.dp)) {
            Text(
                text = "Hola!",
                style = helloTxt
            )

            Text(
                text = "user@default.com >",
                style = userdefault
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                painter = painterResource(R.drawable.ic_round_menu_24),
                contentDescription = null,
                modifier = Modifier.size(30.dp, 39.dp)
            )
        }
    }
}

@Composable
fun background(){
    BoxWithConstraints {
        val gradientColors = listOf(verde3, white, white)
        val gradientAngle = 9f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
            )
            drawRect(brush = gradient)
        }
    }
}

@Composable
fun menuBtn(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, verde1),
        shape = RoundedCornerShape(10.dp),

        ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Incidencias",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                    style = headerText
                )

                Spacer(modifier = Modifier.width(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendar Icon",
                    modifier = Modifier
//                        .width(29.dp)
//                        .height(25.dp)
                        .background(Color.Transparent)
                        .padding(end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.padding(top = 15.dp))

            btnMenu(
                text = "Llegar tarde",
                icon = R.drawable.ic_baseline_access_time_ ,
                text2 = "Consulta medica",
                icon2 = R.drawable.ic_baseline_local_hospital_24
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            btnMenu(
                icon = R.drawable.ic_baseline_money_off_24,
                text = "Día economico",
                icon2 = R.drawable.ic_baseline_access_time_,
                text2 = "Retirarse antes"
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            btnMenu(
                icon = R.drawable.ic_baseline_baby_changing_station_24,
                text = "Lactancia",
                icon2 = R.drawable.ic_circle_right_24,
                text2 = "Otro"
            )


            Column( modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Button(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    colors = ButtonDefaults.buttonColors(grismenubtn),
                    shape = RoundedCornerShape(10.dp)

                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_money_off_24),
                            contentDescription = null,
                            tint = verde1
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Permiso sin goce de sueldo",
                            style = btnMenuText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun btnMenu(icon: Int, text: String, icon2: Int, text2: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .weight(1f),
            colors = ButtonDefaults.buttonColors(grismenubtn),
            shape = RoundedCornerShape(10.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = verde1
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = btnMenuText
                )
            }
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .weight(1f),
            colors = ButtonDefaults.buttonColors(grismenubtn),
            shape = RoundedCornerShape(10.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon2),
                    contentDescription = null,
                    tint = verde1
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text2,
                    style = btnMenuText
                )
            }
        }
    }
}

@Composable
fun historiesRCV(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, verde1),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "En espera de aprobacion",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                    style = headerText
                )

                Spacer(modifier = Modifier.width(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_history_24),
                    contentDescription = "Calendar Icon",
                    modifier = Modifier
//
                        .background(Color.Transparent)
                        .padding(end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.padding(top = 15.dp))

        }
    }

}
