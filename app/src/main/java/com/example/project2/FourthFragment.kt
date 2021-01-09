package com.example.project2

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.login.widget.ProfilePictureView
import com.facebook.login.widget.ProfilePictureView.LARGE
import com.facebook.login.widget.ProfilePictureView.SMALL
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FourthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FourthFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    private lateinit var viewOfLayout: View

    private lateinit var callbackManager: CallbackManager

    var myProfile: Profile? = null
    private lateinit var myProfilePictureView: ProfilePictureView

    private var userName: String? = null
    private var userEmail: String? = null
    private var userImage: String? = null


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
        viewOfLayout = inflater.inflate(R.layout.fragment_fourth, container, false)
        fragManager = myContext.supportFragmentManager


//________________________ facebook login ________________________


        callbackManager = CallbackManager.Factory.create()

        var loginButton = viewOfLayout.findViewById<LoginButton>(R.id.login_button)
        loginButton.setPermissions(listOf("email"))
        loginButton.setFragment(this)

        loginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if(result?.accessToken != null) {
//                    val accessToken = result.accessToken
//                    getFacebookInfo(accessToken)

                } else {
                    Log.d("login","access token is null")
                }
            }
            override fun onCancel() {
                Log.e("Logincan", "can")
            }
            override fun onError(error: FacebookException?) {
                Log.e("LoginErr", error.toString())
            }
        })


//______________________________ display profile image ____________________________
        myProfile = Profile.getCurrentProfile()
        myProfilePictureView = viewOfLayout.findViewById(R.id.userProfilePicture) as ProfilePictureView

        myProfilePictureView.setOnClickListener {
            fragTransaction = fragManager.beginTransaction()
            fragTransaction.detach(this).attach(this).commit()
        }

        if (myProfile != null) {
            myProfilePictureView.profileId = myProfile!!.id
        } else {
            myProfilePictureView.profileId = null
        }
        myProfilePictureView.presetSize = LARGE

//______________________________ display profile name ____________________________
        var myProfileNameView = viewOfLayout.findViewById<TextView>(R.id.userName)
        if (myProfile != null) {
            myProfileNameView.text = myProfile!!.name + "!"
        } else {
            myProfileNameView.text = "화이팅!"
        }

        return viewOfLayout
    }

    override fun onResume() {
        super.onResume()
        myProfile = Profile.getCurrentProfile()

        if (this::myProfilePictureView.isInitialized) {
            if (myProfile != null) {
                myProfilePictureView.profileId = myProfile!!.id
            } else {
                myProfilePictureView.profileId = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun getFacebookInfo(accessToken: AccessToken) {
        val graphRequest = GraphRequest.newMeRequest(accessToken, object: GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(resultObject: JSONObject?, response: GraphResponse?) {
                try {
                    userName = resultObject?.getString("name")
                    userEmail = resultObject?.getString("email")
                    userImage = resultObject?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                    Log.d("result", "name $userName + email $userEmail + image $userImage")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })

        var parameters = Bundle()
        parameters.putString("fields","id,name,email,picture.width(200)")
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
         * @return A new instance of fragment FourthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FourthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}