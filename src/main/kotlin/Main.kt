import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.singleWindowApplication
import motorbuscaminas.Buscaminas

@Composable
fun BoardGrid(board: List<StringBuilder>, onCellClick: (row: Int, col: Int) -> Unit) {
    Column {
        board.forEachIndexed { rowIndex, row ->
            Row {
                row.toString().forEachIndexed { colIndex, cell ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colors.primary)
                            .size(32.dp)
                            .padding(4.dp)
                            .clickable { onCellClick(rowIndex, colIndex) },
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
    var actionMode by remember { mutableStateOf(ActionMode.DISCOVER) }
    var refresh by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        if (!gameStarted) {
            // Board selection UI is visible only if game is not started.
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
        }

        if (gameStarted) {
            // Dummy Text to ensure refresh is read and triggers recomposition.
            Text(text = refresh.toString(), color = MaterialTheme.colors.background)
            Button(onClick = {
                actionMode = when (actionMode) {
                    ActionMode.DISCOVER -> ActionMode.MARK
                    ActionMode.MARK -> ActionMode.UNMARK
                    ActionMode.UNMARK -> ActionMode.DISCOVER
                }
            }, modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Mode: $actionMode")
            }
            Text("Tablero:", modifier = Modifier.padding(top = 16.dp))
            BoardGrid(buscaminas.tableroFinal) { row, col ->
                when (actionMode) {
                    ActionMode.DISCOVER -> buscaminas.indicarDescubrimiento(row + 1, col + 1)
                    ActionMode.MARK -> buscaminas.marcar(row + 1, col + 1)
                    ActionMode.UNMARK -> buscaminas.desmarcar(row + 1, col + 1)
                }
                refresh++ // Trigger recomposition.
            }
        }
    }
}