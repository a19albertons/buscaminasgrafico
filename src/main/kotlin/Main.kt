import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.singleWindowApplication
import motorbuscaminas.Buscaminas

// New composable to display the board as a grid with better aesthetics.
@Composable
fun BoardGrid(board: List<StringBuilder>) {
    Column {
        board.forEach { row ->
            Row {
                row.toString().forEach { cell ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colors.primary)
                            .size(32.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cell.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

fun main() = singleWindowApplication {
    var gameStarted by remember { mutableStateOf(false) }
    val buscaminas = remember { Buscaminas() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Seleccione un modo de juego:")
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Button(onClick = {
                buscaminas.crearTablero(8, 8, 9)
                gameStarted = true
            }) { Text("Classic") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                buscaminas.crearTablero(9, 9, 10)
                gameStarted = true
            }) { Text("Easy") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                buscaminas.crearTablero(16, 16, 40)
                gameStarted = true
            }) { Text("Medium") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                buscaminas.crearTablero(16, 30, 99)
                gameStarted = true
            }) { Text("Expert") }
        }

        if (gameStarted) {
            Text("Tablero:", modifier = Modifier.padding(top = 16.dp))
            BoardGrid(buscaminas.tableroFinal)

            // Additional UI elements such as inputs for coordinates and actions go here.
        }
    }
}