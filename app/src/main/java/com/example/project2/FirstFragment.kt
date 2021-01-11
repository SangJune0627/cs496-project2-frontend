package com.example.project2

import android.app.AlertDialog
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.DialogInterface
import android.os.Bundle
import android.provider.ContactsContract
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
import com.example.project2.Contact.ContactAdapter
import com.example.project2.Contact.ContactItem
import com.facebook.Profile
import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

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
    val PERMISSION_WRITE_CONTACT: Int = 102

    private lateinit var rv_contact: RecyclerView
    private lateinit var tv_permission: TextView
    private lateinit var sv_contact: SearchView
    private lateinit var bt_cloud: FloatingActionButton
    var facebookID: String? = null
    var downloadedContacts: ContactsBluePrint? = null

    private var currentContacts = arrayListOf<ContactItem>()

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
                        if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_CONTACTS)) {
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
            if (Profile.getCurrentProfile() == null) {
                var builder : AlertDialog.Builder= AlertDialog.Builder(context)
                builder.setTitle("페이스북 계정 로그인이 필요합니다")
                builder.setPositiveButton("확인", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which:Int) {}
                })
                builder.show()
            } else {
                var builder :AlertDialog.Builder=AlertDialog.Builder(context)
                builder.setTitle("Cloud Synchronization").setMessage("Load or Save?")
                builder.setNegativeButton("Save", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog:DialogInterface, which:Int) {
                        uploadContacts()
                    }
                })
                builder.setPositiveButton("Load", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog:DialogInterface, which:Int) {
                        downloadContacts()
                    }
                })
                builder.show()
            }

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
            Thread.sleep(200)
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
        currentContacts = ContactList

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

    fun uploadContacts(){
        var facebookID = getID()
        var retrofitConnectionContactsDownload = postConnectionOfContact()
        var postdata = Contact_list(facebookID, currentContacts)
        Log.d("여기",facebookID)
        var uploadCall = retrofitConnectionContactsDownload.downloadService.post(postdata)

        uploadCall.enqueue(object: retrofit2.Callback<ContactsBluePrint> {
            override fun onResponse(
                call: retrofit2.Call<ContactsBluePrint>,
                response: Response<ContactsBluePrint>
            ) {
                if (response.isSuccessful) {
                    Log.d("DownloadContacts", "onResponse: 성공")
                } else {
                    Log.d("DownloadContacts", "onResponse: 실패")
                }
            }
            override fun onFailure(call: retrofit2.Call<ContactsBluePrint>, t: Throwable) {
                Log.d("DownloadContacts", "onFailure" + t.message)
            }
        })
    }

    fun downloadContacts() {
        var facebookID = getID()

        var retrofitConnectionContactsDownload = getConnectionOfContact()

        var downloadCall = retrofitConnectionContactsDownload.downloadService.get(facebookID!!)

        downloadCall.enqueue(object: retrofit2.Callback<ContactsBluePrint> {
            override fun onResponse(
                call: retrofit2.Call<ContactsBluePrint>,
                response: Response<ContactsBluePrint>
            ) {
                if (response.isSuccessful) {
                    downloadedContacts = response.body()
                    Log.d("DownloadContacts", "onResponse: 성공, \n" + downloadedContacts.toString())
                    Log.d("DownloadContacts", downloadedContacts!!.data.toString())
                    var contacts = downloadedContacts!!.data
                    var templist = arrayListOf<ContactItem>()
                    for (each in contacts){
                        var alreadyexist: Boolean = false
                        for (each1 in currentContacts)
                        {
                            if(each.asJsonObject["number"].toString().replace("\"","").equals(each1.number))
                            {
                                alreadyexist=true
                                break
                            }
                        }
                        if(!alreadyexist) {
                            if(each.asJsonObject["thumb"]==null) {
                                templist.add(
                                    ContactItem(
                                        each.asJsonObject["id"].toString().replace("\"", "").toInt(),
                                        each.asJsonObject["lookUp"].toString().replace("\"", "").toInt(),
                                        each.asJsonObject["name"].toString().replace("\"", ""),
                                        each.asJsonObject["number"].toString().replace("\"", ""),
                                        null,
                                        Random().nextInt(requireContext().resources.getIntArray(R.array.contactIconColors).size)
                                    )
                                )
                            }else{
                                templist.add(
                                    ContactItem(
                                        each.asJsonObject["id"].toString().replace("\"", "")
                                            .toInt(),
                                        each.asJsonObject["lookUp"].toString().replace("\"", "")
                                            .toInt(),
                                        each.asJsonObject["name"].toString().replace("\"", ""),
                                        each.asJsonObject["number"].toString().replace("\"", ""),
                                        each.asJsonObject["thumb"].toString().replace("\"", ""),
                                        Random().nextInt(requireContext().resources.getIntArray(R.array.contactIconColors).size)
                                    )
                                )
                            }
                        }
                    }
                    if(templist.size>0) {
                        if (checkSelfPermission(
                                requireActivity(),
                                android.Manifest.permission.WRITE_CONTACTS
                            )
                            == PERMISSION_GRANTED
                        ) {
                            println("승인됨")
                        } else {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.WRITE_CONTACTS),
                                PERMISSION_WRITE_CONTACT
                            )
                        }
                        addContact(templist)
                        showContacts(rv_contact)
                    }
                    Log.d("여기다!",templist.toString())

//                    val contactStructure = ContactStructure.parseJson(contacts_json)
//                    Log.d("DownloadContacts", "onResponse: 성공, \n" + contactStructure.toString())

                } else {
                    Log.d("DownloadContacts", "onResponse: 실패")
                }
            }
            override fun onFailure(call: retrofit2.Call<ContactsBluePrint>, t: Throwable) {
                Log.d("DownloadContacts", "onFailure" + t.message)
            }
        })

    }

    fun getID(): String {
        var myProfile = Profile.getCurrentProfile()
        return myProfile!!.id
    }

    fun addContact(templist:ArrayList<ContactItem>){
        for (each in templist){
            var list = ArrayList<ContentProviderOperation>()
            list.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            list.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, each.name)   //이름
                            .build()
            )
            list.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, each.number)           //전화번호
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE  , ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)   //번호타입(Type_Mobile : 모바일)
                            .build()
            )
            context!!.contentResolver.applyBatch(ContactsContract.AUTHORITY, list)  //주소록추가
            list.clear()   //리스트 초기화
        }
    }
}


class getConnectionOfContact {
    val url = "http://192.249.18.171:4000/"
    var retrofit_download: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var downloadService = retrofit_download.create(GetContacts::class.java)
}

class postConnectionOfContact {
    val url = "http://192.249.18.171:4000/"
    var retrofit_download: Retrofit = Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var downloadService = retrofit_download.create(PostContacts::class.java)
}

interface GetContacts {
    @GET("contacts")
    fun get(@Query("owner") owner: String): retrofit2.Call<ContactsBluePrint>
}

interface PostContacts {
    @POST("contacts")
    fun post(@Body contact_list: Contact_list): retrofit2.Call<ContactsBluePrint>
}

data class ContactsBluePrint(val data: JsonArray)
data class Contact_list (val owner: String, val contact_list: ArrayList<ContactItem>)