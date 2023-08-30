package com.example.pomodorotechnique

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pomodorotechnique.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 1500.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener{startStopTimer()}

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun startStopTimer() {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer(){
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        binding.btnStart.text = "Stop"
        timerStarted = true
    }

    private fun stopTimer(){
        stopService(serviceIntent)
        binding.btnStart.text = "Start"
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA,0.0)
            binding.timerPomodoro.text = getTimeStringFromDouble(time)

            if(time == 0.0)
                stopTimer()
        }
    }

    private fun getTimeStringFromDouble(time: Double) :String{
        val resultInt = time.roundToInt()
        val minutes = resultInt % 86400 %3600 / 60
        val seconds = resultInt % 86400 %3600 % 60

        return makeTimeString(minutes,seconds)
    }

    private fun makeTimeString(min:Int, sec:Int): String = String.format("%02d:%02d", min, sec)


}