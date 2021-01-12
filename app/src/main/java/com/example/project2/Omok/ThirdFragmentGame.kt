package com.example.project2.Omok

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.AsyncTask
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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.project2.BaseApplication
import com.example.project2.R
import com.example.project2.Util.*
import com.facebook.Profile
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.KeyStore

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
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction
    private lateinit var thisFragment: ThirdFragmentGame

    private lateinit var viewOfLayout: View

    private lateinit var myProfile: Profile

    lateinit var roomNumber: String

    private var waitForOpponent = true
    var waitForNextMove = false
    var initial_state = true

    private lateinit var waitingMessage: TextView
    var currentOpponent: User? = null

    var myTurn = true
    var isBlack = true
    var circlePaint = Paint()
    lateinit var canvas: Canvas


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
        myProfile = Profile.getCurrentProfile()
        Log.d("profile", "init")
        thisFragment = this
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_third_game, container, false)
        fragManager = myContext.supportFragmentManager



        // ______________대기 메세지 띄우기______________________________________
        waitingMessage = viewOfLayout.findViewById<TextView>(R.id.waitingMessage)
        waitingMessage.text = "${roomNumber}번 방에서 상대방을 찾는 중입니다."

        // ______________대기하기________________________________________________
        if (currentOpponent == null) {
            val waitOpponentAsync = WaitOpponentAsync()
            waitOpponentAsync.execute()
        } else {
            waitingMessage.text = "${currentOpponent!!.name} 님과 대결 중입니다. 화이팅 하세요"
        }

        var displayMetrics = DisplayMetrics()
        myContext.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        var x_pixels = displayMetrics.widthPixels
        var y_pixels = displayMetrics.heightPixels

        var omok: ImageView = viewOfLayout.findViewById<ImageView>(R.id.omok)

        Log.d("Omok", "x : $x_pixels , y : $y_pixels")


        var bitmap = Bitmap.createBitmap(x_pixels - emptySize, x_pixels - emptySize, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        var paint = Paint()
        canvas.drawColor(ContextCompat.getColor(myContext, R.color.omok_background))
        paint.color = ContextCompat.getColor(myContext, R.color.black)
        paint.strokeWidth = 1.0f
        paint.style = Paint.Style.STROKE
        for (num1 in 0..18) {
            canvas.drawLine(
                0.0f,
                (cellWidth * num1).toFloat(),
                x_pixels.toFloat(),
                (cellWidth * num1).toFloat(),
                paint
            )
            canvas.drawLine(
                (cellWidth * num1).toFloat(),
                0.0f,
                (cellWidth * num1).toFloat(),
                x_pixels.toFloat(),
                paint
            )
        }

        omok.setImageBitmap(bitmap)


        omokValue.forEachIndexed {x_iter, column ->
            column.forEachIndexed { y_iter, color ->
                if (color == 1) {
                    circlePaint.color = ContextCompat.getColor(myContext, R.color.black)
                    canvas.drawCircle(x_iter * cellWidth.toFloat(),
                        y_iter * cellWidth.toFloat(),
                        cellWidth / 2.0f,
                        circlePaint
                    )
                } else if (color == 2) {
                    circlePaint.color = ContextCompat.getColor(myContext, R.color.white)
                    canvas.drawCircle(x_iter * cellWidth.toFloat(),
                        y_iter * cellWidth.toFloat(),
                        cellWidth / 2.0f,
                        circlePaint
                    )
                }
            }
        }


        if (!myTurn && initial_state) {
            val waitNextMoveAsync = WaitNextMoveAsync()
            waitNextMoveAsync.execute()
            initial_state = false
        }


        omok.setOnTouchListener { v: View?, event: MotionEvent? ->
            Log.d("Omok", " position X : ${event?.x} / Y : ${event?.y} ")
            var x = event!!.x
            var y = event.y

            var xCount = Math.round(x / cellWidth)
            var yCount = Math.round(y / cellWidth)

            if (omokValue[xCount][yCount] != 0) {
                Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show()
            } else {
                if (myTurn) {
                    if (isBlack) {
                        circlePaint.color = ContextCompat.getColor(myContext, R.color.black)
                        omokValue[xCount][yCount] = BaseApplication.blackStone
                    } else {
                        circlePaint.color = ContextCompat.getColor(myContext, R.color.white)
                        omokValue[xCount][yCount] = BaseApplication.whiteStone
                    }
                    canvas.drawCircle(
                        xCount * cellWidth.toFloat(),
                        yCount * cellWidth.toFloat(),
                        cellWidth / 2.0f,
                        circlePaint
                    )

                    var retrofitGameSendMove = RetrofitGameSendMove()
                    var sendMoveCall = retrofitGameSendMove.sendMoveService
                        .post(
                            Move(
                                roomnumber = roomNumber, id = myProfile.id, name = myProfile.name,
                                coordinates = Coordinates(xCount, yCount)
                            )
                        )
                    sendMoveCall.enqueue(object: Callback<GameRoomBluePrint> {
                        override fun onResponse(
                            call: Call<GameRoomBluePrint>,
                            response: Response<GameRoomBluePrint>
                        ) {
                            val newRoom_Json = response.body()!!.data

                            myTurn = false
                            waitForNextMove = true

                            omok.setImageBitmap(bitmap)
                            if (horizonCheck(xCount, yCount, turn) || verticalCheck(
                                    xCount,
                                    yCount,
                                    turn
                                ) || rightDownCheck(xCount, yCount, turn) || rightUpCheck(xCount, yCount, turn)
                            ) {
                                Toast.makeText(myContext, "${turn}이 이겼습니다.", Toast.LENGTH_SHORT).show()
                            }

                            Log.d("wait", "async직전")
                            var waitNextMoveAsync = WaitNextMoveAsync()
                            waitNextMoveAsync.execute()

                        }

                        override fun onFailure(
                            call: Call<GameRoomBluePrint>,
                            t: Throwable
                        ) {
                            Log.d("SendMove", "onFailure" + t.message)
                        }
                    })
                }
            }
            return@setOnTouchListener false
        }
        // Inflate the layout for this fragment
        return viewOfLayout
    }

    inner class WaitOpponentAsync : AsyncTask<Any?, Any?, Any?>() {

        override fun doInBackground(vararg params: Any?): Any? {

            while (waitForOpponent) {

                var retrofitGameWaitOpponent = RetrofitGameWaitOpponent()
                var waitOpponentCall = retrofitGameWaitOpponent.waitOpponentService.get(roomNumber)

                Thread.sleep(1000)
                waitOpponentCall.enqueue(object: Callback<GameRoomBluePrint> {
                    override fun onResponse(
                        call: Call<GameRoomBluePrint>,
                        response: Response<GameRoomBluePrint>
                    ) {
                        val myRoom_Json = response.body()!!.data
                        val state = myRoom_Json[0].asJsonObject["state"].toString().replace("\"", "")
                        if (state == "play") {
                            val opponent = User(id = (myRoom_Json[0].asJsonObject["user2"] as JsonObject)["id"].toString(),
                                name = (myRoom_Json[0].asJsonObject["user2"] as JsonObject)["name"].toString().replace("\"", ""))
                            waitingMessage.text = "${opponent.name} 님과 대결 중입니다. 화이팅 하세요"

                            if (waitForOpponent) {
                                var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                                builder.setTitle("${opponent.name} 님이 감히 승부를 걸어왔습니다")
                                builder.setPositiveButton("부순다", object: DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface, which:Int) {}
                                })
                                builder.show()
                            }

                            currentOpponent = opponent
                            waitForOpponent = false
                        }
                    }

                    override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                        Log.d("WaitOpponent", "onFailure" + t.message)
                    }
                })
            }
            return null
        }
    }

    inner class WaitNextMoveAsync : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(vararg params: Any?): Any? {
            Log.d("while", "out")
            while (waitForNextMove) {
                Log.d("while", "in")
                var retrofitGameWaitMove = RetrofitGameWaitMove()
                var waitMoveCall = retrofitGameWaitMove.waitMoveService.get(roomNumber = roomNumber)

                Thread.sleep(1000)

                waitMoveCall.enqueue(object: Callback<GameRoomBluePrint> {
                    override fun onResponse(
                        call: Call<GameRoomBluePrint>,
                        response: Response<GameRoomBluePrint>
                    ) {
                        val myRoom_Json = response.body()!!.data
                        val lastMover = myRoom_Json[0].asJsonObject["turn"].toString().replace("\"", "")
                        Log.d("lastmover", "$lastMover")
                        Log.d("myID", myProfile.id)

                        if (myProfile.id != lastMover && waitForNextMove) {
                            val new_x = Integer.parseInt(myRoom_Json[0].asJsonObject["x"].toString())
                            val new_y = Integer.parseInt(myRoom_Json[0].asJsonObject["y"].toString())
                            Log.d("지영", "무브 $new_x, $new_y")

                            if (isBlack) {
                                circlePaint.color = ContextCompat.getColor(myContext, R.color.white)
                                omokValue[new_x][new_y] = BaseApplication.whiteStone
                            } else {
                                circlePaint.color = ContextCompat.getColor(myContext, R.color.black)
                                omokValue[new_x][new_y] = BaseApplication.blackStone
                            }

                            canvas.drawCircle(
                                new_x * cellWidth.toFloat(),
                                new_y * cellWidth.toFloat(),
                                cellWidth / 2.0f,
                                circlePaint
                            )

                            myTurn = true
                            waitForNextMove = false


                            fragTransaction = fragManager.beginTransaction()
                            fragTransaction.detach(thisFragment).attach(thisFragment).commit()
                        }

                    }

                    override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                        Log.d("WaitMove", "onFailure" + t.message)
                    }
                })
            }
            return null
        }
    }

    fun surrender() {
        Log.d("surrender", "nono")
        waitForNextMove = false
        myProfile = Profile.getCurrentProfile()
        var retrofitGameSurrender = RetrofitGameSurrender()
        var surrenderCall = retrofitGameSurrender.surrenderService.get(myProfile.id, roomNumber)

        surrenderCall.enqueue(object: Callback<GameRoomBluePrint> {
            override fun onResponse(
                call: Call<GameRoomBluePrint>,
                response: Response<GameRoomBluePrint>
            ) {
                omokValue = Array(19) { Array(19) { 0 } } // 바둑판 엎기

                var builder : AlertDialog.Builder= AlertDialog.Builder(myContext)
                builder.setTitle("기권했습니다")
                builder.setPositiveButton("확인", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which:Int) {
                        fragManager.popBackStack()
                    }
                }).show()
            }

            override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                Log.d("Surrender", "onFailure" + t.message)
            }
        })
    }

    fun victory(move: Move) {
        var retrofitGameVictory = RetrofitGameVictory()
        var victoryCall = retrofitGameVictory.victoryService.post(move)

        victoryCall.enqueue(object: Callback<GameRoomBluePrint> {
            override fun onResponse(
                call: Call<GameRoomBluePrint>,
                response: Response<GameRoomBluePrint>
            ) {
                waitForNextMove = false
                omokValue = Array(19) { Array(19) { 0 } } // 바둑판 엎기

                var builder : AlertDialog.Builder= AlertDialog.Builder(myContext)
                builder.setTitle("승리하였습니다!")
                builder.setPositiveButton("YAY", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which:Int) {

                        fragManager.popBackStack()
                    }
                })

            }

            override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                Log.d("Victory", "onFailure" + t.message)
            }
        })

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