package com.example.project2.Omok

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.project2.BaseApplication
import com.example.project2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragmentGame.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragmentGame : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var turn = BaseApplication.blackStone
    private var cellWidth = 55
    private var emptySize = 88
//    private var omok = viewR.id.omok

    private var omokValue = Array(19) { Array(19) { 0 } }

    private lateinit var myContext: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_third_game, container, false)
        var displayMetrics = DisplayMetrics()
        myContext.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        var x = displayMetrics.widthPixels
        var y = displayMetrics.heightPixels

        var omok: ImageView = view.findViewById<ImageView>(R.id.omok)

        Log.d("Omok", "x : $x , y : $y")

        var bitmap = Bitmap.createBitmap(x - emptySize, x - emptySize, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        var paint = Paint()
        canvas.drawColor(ContextCompat.getColor(myContext, R.color.omok_background))
        paint.color = ContextCompat.getColor(myContext, R.color.black)
        paint.strokeWidth = 1.0f
        paint.style = Paint.Style.STROKE
        for (num1 in 0..18) {
            canvas.drawLine(
                0.0f,
                (cellWidth * num1).toFloat(),
                x.toFloat(),
                (cellWidth * num1).toFloat(),
                paint
            )
            canvas.drawLine(
                (cellWidth * num1).toFloat(),
                0.0f,
                (cellWidth * num1).toFloat(),
                x.toFloat(),
                paint
            )
        }

        omok.setImageBitmap(bitmap)

        omok.setOnTouchListener { v: View?, event: MotionEvent? ->
            Log.d("Omok", " position X : ${event?.x} / Y : ${event?.y} ")
            var x = event!!.x
            var y = event.y

            var xCount = Math.round(x / cellWidth)
            var yCount = Math.round(y / cellWidth)

            if (omokValue[xCount][yCount] != 0) {
                Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show()
            } else {
                var circlePaint = Paint()
                when (turn) {
                    BaseApplication.blackStone -> {
                        circlePaint.color = ContextCompat.getColor(myContext, R.color.black)
                        omokValue[xCount][yCount] = BaseApplication.blackStone
                    }
                    BaseApplication.whiteStone -> {
                        circlePaint.color = ContextCompat.getColor(myContext, R.color.white)
                        omokValue[xCount][yCount] = BaseApplication.whiteStone
                    }
                }
                canvas.drawCircle(
                    xCount * cellWidth.toFloat(),
                    yCount * cellWidth.toFloat(),
                    cellWidth / 2.0f,
                    circlePaint
                )
                omok.setImageBitmap(bitmap)
                if (horizonCheck(xCount, yCount, turn) || verticalCheck(
                        xCount,
                        yCount,
                        turn
                    ) || rightDownCheck(xCount, yCount, turn) || rightUpCheck(xCount, yCount, turn)
                ) {
                    Toast.makeText(myContext, "${turn}이 이겼습니다.", Toast.LENGTH_SHORT).show()
                }
                turn = if (turn == BaseApplication.blackStone) BaseApplication.whiteStone else BaseApplication.blackStone
            }
            return@setOnTouchListener false
        }
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragmentGame.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragmentGame().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun horizonCheck(x: Int, y: Int, turn: Int): Boolean {
        return (currentLeftCheck(x, y, turn) + currentRightCheck(x, y, turn) == 4)
    }

    fun verticalCheck(x: Int, y: Int, turn: Int): Boolean {
        return (currentUpCheck(x, y, turn) + currentDownCheck(x, y, turn) == 4)
    }

    fun rightUpCheck(x: Int, y: Int, turn: Int): Boolean { // 우상향
        return (currentLeftDownCheck(x, y, turn) + currentRightUpCheck(x, y, turn) == 4)
    }

    fun rightDownCheck(x: Int, y: Int, turn: Int): Boolean { // 우하향
        return (currentLeftUpCheck(x, y, turn) + currentRightDownCheck(x, y, turn) == 4)
    }


    fun currentLeftCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x - 1 - count][y] == turn) {
            count++
        }
        return count
    }

    fun currentRightCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x + 1 + count][y] == turn) {
            count++
        }
        return count
    }

    fun currentUpCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x][y + 1 + count] == turn) {
            count++
        }
        return count
    }

    fun currentDownCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x][y - 1 - count] == turn) {
            count++
        }
        return count
    }

    fun currentLeftDownCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x - 1 - count][y - 1 - count] == turn) {
            count++
        }
        return count
    }

    fun currentRightUpCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x + 1 + count][y + 1 + count] == turn) {
            count++
        }
        return count
    }

    fun currentLeftUpCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x - 1 - count][y + 1 + count] == turn) {
            count++
        }
        return count
    }

    fun currentRightDownCheck(x: Int, y: Int, turn: Int): Int {
        var count = 0
        while (omokValue[x + 1 + count][y - 1 - count] == turn) {
            count++
        }
        return count
    }
}