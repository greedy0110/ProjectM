package com.example.a.lockquizekotlin

import android.app.AlertDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import kotlinx.android.synthetic.main.activity_force_lock_setting.*
import android.content.DialogInterface
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils


class ForceLockSettingActivity : AppCompatActivity() {

    val TAG = "ForceLockSetting"
    private val mForceLockList = arrayOf("1", "3", "5", "10")
    private var mSlideForcePeriod = SettingsContract.Schema.DEFAULT_SLIDE_FORCE_PERIOD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_force_lock_setting)

        select_force_period_button.setOnClickListener {
            showSelectTimeDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_force_lock_setting_layout)

        val entry = SettingsContract.getSettingsEntry(applicationContext)
        mSlideForcePeriod = entry.slideForcePeriod
        updateUI()
    }

    private fun showSelectTimeDialog() {
        createForceLockDialog().show()
    }

    private fun createForceLockDialog(): Dialog{
        val builder = AlertDialog.Builder(ForceLockSettingActivity@this)
        builder.setTitle(R.string.forceLockDialogTitle)
                .setItems(mForceLockList, DialogInterface.OnClickListener {dialog, which ->
                    Log.d(TAG, "${mForceLockList[which]}, 선택됌")
                    val time = mForceLockList[which].toInt() * 1000
                    val entry = SettingsContract.getSettingsEntry(applicationContext)
                    if (entry.slideForcePeriod == time) return@OnClickListener
                    entry.slideForcePeriod = time
                    mSlideForcePeriod = time
                    SettingsContract.setSettingsEntry(applicationContext, entry)
                    if (entry.slideOnOff == "o") {
                        AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext, entry.slideForcePeriod)
                    }
                    updateUI()
                })
        return builder.create()
    }

    private fun updateUI() {
        select_force_period_button.text = "${mSlideForcePeriod/1000.0} 초"
    }
}
