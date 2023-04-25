package com.example.customviews

enum class Cell {
    PLAYER_1,
    PLAYER_2,
    EMPTY
}


class TicTacToeItem(
    val rows: Int,
    val columns: Int
) {
    var isPlayer1 = true
    private val cells = Array(rows) { Array(columns) { Cell.EMPTY } }
    var listener:(field: TicTacToeItem) -> Unit = {}

    fun getCell(cell:CellNumber): Cell {
        return cells[cell.row][cell.column]
    }

    fun getCell(row: Int, column: Int): Cell {
        return cells[row][column]
    }

    fun setCell(cell: CellNumber) {
        if (getCell(cell) == Cell.EMPTY) {
            if (isPlayer1) {
                cells[cell.row][cell.column] = Cell.PLAYER_1
                isPlayer1 = !isPlayer1
            } else {
                cells[cell.row][cell.column] = Cell.PLAYER_2
                isPlayer1 = !isPlayer1
            }
            listener.invoke(this)
        }
    }
}