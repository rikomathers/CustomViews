package com.example.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.customviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.apply {
            btn.setOnClickListener{
                val rows = editRows.text.toString().toInt()
                val columns = editColumns.text.toString().toInt()
                if (rows>2 && columns>2) showField(rows, columns)
                if (rows<=2) Toast.makeText(this@MainActivity, "Колличество строк не может быть меньше двух",Toast.LENGTH_SHORT).show()
                if (columns<=2) Toast.makeText(this@MainActivity, "Колличество столбцов не может быть меньше двух",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showField(rows:Int,columns:Int) = binding.apply {
        btn.visibility = View.GONE
        editColumns.visibility = View.GONE
        editRows.visibility = View.GONE
        mainText.visibility = View.GONE
        myView.ticTacToeItem = TicTacToeItem(rows, columns)
        myView.visibility = View.VISIBLE
    }
}