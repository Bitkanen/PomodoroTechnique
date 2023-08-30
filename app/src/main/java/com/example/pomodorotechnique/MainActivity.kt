package com.example.pomodorotechnique

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.pomodorotechnique.databinding.ActivityMainBinding
import java.util.*
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
        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico_gray)
        binding.btnStart.setOnClickListener{
            startStopTimer()

        }
        binding.btnRestart.setOnClickListener{restartTimer()}

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun startStopTimer() {
        if(timerStarted){
            stopTimer()
        }
        else{
            startTimer()
            binding.btnRestart.setVisibility(View.VISIBLE)
            binding.layoutPomodoro.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
            binding.layoutTimer.setBackgroundResource(R.drawable.background_timer_red)
            binding.btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
            binding.btnRestart.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
            binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico)
        }
    }

    private fun restartTimer(){
        stopTimer()
        time = 1500.0
        binding.btnRestart.setVisibility(View.INVISIBLE)
        binding.timerPomodoro.text = "25:00"
        binding.layoutPomodoro.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.layoutTimer.setBackgroundResource(R.drawable.background_timer_gray)
        binding.btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.btnRestart.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico_gray)
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