package com.aplicacion.permisapp.ui.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.ui.theme.cafe
import com.aplicacion.permisapp.ui.theme.gris
import com.aplicacion.permisapp.ui.theme.helpText
import com.aplicacion.permisapp.ui.theme.iniciarSesionTextStyle
import com.aplicacion.permisapp.ui.theme.verde
import com.aplicacion.permisapp.ui.theme.verde1


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainActivityLoginScreen() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.mobile_login_rafiki),
            contentDescription = null,
            modifier = Modifier.size(270.dp)
        )
        Text(
            text = "Iniciar sesión",
            color = verde1,
            style = iniciarSesionTextStyle
        )

        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 15.dp, end = 15.dp),
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                )
            }
        )

        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 15.dp, end = 15.dp),
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                        contentDescription = stringResource(R.string.toggle_password_visibility),
                        tint = Color.Gray
                    )
                }
            }
        )

        Column(modifier = Modifier
            .padding(start = 15.dp, top = 8.dp)
            .fillMaxWidth(),

            horizontalAlignment = Alignment.Start)
        {
            Text(
                text = "¿Olvidó su contraseña?",
                style = helpText,
                color = cafe,
            )
        }

        Column(modifier = Modifier
            .padding(end = 15.dp)
            .fillMaxWidth(),

            horizontalAlignment = Alignment.End)
        {
            Text(
                text = "¿Necesita ayuda?",
                style = helpText,
                color = cafe,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {  },
            modifier = Modifier
                .width(282.dp)
                .padding(top = 16.dp,)
                .background(
                    color = gris,
                    shape = RoundedCornerShape(10.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                gris
            )

        ) {
            Text(
                text = "Ingresar",
                style = MaterialTheme.typography.bodyLarge,
                color  = cafe,

                )
        }

        Text(
            text = "¿Aún no tienes una cuenta?",
            style = helpText,
            color = cafe,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {  },
            modifier = Modifier
                .width(282.dp)
                .padding(top = 16.dp)
                .background(
                    color = verde,
                    shape = RoundedCornerShape(10.dp)
                ),
            colors = ButtonDefaults.buttonColors(verde)
        ) {
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.bodyLarge,
                color  = cafe,
            )
        }
    }
}



