package com.example.project2

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.project2.Gallery.GalleryImage
import com.example.project2.Gallery.GalleryStructure
import com.example.project2.Util.SwipeLockableViewPager
import com.facebook.FacebookSdk
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    val PERMISSION_READ_CONTACT: Int = 101

    private var firstFragment: FirstFragment? = null
    private var secondFragment: SecondFragment? = null
    private var thirdFragment: ThirdFragment? = null
    private var fourthFragment: FourthFragment? = null

    private var tabs_main: TabLayout? = null

    var galleryImages: ArrayList<GalleryImage> = ArrayList()
    var galleryStructure: GalleryStructure = GalleryStructure()

    var facebookID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        imgs.forEachIndexed {index, img_fd ->
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_READ_CONTACT+65636 -> { // 101+65536 (why??)
                if (grantResults.size > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                    firstFragment?.onPermissionGranted()
                }
            }
        }
    }

    override fun onBackPressed() {
        val index = tabs_main?.selectedTabPosition
        when (index) {
            0 -> if (firstFragment?.closeSearchView() == true) finish()
            1 -> if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack() else finish()
            else -> finish()
        }
    }
    // #begin: src by 박해철
    /**
     * This code is used for gesture detection in second tab (SecondFragmentGallery.kt)
     */
    private val OnTouchListener= ArrayList<MyOnTouchListener>()
    interface MyOnTouchListener{
        fun OnTouch(ev: MotionEvent?)
    }
    fun registerMyOnTouchListener(listener: MyOnTouchListener){
        OnTouchListener.add(listener)
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        for (listener in OnTouchListener) listener.OnTouch(ev)
        return super.dispatchTouchEvent(ev)
    }
    // #end: src by 박해철

//    ____________________________________Server에 갤러리 동기화___________________________________________

    fun uploadGallery() {
//TODO: 아이디를 가져와야 함.
    }

    fun downloadGallery() {
//TODO: 아이디를 가져와야 함.
    }

//    ____________________________ 갤러리 동기화 클래스 ______________________________
    inner class DownloadGalleryAsync: AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(vararg params: Any?): Any? {
            var retrofit_download = Retrofit.Builder().baseUrl("http://192.249.18.171:4000")
                .addConverterFactory(GsonConverterFactory.create()).build()

            var service_download = retrofit_download.create()
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
        }
    }

    inner class UploadGalleryAsync: AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(vararg params: Any?): Any? {
            TODO("Not yet implemented")
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
        }
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

interface DownloadService {
    @GET("/gallery")
    fun get(): Call
}