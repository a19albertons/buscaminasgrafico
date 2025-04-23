// File: src/main/kotlin/Main.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.delay

@Composable
fun BoardGrid(board: List<StringBuilder>, onCellClick: (row: Int, col: Int) -> Unit) {
    Column {
        board.forEachIndexed { rowIndex, row ->
            Row {
                row.toString().forEachIndexed { colIndex, cell ->
                    val cellText = if (cell == 'B') "🚩" else cell.toString()
                    // When the cell has been discovered (contains a digit, a bomb indicator “B”, or a space) it gets a white background.
                    // Unclicked cells have a grey background.
                    val backgroundColor = when {
                        cell == 'F' -> Color.LightGray
                        cell.isDigit() || cell == 'B' || cell == ' ' -> Color.White
                        else -> Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colors.primary)
                            .background(backgroundColor)
                            .size(32.dp)
                            .padding(4.dp)
                            .clickable { onCellClick(rowIndex, colIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cellText,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun LoseBoardGrid(board: List<StringBuilder>) {
    Column {
        board.forEach { row ->
            Row {
                row.forEach { cell ->
                    val cellText = when {
                        cell == '*' -> "💣"
                        cell == '-' -> " "
                        else -> cell.toString()
                    }
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colors.primary)
                            .background(if (cell == '*') Color.LightGray else Color.White)
                            .size(32.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = cellText, style = MaterialTheme.typography.body1)
                    }
                }
            }
        }
    }
}

fun main() = singleWindowApplication {
    val estadoJuego = remember { EstadoJuego() }
    val customModeSelected = remember { mutableStateOf(false) }
    val customRows = remember { mutableStateOf("10") }
    val customCols = remember { mutableStateOf("10") }
    val customMines = remember { mutableStateOf("10") }
    val elapsedTime = remember { mutableStateOf(0) }

    // Timer coroutine: increments elapsedTime every second while game is active.
    LaunchedEffect(key1 = estadoJuego.gameStarted.value) {
        while (estadoJuego.gameStarted.value && !estadoJuego.gameLost.value && !estadoJuego.gameWon.value) {
            delay(1000)
            elapsedTime.value++
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (!estadoJuego.gameStarted.value) {
            if (!customModeSelected.value) {
                Text("Seleccione un modo de juego:")
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    Button(onClick = { estadoJuego.iniciarJuego("Classic") }) { Text("Classic") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { estadoJuego.iniciarJuego("Easy") }) { Text("Easy") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { estadoJuego.iniciarJuego("Medium") }) { Text("Medium") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { estadoJuego.iniciarJuego("Expert") }) { Text("Expert") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { customModeSelected.value = true }) { Text("Custom") }
                }
            } else {
                Text("Custom Game Mode:")
                OutlinedTextField(
                    value = customRows.value,
                    onValueChange = { customRows.value = it },
                    label = { Text("Rows") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = customCols.value,
                    onValueChange = { customCols.value = it },
                    label = { Text("Columns") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = customMines.value,
                    onValueChange = { customMines.value = it },
                    label = { Text("Mines") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val rows = customRows.value.toIntOrNull() ?: 10
                        val cols = customCols.value.toIntOrNull() ?: 10
                        val mines = customMines.value.toIntOrNull() ?: 10
                        estadoJuego.buscaminas.crearTablero(rows, cols, mines)
                        estadoJuego.gameStarted.value = true
                    }
                ) {
                    Text("Start Custom Game")
                }
            }
        }

        if (estadoJuego.gameStarted.value) {
            // Timer display.
            Text("Time: ${elapsedTime.value} seconds", style = MaterialTheme.typography.h6)
            when {
                estadoJuego.gameLost.value -> {
                    Text("Has perdido", style = MaterialTheme.typography.h4, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tablero de minas:")
                    LoseBoardGrid(estadoJuego.buscaminas.tableroDeMinas())
                }
                estadoJuego.gameWon.value -> {
                    Text("Has ganado", style = MaterialTheme.typography.h4, color = Color.Green)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tablero final:")
                    BoardGrid(estadoJuego.buscaminas.tableroDeFinal()) { _, _ -> }
                }
                else -> {
                    Text(text = estadoJuego.refresh.value.toString(), color = MaterialTheme.colors.background)
                    Button(
                        onClick = { estadoJuego.changeActionMode() },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Mode: ${estadoJuego.actionMode.value}")
                    }
                    Text("Tablero:", modifier = Modifier.padding(top = 16.dp))
                    BoardGrid(estadoJuego.buscaminas.tableroDeFinal()) { row, col ->
                        estadoJuego.onCellClick(row, col)
                    }
                }
            }
        }
    }
}