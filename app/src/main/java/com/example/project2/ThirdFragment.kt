package com.example.project2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.tv.TvContract.Programs.Genres.encode
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.project2.Stopwatch.StopwatchService
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder.encode
import java.security.MessageDigest
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var myContext: FragmentActivity

    private lateinit var callbackManager: CallbackManager


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        LoginManager.getInstance().


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewOfLayout = inflater.inflate(R.layout.fragment_third, container, false)
        FacebookSdk.sdkInitialize(myContext)

        callbackManager = CallbackManager.Factory.create()

        var loginButton = viewOfLayout.findViewById<LoginButton>(R.id.login_button)
//        loginButton.setPermissions(listOf("email", "public_profile"))
        loginButton.setPermissions(listOf("email", "public_profile"))
        Log.d("fick", "fock")
        loginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                Log.d("fuck", "fack")
                if(result?.accessToken != null) {
                    val accessToken = result.accessToken
                    getFacebookInfo(accessToken)
                } else {
                    Log.d("login","access token is null")
                }
//                var graphRequest = GraphRequest.newMeRequest(result?.accessToken, object: GraphRequest.GraphJSONObjectCallback {
//                    override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
//                        Log.v("result", `object`.toString())
//                    }
//                }
//            )
//                var parameters = Bundle()
//                parameters.putString("fields", "id,name,email,gender,birthday")
//                graphRequest.parameters = parameters
//                graphRequest.executeAsync()
            }
            override fun onCancel() {
                Log.e("Logincan", "can")
            }

            override fun onError(error: FacebookException?) {
                Log.e("LoginErr", error.toString())
            }

        })

        return viewOfLayout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun getFacebookInfo(accessToken: AccessToken) {
        val graphRequest = GraphRequest.newMeRequest(accessToken, object: GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(resultObject: JSONObject?, response: GraphResponse?) {
                try {
                    val name = resultObject?.getString("name")
                    val email = resultObject?.getString("email")
                    val image = resultObject?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                    Log.d("result", "name $name + email $email + image $image")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })

        var parameters = Bundle()
        parameters.putString("fields","id,name,email,picture.width(200")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}