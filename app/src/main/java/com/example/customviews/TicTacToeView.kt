package com.example.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class TicTacToeView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrs: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttrs, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) : this(context, attrs, defStyleAttrs, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var player1Color by Delegates.notNull<Int>()
    private var player2Color by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    private lateinit var player1Paint: Paint
    private lateinit var player2Paint: Paint
    private lateinit var gridPaint: Paint
    private lateinit var currentCellPaint: Paint

    init {
        if (attrs!=null) initAttrs(attrs, defStyleAttrs, defStyleRes)
        else initDefaultColors()
        initPaints()
        initDefaultSettings()
    }

    private fun initDefaultSettings(){
        isFocusable = true
        isClickable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }
    }

    private fun initAttrs(attrs: AttributeSet, defStyleAttrs: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TicTacToeView, defStyleAttrs, defStyleRes)
        parseAttr(typedArray)
        typedArray.recycle()
    }

    private fun parseAttr(typedArray: TypedArray) {
        player1Color = typedArray.getColor(R.styleable.TicTacToeView_player1Color, player1DefaultColor)
        player2Color = typedArray.getColor(R.styleable.TicTacToeView_player2Color, player2DefaultColor)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, gridDefaultColor)
    }

    private fun initDefaultColors() {
        player1Color = player1DefaultColor
        player2Color = player2DefaultColor
        gridColor = gridDefaultColor
    }

    private fun initPaints() {
        player1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player1Paint.color = player1Color
        player1Paint.style = Paint.Style.STROKE
        player1Paint.strokeWidth = fromDipToPix(3f)

        player2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player2Paint.color = player2Color
        player2Paint.style = Paint.Style.STROKE
        player2Paint.strokeWidth = fromDipToPix(3f)

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = fromDipToPix(1f)

        currentCellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        currentCellPaint.color = currentCellDefaultColor
        currentCellPaint.style = Paint.Style.FILL
    }

    var ticTacToeItem: TicTacToeItem = TicTacToeItem(0,0)
        set(value) {
            field = value
            requestLayout()
            invalidate()
            setSizes()
        }



    private var cellSize = 0f
    private var cellPadding = 0f
    private val fieldRect = RectF(0f, 0f, 0f, 0f)
    private val cellRect = RectF(0f, 0f, 0f, 0f)
    private var currentCell : CellNumber? = null
    private var currentCellForKeys : CellNumber? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingBottom + paddingTop
        val columns = ticTacToeItem.columns
        val rows = ticTacToeItem.rows
        val cellSize = fromDipToPix(defaultCellSize).toInt()
        val desiredWidth = max(minWidth, columns * cellSize + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight, rows * cellSize + paddingTop + paddingBottom)
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    private fun fromDipToPix(dip:Float):Float{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setSizes()
    }

    private fun setSizes() {
        val field = ticTacToeItem
        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom
        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()
        cellSize = min(cellHeight, cellWidth)
        cellPadding = cellSize * 0.2f
        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows
        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (ticTacToeItem.columns == 0 || ticTacToeItem.rows == 0) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return
        drawCurrentCell(canvas)
        drawCurrentCellForKey(canvas)
        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val startX = fieldRect.left
        val endX = fieldRect.right
        val startY = fieldRect.top
        val endY = fieldRect.bottom
        for (i in 0..ticTacToeItem.rows) {
            val step = startY + i * cellSize
            canvas.drawLine(startX, step, endX, step, gridPaint)
        }
        for (i in 0..ticTacToeItem.columns) {
            val step = startX + i * cellSize
            canvas.drawLine(step, startY, step, endY, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        for (row in 0 until ticTacToeItem.rows) {
            for (column in 0 until ticTacToeItem.columns) {
                val cell = ticTacToeItem.getCell(row, column)
                if (cell == Cell.PLAYER_1) {
                    drawPlayer1(row, column, canvas)
                } else if (cell == Cell.PLAYER_2) {
                    drawPlayer2(row, column, canvas)
                }
            }
        }
    }

    private fun drawPlayer1(row: Int, column: Int, canvas: Canvas) {
        updateCurrentCellRect(CellNumber(row, column))
        canvas.drawLine(cellRect.left, cellRect.top, cellRect.right, cellRect.bottom, player1Paint)
        canvas.drawLine(cellRect.right, cellRect.top, cellRect.left, cellRect.bottom, player1Paint)
    }

    private fun drawPlayer2(row: Int, column: Int, canvas: Canvas) {
        updateCurrentCellRect(CellNumber(row, column))
        canvas.drawCircle(cellRect.centerX(), cellRect.centerY(), cellRect.width()/2, player2Paint)
    }

    private fun updateCurrentCellRect(cell:CellNumber) {
        cellRect.left = fieldRect.left + cell.column * cellSize + cellPadding
        cellRect.top = fieldRect.top + cell.row * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding * 2
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2
    }

    private fun drawCurrentCell(canvas: Canvas){
        currentCell?.let {
            updateCurrentCellRect(it)
            canvas.drawRect(
                cellRect.left - cellPadding, cellRect.top - cellPadding,
                cellRect.right + cellPadding, cellRect.bottom + cellPadding,
                currentCellPaint
            )
        }
    }

    private fun drawCurrentCellForKey(canvas: Canvas){
        currentCellForKeys?.let {
            updateCurrentCellRect(it)
            canvas.drawRect(
                cellRect.left - cellPadding, cellRect.top - cellPadding,
                cellRect.right + cellPadding, cellRect.bottom + cellPadding,
                currentCellPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                updateCurrentCell(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                val cell = getCellNumber(event)
                cell?.let {
                    ticTacToeItem.setCell(it)
                } ?: currentCellForKeys?.let {
                    ticTacToeItem.setCell(it)
                }
                currentCell = null
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateCurrentCell(event)
                invalidate()
                return true
            }
        }
        return false
    }

    private fun updateCurrentCell(event: MotionEvent) {
        val cell = getCellNumber(event)
        if (cell != null && cell != currentCell && ticTacToeItem.getCell(cell) == Cell.EMPTY) {
            currentCell = cell
        }
    }

    private fun getCellNumber(event: MotionEvent): CellNumber? {
        return if (event.x <= fieldRect.left || event.x >= fieldRect.right ||
            event.y <= fieldRect.top || event.y >= fieldRect.bottom
        ) {
            null
        } else {
            CellNumber(
                ((event.y - fieldRect.top) / cellSize).toInt(),
                ((event.x - fieldRect.left) / cellSize).toInt()
            )
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        currentCell?.let {
            ticTacToeItem.setCell(it)
        } ?: currentCellForKeys?.let {
            ticTacToeItem.setCell(it)
        }
        currentCell = null
        invalidate()
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> return moveCurrentCell(1, 0)
            KeyEvent.KEYCODE_DPAD_UP -> return moveCurrentCell(-1, 0)
            KeyEvent.KEYCODE_DPAD_RIGHT -> return moveCurrentCell(0, 1)
            KeyEvent.KEYCODE_DPAD_LEFT -> return moveCurrentCell(0, -1)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun moveCurrentCell(row: Int, column: Int): Boolean {
        if (currentCellForKeys == null) {
            currentCellForKeys = CellNumber(0, 0)
        } else {
            currentCellForKeys?.let { cell ->
                if (cell.column + column >= 0 && cell.column + column < ticTacToeItem.columns) {
                    cell.column += column
                }
                if (cell.row + row >= 0 && cell.row + row < ticTacToeItem.rows) {
                    cell.row += row
                }
            }
        }
        invalidate()
        return true
    }

    companion object {
        const val player1DefaultColor = Color.GREEN
        const val player2DefaultColor = Color.RED
        const val gridDefaultColor = Color.BLACK
        val currentCellDefaultColor = Color.rgb(230,230,230)
        const val defaultCellSize = 50f
    }
}