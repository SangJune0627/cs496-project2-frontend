package com.example.project2

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.project2.Gallery.GalleryImage
import com.example.project2.Gallery.GalleryStructure
import com.example.project2.Util.*
import com.facebook.FacebookSdk
import com.facebook.Profile
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val PERMISSION_READ_CONTACT: Int = 101
    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS)

    private var firstFragment: FirstFragment? = null
    private var secondFragment: SecondFragment? = null
    private var thirdFragment: ThirdFragment? = null
    private var fourthFragment: FourthFragment? = null

    private var tabs_main: TabLayout? = null

    var galleryImages: ArrayList<GalleryImage> = ArrayList()
    var galleryStructure: GalleryStructure = GalleryStructure()

    var facebookID: String? = null
    var downloadedGalleryResult: GalleryBluePrint? = null
    var uploadedGalleryResult: GalleryBluePrint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()


        // ______________처음 galleryImages랑 galleryStructure 초기화_____________
        val imgs = arrayListOf<Int>(
            R.raw.pic_gif,
            R.raw.haring_01,
            R.raw.haring_02,
            R.raw.haring_03,
            R.raw.haring_04,
            R.raw.haring_05,
            R.raw.haring_06,
            R.raw.haring_07,
            R.raw.haring_08,
            R.raw.haring_09,
            R.raw.haring_10,
            R.raw.haring_11,
            R.raw.haring_12,
            R.raw.haring_13,
            R.raw.haring_14,
            R.raw.haring_15,
            R.raw.haring_16,
            R.raw.haring_17,
            R.raw.haring_18,
            R.raw.haring_19,
            R.raw.haring_20,
            R.raw.haring_21,
            R.raw.haring_22,
            R.raw.haring_23,
            R.raw.haring_24
        )
        galleryStructure.dirName = "root"

        imgs.forEachIndexed { index, img_fd ->
            galleryImages.add(GalleryImage(type = 0, fd = img_fd))
            var childStructure = GalleryStructure()
            childStructure.type = 0
            childStructure.imgAddr = index
            galleryStructure.children.add(childStructure)
        }

        setContentView(R.layout.activity_main)
        FacebookSdk.sdkInitialize(applicationContext)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        val viewpager_main = findViewById<SwipeLockableViewPager>(R.id.viewpager_main)
        tabs_main = findViewById(R.id.tabs_main)
        viewpager_main.adapter = fragmentAdapter
        viewpager_main.setSwipePagingEnabled(false)

        firstFragment = fragmentAdapter.firstFragment
        secondFragment = fragmentAdapter.secondFragment
        thirdFragment = fragmentAdapter.thirdFragment
        fourthFragment = fragmentAdapter.fourthFragment

        secondFragment!!.galleryStructure = galleryStructure
        secondFragment!!.galleryImages = galleryImages


        tabs_main?.setupWithViewPager(viewpager_main)

        val tabIndex = intent.extras?.getInt("tabIndex", 0)
        if (tabIndex != null) {
            tabs_main?.getTabAt(tabIndex)?.select()
        }
    }

    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    //권한 요청 결과 함수
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")

                        }
                    }
                }
            }
        }
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PERMISSION_READ_CONTACT + 65636 -> { // 101+65536 (why??)
//                if (grantResults.size > 0 && grantResults[0] == PERMISSION_GRANTED) {
//                    Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
//                    firstFragment?.onPermissionGranted()
//                }
//            }
//        }
//    }

    override fun onBackPressed() {
        val index = tabs_main?.selectedTabPosition
        when (index) {
            0 -> if (firstFragment?.closeSearchView() == true) finish()
            1 -> if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack() else finish()
            2 -> if (supportFragmentManager.backStackEntryCount > 0) {
                if (thirdFragment!!.thirdFragmentGame.waitForNextMove) {
                    Toast.makeText(this, "상대방의 턴에는 도망칠 수 없다!", Toast.LENGTH_SHORT).show()
                } else {
                    var builder : AlertDialog.Builder= AlertDialog.Builder(this)
                    builder.setTitle("도망치겠습니까?")
                    builder.setPositiveButton("도망친다", object: DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which:Int) {

                            //Todo 위에거를 surrender function으로 바꿔야 한다.
                            thirdFragment!!.thirdFragmentGame.surrender()
                        }
                    })
                    builder.setNegativeButton("버틴다", object: DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which:Int) {}
                    })
                    builder.show()
                }



            } else finish()
            else -> finish()
        }
    }
    // #begin: src by 박해철
    /**
     * This code is used for gesture detection in second tab (SecondFragmentGallery.kt)
     */
    private val OnTouchListener = ArrayList<MyOnTouchListener>()

    interface MyOnTouchListener {
        fun OnTouch(ev: MotionEvent?)
    }

    fun registerMyOnTouchListener(listener: MyOnTouchListener) {
        OnTouchListener.add(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        for (listener in OnTouchListener) listener.OnTouch(ev)
        return super.dispatchTouchEvent(ev)
    }
    // #end: src by 박해철

//    ____________________________________Server에 갤러리 동기화___________________________________________

    fun uploadGallery() {
        facebookID = Profile.getCurrentProfile().id

        var retrofitGalleryUpload = RetrofitGalleryUpload()
        var uploadCall = retrofitGalleryUpload.uploadService.post(
            Images_and_Structure(facebookID!!, galleryImages, galleryStructure))
        println(galleryImages)
        println(galleryStructure.children)

        uploadCall.enqueue(object: Callback<GalleryBluePrint> {
            override fun onResponse(
                call: Call<GalleryBluePrint>,
                response: Response<GalleryBluePrint>
            ) {
                if (response.isSuccessful) {
                    uploadedGalleryResult = response.body()
                    Log.d("UploadGallery", "onResponse: 성공")
                } else {
                    Log.d("UploadGallery", "onResponse: 실패")
                }
            }

            override fun onFailure(call: Call<GalleryBluePrint>, t: Throwable) {
                Log.d("UploadGallery", "onFailure" + t.message)
            }
        })
    }

    fun downloadGallery() {
        facebookID = Profile.getCurrentProfile().id

        var retrofitGalleryDownload = RetrofitGalleryDownload()
        var downloadCall = retrofitGalleryDownload.downloadService.get(facebookID!!)

        downloadCall.enqueue(object: Callback<GalleryBluePrint> {
            override fun onResponse(
                call: Call<GalleryBluePrint>,
                response: Response<GalleryBluePrint>
            ) {
                if (response.isSuccessful) {
                    downloadedGalleryResult = response.body()
                    val structure_Json = (downloadedGalleryResult!!.data["structure"] as JsonObject)["children"] as JsonArray
                    val images_Json = downloadedGalleryResult!!.data["image_list"] as JsonArray

                    galleryStructure = GalleryStructure.parseJson(structure_Json)
                    galleryImages = GalleryImage.parseJson(images_Json)

                    secondFragment!!.galleryStructure = galleryStructure
                    secondFragment!!.galleryImages = galleryImages

                    secondFragment!!.refresh_Gallery()

                    Log.d("DownloadGallery", "onResponse: 성공")
                } else {
                    Log.d("DownloadGallery", "onResponse: 실패")
                }
            }

            override fun onFailure(call: Call<GalleryBluePrint>, t: Throwable) {
                Log.d("DownloadGallery", "onFailure" + t.message)
            }
        })

    }
}

class MyPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    var firstFragment: FirstFragment = FirstFragment()
    var secondFragment: SecondFragment = SecondFragment()
    var thirdFragment: ThirdFragment = ThirdFragment()
    var fourthFragment: FourthFragment = FourthFragment()

    override fun getItem(poisition: Int): Fragment {
        return when (poisition) {
            0 -> {firstFragment}
            1 -> {secondFragment}
            2 -> {thirdFragment}
            else -> {fourthFragment}
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "연락처"
            1 -> "갤러리"
            2 -> "게임"
            else-> {return "계정"}
        }
    }
}