package com.example.a.lockquizekotlin

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager



class UnlockCaptureService : Service() {
    val TAG: String = "UnlockCaptureService"
    var mReceiver:UnlockReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        initReceiver()
        registerRestartAlarm(true)

        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            if (intent.action == null && mReceiver == null){
                initReceiver()
            }
        }

        return Service.START_REDELIVER_INTENT
    }

    private fun initReceiver(){
        mReceiver = UnlockReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        clearReceiver()
    }

    private fun clearReceiver(){
        mReceiver?.let { unregisterReceiver(mReceiver) }
        registerRestartAlarm(false)
    }

    private fun registerRestartAlarm(isOn: Boolean){
        val intent = Intent(this@UnlockCaptureService, RestartReceiver::class.java)
        intent.action = RestartReceiver.ACTION_RESTART_SERVICE
        val sender = PendingIntent.getBroadcast(applicationContext, 0, intent, 0)

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        am?.let {
            when(isOn){
                true -> {
                    val intervalTime = 1000000L
                    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, intervalTime, sender)
                }
                false -> {
                    am.cancel(sender)
                }
            }
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "channel"
        val channelName = "Channel Name"

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(
                    channelId, channelName, importance)

            notifManager.createNotificationChannel(mChannel)

        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val requestID = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(applicationContext, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentTitle("LockQuize") // required
                .setContentText("정답을 맞추지못하면 영원히 빠져나올수 없지롱")  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제

                .setSound(RingtoneManager

                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                .setSmallIcon(android.R.drawable.btn_star)
//                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.msg_icon))
//                .setBadgeIconType(R.drawable.msg_icon)

                .setContentIntent(pendingIntent)

        return builder.build()
    }
}
