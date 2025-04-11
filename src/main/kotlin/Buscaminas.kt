import kotlin.random.Random

class Buscaminas {
    private lateinit var tablero: MutableList<StringBuilder>
    lateinit var tableroMinas: MutableList<StringBuilder>
    private lateinit var tableroBanderas: MutableList<StringBuilder>
    lateinit var tableroFinal:MutableList<StringBuilder>
    private var numMinas = 0



    // Controla el descubrimiento del mapa
    private fun descubrirMapa(x:Int, y:Int):Boolean {
//    Control de limites
        if (x !in tablero.indices || y !in tablero[x].indices) return false
//    no eres casilla sin probar
        if (tablero[x][y]!='-')return false

//    Momento/Instruccion que controla si esta posicion tiene
//    o no una mina con la funcion de comprobar_alrededor
        tablero[x][y]=comprobarAlrededor(x,y,tableroMinas)

//    Si sale una sola mina impide que siga este camino
        if (tablero[x][y]==' '){
//    arriba-izquierda
            descubrirMapa(x - 1, y - 1)

//    arriba
            descubrirMapa(x - 1, y)

//    arriba-derecha
            descubrirMapa(x - 1, y + 1)

//    izquierda
            descubrirMapa(x, y - 1)

//    derecha
            descubrirMapa(x, y + 1)

//    abajo-izquierda
            descubrirMapa(x + 1, y - 1)

//    abajo
            descubrirMapa(x + 1, y)

//    abajo-derecha
            descubrirMapa(x + 1, y + 1)
        }

        return true
    }

    //Controla las minas alrededor de una posicon dada. Si alguna casilla
// el numero de minas no coincide con el esperado esta aqui el
// problema ya ha sucedido
    private fun comprobarAlrededor(x:Int, y:Int, tablero:MutableList<StringBuilder>):Char {
        var contadorMinas= 0
//    superior izquierda
        if (x-1 in tablero.indices && y-1 in tablero[x].indices && tablero[x-1][y-1]=='*') {
            contadorMinas++
        }
        //    superior
        if (x - 1 in tablero.indices && y in tablero[x].indices && tablero[x - 1][y] == '*') {
            contadorMinas++
        }

//    superior derecha
        if (x - 1 in tablero.indices && y + 1 in tablero[x].indices && tablero[x - 1][y + 1] == '*') {
            contadorMinas++
        }

//    izquierda
        if (x in tablero.indices && y - 1 in tablero[x].indices && tablero[x][y - 1] == '*') {
            contadorMinas++
        }

//    derecha
        if (x in tablero.indices && y + 1 in tablero[x].indices && tablero[x][y + 1] == '*') {
            contadorMinas++
        }

//    inferior izquierda
        if (x + 1 in tablero.indices && y - 1 in tablero[x].indices && tablero[x + 1][y - 1] == '*') {
            contadorMinas++
        }

//    inferior
        if (x + 1 in tablero.indices && y in tablero[x].indices && tablero[x + 1][y] == '*') {
            contadorMinas++
        }

//    inferior derecha
        if (x + 1 in tablero.indices && y + 1 in tablero[x].indices && tablero[x + 1][y + 1] == '*') {
            contadorMinas++
        }

//    Devuelve o no un valor numerico dependiendo
//    de lo que indica el contador de minas
        return if (contadorMinas!=0) {
            contadorMinas.toString().first()
        }
        else {
            ' '
        }

    }




    // fun crear tablero
    fun crearTablero(x: Int, y: Int, numMinas: Int) {
        val temporal = mutableListOf<StringBuilder>()
        for (i in 0..<x) {
            val temporal2 = StringBuilder()
            for (j in 0..<y) {
                temporal2.append("-")
            }
            temporal.add(temporal2)
        }
//      diferenciación profunda chat gpt
        tablero=temporal.map { StringBuilder(it.toString()) }.toMutableList()
        tableroFinal=temporal.map { StringBuilder(it.toString()) }.toMutableList()
        tableroBanderas=temporal.map { StringBuilder(it.toString()) }.toMutableList()
        anadirMinas(temporal, numMinas)
        tableroMinas=temporal.map { StringBuilder(it.toString()) }.toMutableList()

    }

//    Create by ChatGPT
    private fun anadirMinas(temporal: MutableList<StringBuilder> ,numMinas: Int) {
        this.numMinas=numMinas
        val rows = temporal.size
        if (rows == 0) return
        val cols = temporal[0].length
        var placed = 0
        while (placed < numMinas) {
            val i = Random.nextInt(rows)
            val j = Random.nextInt(cols)
            if (temporal[i][j] != '*') {
                temporal[i].setCharAt(j, '*')
                placed++
            }
        }
    }

    fun indicarDescubrimiento(x: Int, y: Int){
        if (tableroBanderas[x-1][y-1]!='B') {
            descubrirMapa(x-1, y-1)
            actualizarFinal()
        }
    }
    fun marcar(x:Int, y:Int){
        if (tablero[x-1][y-1] == '-') {
            tableroBanderas[x-1][y-1]='*'
            actualizarFinal()
        }

    }
    fun desmarcar( x: Int, y: Int) {
        tableroBanderas[x-1][y-1]='-'
        actualizarFinal()
    }
    private fun actualizarFinal(){
        tableroFinal=tablero.map { StringBuilder(it.toString()) }.toMutableList()
//        Limpia banderas indebidas
        for (i in 0..< tablero.size){
            for (j in 0..<tablero[i].length) {
                if (tablero[i][j] != '-' && tableroBanderas[i][j] == '*') {
                    tableroBanderas[i][j]='-'
                }
            }
        }

//        Actualiza tablero final
        for (i in 0..<tableroBanderas.size) {
            for (j in 0..<tableroBanderas[i].length) {
                if (tableroBanderas[i][j] == '*') {
                    tableroFinal[i][j] = 'B'
                }
            }
        }
    }
    fun esMina(x:Int, y:Int, tipo:Char):Boolean{
        return tableroMinas[x-1][y-1] == '*' && tableroBanderas[x-1][y-1] == '-' && tipo == 'D'
    }
    fun ganar():Boolean{
        var numGuiones=0
        for (i in 0..<tablero.size){
            for (j in 0..<tablero[i].length) {
                if (tablero[i][j]=='-') {
                    numGuiones++
                }
            }
        }
        return numGuiones==numMinas
    }
}