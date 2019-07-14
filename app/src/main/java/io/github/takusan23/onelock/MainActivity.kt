package io.github.takusan23.onelock

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val ADMIN_INTENT = 1000
    lateinit var devicePolicyManager: DevicePolicyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        getPermission()

    }

    /*管理者権限をもらう*/
    fun getPermission() {
        val componentName = ComponentName(this, DeviceAdmin::class.java)
        if (!devicePolicyManager.isAdminActive(componentName)) {
            //無い時
            //権限画面に飛ばす
            //押して認証画面へ
            permission_button.setOnClickListener {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "端末をロック画面へ移行させるために使います。")
                startActivityForResult(intent, ADMIN_INTENT)
            }
        } else {
            //あるのでスリープへ
            devicePolicyManager.lockNow()
            //Activityも閉じる
            finishAndRemoveTask()
        }
    }

    /*権限もらえたかチェック*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "権限が付与されました。", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "権限が付与されませんでした。", Toast.LENGTH_SHORT).show()
            }
        }
    }

}