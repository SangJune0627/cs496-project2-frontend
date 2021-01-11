package com.example.project2.Util

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.ArrayList

class PermissionSupport     // 생성자에서 Activity와 Context를 파라미터로 받았습니다.
    (private val activity: Activity, private val context: Context) {
    // 요청할 권한을 배열로 저장해주었습니다.
    private val permissions = arrayOf(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CONTACTS
    )
    private var permissionList: MutableList<*>? = null

    // 이 부분은 권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int 형입니다.
    // 본인에 맞게 숫자를 지정하시면 될 것 같습니다.
    private val MULTIPLE_PERMISSIONS = 1023

    // 허용 받아야할 권한이 남았는지 체크
    fun checkPermission(): Boolean {
        var result: Int
        permissionList = ArrayList<Any>()

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for (pm in permissions) {
            result = ContextCompat.checkSelfPermission(context, pm)
            if (result != PackageManager.PERMISSION_GRANTED) {
                (permissionList as ArrayList<Any>).add(pm)
            }
        }
        return if (!(permissionList as ArrayList<Any>).isEmpty()) {
            false
        } else true
    }

    // 권한 허용 요청
    fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity,
            permissionList!! as Array<String>,
            MULTIPLE_PERMISSIONS
        )
    }

    // 권한 요청에 대한 결과 처리
    fun permissionResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ): Boolean {

        // 우선 requestCode가 아까 위에 final로 선언하였던 숫자와 맞는지, 결과값의 길이가 0보다는 큰지 먼저 체크했습니다.
        if (requestCode == MULTIPLE_PERMISSIONS && grantResults.size > 0) {
            for (i in grantResults.indices) {
                //grantResults 가 0이면 사용자가 허용한 것이고 / -1이면 거부한 것입니다.
                // -1이 있는지 체크하여 하나라도 -1이 나온다면 false를 리턴해주었습니다.
                if (grantResults[i] == -1) {
                    return false
                }
            }
        }
        return true
    }
}