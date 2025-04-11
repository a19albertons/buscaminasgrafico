// File: src/main/kotlin/EstadoJuego.kt
package motorbuscaminas

import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

class EstadoJuego {
    val gameStarted = mutableStateOf(false)
    val gameLost = mutableStateOf(false)
    val gameWon = mutableStateOf(false)
    val refresh = mutableStateOf(0)
    val actionMode = mutableStateOf(ActionMode.DISCOVER)
    val buscaminas = Buscaminas()

    fun iniciarJuego(mode: String) {
        when (mode) {
            "Classic" -> buscaminas.crearTablero(8, 8, 9)
            "Easy" -> buscaminas.crearTablero(9, 9, 10)
            "Medium" -> buscaminas.crearTablero(16, 16, 40)
            "Expert" -> buscaminas.crearTablero(16, 30, 99)
        }
        gameStarted.value = true
    }

    fun onCellClick(row: Int, col: Int) {
        when (actionMode.value) {
            ActionMode.DISCOVER -> {
                buscaminas.indicarDescubrimiento(row + 1, col + 1)
                if (buscaminas.esMina(row + 1, col + 1, 'D')) {
                    gameLost.value = true
                }
            }
            ActionMode.MARK -> buscaminas.marcar(row + 1, col + 1)
            ActionMode.UNMARK -> buscaminas.desmarcar(row + 1, col + 1)
        }
        refresh.value++
        if (buscaminas.ganar()) {
            gameWon.value = true
        }
    }

    fun changeActionMode() {
        actionMode.value = when (actionMode.value) {
            ActionMode.DISCOVER -> ActionMode.MARK
            ActionMode.MARK -> ActionMode.UNMARK
            ActionMode.UNMARK -> ActionMode.DISCOVER
        }
    }
}