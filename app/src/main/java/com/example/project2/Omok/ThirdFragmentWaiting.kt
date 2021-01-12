package com.example.project2.Omok

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project2.R
import com.example.project2.Util.*
import com.facebook.Profile
import com.facebook.login.widget.ProfilePictureView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThridFragmentWaiting.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragmentWaiting : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View
    private lateinit var opponentView: RecyclerView
    private lateinit var opponentAdapter: OpponentAdapter

    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction
    private var thisFragment = this

    lateinit var thirdFragmentGame: ThirdFragmentGame

    private var myProfile: Profile? = null

    var rooms = arrayListOf<Room>(Room(roomnumber = 1, user1 = User(name = "박상준", id = "2905335259711277"), user2 = null, state = "wait"))

    var shouldRefresh = true

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_thrid_waiting, container, false)

        fragManager = myContext.supportFragmentManager
        viewManager = LinearLayoutManager(myContext)

        var waitingTitleView = viewOfLayout.findViewById<TextView>(R.id.waitingTitleView)

        // ______________ 맨 위 제목 세팅 ___________________________________________________________
        myProfile = Profile.getCurrentProfile()
        if (myProfile == null) {
            waitingTitleView.setText("페이스북에 로그인해주세요")
        } else {
            waitingTitleView.setText(myProfile!!.name + "~ 그 상대는!")
        }
        waitingTitleView.setOnClickListener {
            fragTransaction = fragManager.beginTransaction()
            fragTransaction.detach(this).attach(this).commit() }

        // _______________ 아래 참가자 리스트 세팅 __________________________________________________
        opponentAdapter = OpponentAdapter(myContext, rooms)
        opponentView = viewOfLayout.findViewById(R.id.opponentRecyclerView)

        opponentView.adapter = opponentAdapter
        opponentView.layoutManager = viewManager

        // __________ 새로고침 버튼 _________________________________________________________________

        var refreshButton = viewOfLayout.findViewById<Button>(R.id.refreshButton)
        refreshButton.setOnClickListener {
            myProfile = Profile.getCurrentProfile()

            if (myProfile == null) {
                var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                builder.setTitle("페이스북 계정 로그인이 필요합니다")
                builder.setPositiveButton("확인", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which:Int) {}
                })
                builder.show()
            } else {
                refresh()
            }
        }
        // __ 방만들기 버튼 __________________________________________________________________________________________
        var makeRoomButton = viewOfLayout.findViewById<Button>(R.id.mkRoomButton)
        makeRoomButton.setOnClickListener {
            myProfile = Profile.getCurrentProfile()

            if (myProfile == null) {
                var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                builder.setTitle("페이스북 계정 로그인이 필요합니다")
                builder.setPositiveButton("확인", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which:Int) {}
                })
                builder.show()
            } else {
                var retrofitGameMakeRoom = RetrofitGameMakeRoom()
                var makeRoomCall = retrofitGameMakeRoom.makeRoomService.post(User(id = myProfile!!.id, name = myProfile!!.name))

                makeRoomCall.enqueue(object: Callback<GameRoomBluePrint> {
                    override fun onResponse(
                        call: Call<GameRoomBluePrint>,
                        response: Response<GameRoomBluePrint>
                    ) {
                        val newRoom_Json = response.body()!!.data // 이번에 만들어진 방만 담고있는 리스트
                        val roomNumber = newRoom_Json[0].asJsonObject["roomnumber"].toString()

                        thirdFragmentGame.roomNumber = roomNumber
                        thirdFragmentGame.isBlack = true
                        thirdFragmentGame.myTurn = true

                        fragTransaction = fragManager.beginTransaction()
                        fragTransaction.replace(R.id.thirdFragment, thirdFragmentGame)
                        fragTransaction.addToBackStack(null)
                        fragTransaction.commit()

                    }

                    override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                        Log.d("MakeRoom", "onFailure" + t.message)
                    }
                })
            }
        }

        return viewOfLayout
    }

    inner class OpponentAdapter(val context: Context, val rooms: ArrayList<Room>) : RecyclerView.Adapter<OpponentAdapter.Holder>() {
        private var displayOpponents = rooms

        inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
            val profileImg = itemView?.findViewById<ProfilePictureView>(R.id.opponentProfile)
            val name = itemView?.findViewById<TextView>(R.id.opponentName)
            val status = itemView?.findViewById<TextView>(R.id.roomStatus)

            @SuppressLint("SetTextI18n")
            fun bind (room: Room) {
                profileImg?.profileId = room.user1.id
                status?.text = "${room.roomnumber}번방: " + room.state
                if (room.state == "wait") {
                    name?.text = room.user1.name + "!!"

                } else {
                    name?.text = room.user1.name + " VS " + room.user2!!.name
                    name?.textSize = 20.0F
                    name?.gravity = Gravity.CENTER
                }
            }
        }

        init {
            displayOpponents = rooms
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(displayOpponents[position])
            holder.itemView.setOnClickListener {
                var myProfile = Profile.getCurrentProfile()

                if (myProfile == null) {
                    var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                    builder.setTitle("페이스북 계정 로그인이 필요합니다")
                    builder.setPositiveButton("확인", object: DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which:Int) {}
                    })
                    builder.show()
                } else {
                    var retrofitGameBeOpponent = RetrofitGameBeOpponent()
                    var beOpponentCall = retrofitGameBeOpponent.beOpponentService
                        .post(Challenge(roomnumber = displayOpponents[position].roomnumber.toString(),
                            id = myProfile.id, name = myProfile.name))

                    beOpponentCall.enqueue(object: Callback<GameRoomBluePrint> {
                        override fun onResponse(
                            call: Call<GameRoomBluePrint>,
                            response: Response<GameRoomBluePrint>
                        ) {
                            if (response.isSuccessful) {
                                val myRoom_Json = response.body()!!.data
                                val roomNumber = myRoom_Json[0].asJsonObject["roomnumber"].toString()

                                val user1 = User(id = (myRoom_Json[0].asJsonObject["user1"] as JsonObject)["id"].toString(),
                                    name = (myRoom_Json[0].asJsonObject["user1"] as JsonObject)["name"].toString().replace("\"", ""))
                                val user2 = User(id = (myRoom_Json[0].asJsonObject["user2"] as JsonObject)["id"].toString(),
                                    name = (myRoom_Json[0].asJsonObject["user2"] as JsonObject)["name"].toString().replace("\"", ""))

                                val state = myRoom_Json[0].asJsonObject["state"].toString().replace("\"", "")

                                if (user2.id == myProfile.id) {
                                    thirdFragmentGame.roomNumber = roomNumber
                                    thirdFragmentGame.currentOpponent = user1
                                    thirdFragmentGame.isBlack = false
                                    thirdFragmentGame.myTurn = false
                                    thirdFragmentGame.waitForNextMove = true

                                    fragTransaction = fragManager.beginTransaction()
                                    fragTransaction.replace(R.id.thirdFragment, thirdFragmentGame)
                                    fragTransaction.addToBackStack(null)
                                    fragTransaction.commit()
                                } else {
                                    var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                                    builder.setTitle("방이 가득 찼습니다.")
                                    builder.setPositiveButton("다음 기회를 노린다", object: DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface, which:Int) {}
                                    })
                                    builder.show()
                                }

                            } else {
                                Log.d("Be Opponent", "Fail onResponse")
                            }
                        }

                        override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                            Log.d("BeOpponent", "onFailure" + t.message)
                        }
                    })
                }

            }
        }

        override fun getItemCount(): Int {
            return rooms.size
        }
    }

    private fun refresh() {

        var retrofitGameRoomDownload = RetrofitGameRoomDownload()
        var downloadCall = retrofitGameRoomDownload.downloadService.get(myProfile!!.id, myProfile!!.name)

        downloadCall.enqueue(object: Callback<GameRoomBluePrint> {
            override fun onResponse(
                call: Call<GameRoomBluePrint>,
                response: Response<GameRoomBluePrint>
            ) {
                rooms = ArrayList() // room 초기화
                if (response.isSuccessful) {

                    val roomList_Json = response.body()!!.data
                    println(roomList_Json)
                    roomList_Json.forEach{
                        val state = it.asJsonObject["state"].toString().replace("\"", "")

                        if (state != "boom") {
                            val roomnumber = Integer.parseInt(it.asJsonObject["roomnumber"].toString())
                            val user1 = User(id = (it.asJsonObject["user1"] as JsonObject)["id"].toString(),
                                name = (it.asJsonObject["user1"] as JsonObject)["name"].toString().replace("\"", ""))

                            var user2: User? = null
                            if (state == "play") {
                                user2 = User(id = (it.asJsonObject["user2"] as JsonObject)["id"].toString(),
                                    name = (it.asJsonObject["user2"] as JsonObject)["name"].toString().replace("\"", ""))
                            }
                            rooms.add(Room(roomnumber, user1, user2, state))
                        }
                    }
                    Thread.sleep(200)
                    fragTransaction = fragManager.beginTransaction()
                    fragTransaction.detach(thisFragment).attach(thisFragment).commit()

                } else {
                    Log.d("DownloadRoom", "onResponse: 실패")
                }
            }

            override fun onFailure(call: Call<GameRoomBluePrint>, t: Throwable) {
                Log.d("DownloadRoom", "onFailure" + t.message)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (shouldRefresh) {
            refresh()
            shouldRefresh = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThridFragmentWaiting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragmentWaiting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

