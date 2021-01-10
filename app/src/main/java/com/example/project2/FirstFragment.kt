package com.example.project2

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.Call
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.PermissionChecker.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.hurrypizza.test.Contact.ContactAdapter
import com.hurrypizza.test.Contact.ContactItem
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.security.auth.callback.Callback


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var dirty_bit: Int = 0

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val PERMISSION_READ_CONTACT: Int = 101

    private lateinit var rv_contact: RecyclerView
    private lateinit var tv_permission: TextView
    private lateinit var sv_contact: SearchView
    private lateinit var bt_cloud: FloatingActionButton

    private var adapter: ContactAdapter? = null

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
        val rootview: View? = inflater.inflate(R.layout.fragment_first, container, false)

        tv_permission = rootview?.findViewById(R.id.tv_permission)!!
        rv_contact = rootview.findViewById(R.id.rv_contact)!!
        sv_contact = rootview.findViewById(R.id.sv_contact)!!
        bt_cloud = rootview.findViewById(R.id.bt_cloud)!!

        if (checkAndRequestPermission() == true) {
            onPermissionGranted()
        } else {
            val spannable = SpannableStringBuilder("연락처를 불러올 수 없습니다.\n이곳을 눌러 권한을 설정해주세요.")
            spannable.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.teal_200)),
                17, 19,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            val clickableSpan = object: ClickableSpan(){
                override fun onClick(widget: View) {
                    if (checkAndRequestPermission()==false) {
                        if (!shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)) {
                            Toast.makeText(
                                context,
                                "권한이 거절되었습니다. 설정에서 권한을 허용해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                        onPermissionGranted()
                    }

                }
            }
            spannable.setSpan(
                clickableSpan,
                17, 19,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_permission.text = spannable
            tv_permission.movementMethod = LinkMovementMethod.getInstance()
        }

        sv_contact.setOnClickListener {
            closeSearchView()
        }

        sv_contact.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })

        bt_cloud.setOnClickListener{
            var builder :AlertDialog.Builder=AlertDialog.Builder(context)
            builder.setTitle("Cloud Synchronization").setMessage("Load or Save?")
            builder.setNegativeButton("Save", object: DialogInterface.OnClickListener {
                override fun onClick(dialog:DialogInterface, which:Int) {

                    var result: Repo? = null
                    var retrofit = Retrofit.Builder().baseUrl("http://192.249.18.171:4000")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                    var service1 = retrofit.create(RetrofitService::class.java)
                    var call1 = service1.getPosts()
                    call1.enqueue(object: Callback<Repo> {
                        override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                            if (response.isSuccessful) {
                                result = response.body()
                                Log.d("response", "onResponse: 성공, \n" + result.toString())
                            } else {
                                Log.d("respose", "onResponse: 실패")
                            }
                        }
                        override fun onFailure(call: Call<Repo>, t: Throwable) {
                            Log.d("통신실패", "onFailure: " + t.message)
                        }
                    })

                }
            })
            builder.setPositiveButton("Load", object: DialogInterface.OnClickListener {
                override fun onClick(dialog:DialogInterface, which:Int) {
                }
            })
            builder.show()
        }

        return rootview
    }

    fun checkAndRequestPermission(): Boolean {
        if (checkSelfPermission(requireActivity(), android.Manifest.permission.READ_CONTACTS)
            == PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                PERMISSION_READ_CONTACT
            )
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        if(dirty_bit==1) {
            Thread.sleep(100)
            rv_contact.let { showContacts(it) }
        }
    }

    fun onPermissionGranted() {
        tv_permission.text = ""
        rv_contact.let { showContacts(it) }
    }

    fun showContacts(rv: RecyclerView) {
        val ContactList = arrayListOf<ContactItem>()
        val resolver: ContentResolver = requireActivity().contentResolver
        val c = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
        )

        if (c != null && c.count > 0) {
            c.moveToFirst()
            do {
                var id: Int = c.getInt(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                )
                var lookup: Int = c.getInt(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
                )
                var name: String = c.getString(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                var number = c.getString(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                var thumb: String? = c.getString(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
                )
                ContactList.add(
                    ContactItem(
                        id, lookup, name, number, thumb,
                        Random().nextInt(requireContext().resources.getIntArray(R.array.contactIconColors).size)
                    )
                )
            } while (c.moveToNext())
        }
        dirty_bit = 0

        adapter = ContactAdapter(requireContext(), ContactList)
        rv.adapter = adapter

        val lm = LinearLayoutManager(requireContext())
        rv.layoutManager = lm
        rv.setHasFixedSize(true)
    }

    fun closeSearchView(): Boolean {
        sv_contact.setQuery("", false)
        val focused = sv_contact.isIconified
        sv_contact.isIconified = !sv_contact.isIconified
        return focused
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}