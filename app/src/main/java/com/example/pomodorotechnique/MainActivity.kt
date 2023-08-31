package com.example.pomodorotechnique

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Vibrator
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.pomodorotechnique.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    //Pomodoro Time: 1500, Break Time: 600

    //Variables
    private lateinit var binding: ActivityMainBinding   //Declared Binding
    private var timerStarted = false    //Bool of the timer if it started
    private var timerBreak = false      //Bool of the timer if it is a Break
    private lateinit var serviceIntent: Intent  //Declared Intent Service
    private var time = 1500.0   //Time Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)   //Initializing Binging Variable
        setContentView(binding.root)

        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico_gray)   //Set the Image of the logo
        binding.btnStart.setOnClickListener { startStopTimer()}     //Starts the Timer
        binding.btnRestart.setOnClickListener { restartTimer() }    //Restart the Timer

        serviceIntent = Intent(applicationContext, TimerService::class.java)    //Initializing Intent Service Variable
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))  //Get actualizations of TimerService
    }

    //Function to Stop and Start the Timer
    private fun startStopTimer() {

        if (timerStarted) stopTimer()
        else if (timerStarted == false && timerBreak) breakTimer()
        else startTimer()
    }

    //Function to Restart the Timer
    private fun restartTimer() {
        stopService(serviceIntent)  //Stops the Service (TimerService)
        timerStarted = false
        time = 1500.0   //Set the Timer to Default

        //region Change of Visual Objects When Restart the Timer
        binding.btnStart.text = "Start"
        binding.txtMessage.text = "Let's started!"
        binding.timerPomodoro.text = "25:00"
        binding.btnRestart.setVisibility(View.INVISIBLE)
        binding.layoutPomodoro.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.layoutTimer.setBackgroundResource(R.drawable.background_timer_gray)
        binding.btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.btnRestart.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_Primary))
        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico_gray)
        //endregion
    }

    //Function to Start the Timer
    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)   //Set the Intent to the Service (TimerService)
        startService(serviceIntent)     //Starts the Service (TimerService)
        timerStarted = true     //Change the Status Timer
        timerBreak = false   //Change the Status Break

        //region Change of Visual Objects When the Timer Starts
        binding.btnStart.text = "Stop"
        binding.txtMessage.text = "We are Working"
        binding.btnRestart.setVisibility(View.VISIBLE)
        binding.layoutPomodoro.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
        binding.layoutTimer.setBackgroundResource(R.drawable.background_timer_red)
        binding.btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
        binding.btnRestart.setBackgroundColor(ContextCompat.getColor(this, R.color.red_Primary))
        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico)
        //endregion
    }

    //Function to Stop the Timer
    private fun stopTimer() {
        stopService(serviceIntent) //Stops the Service (TimerService)
        timerStarted = false
        //region Change of Visual Objects When the Timer Stops
        binding.txtMessage.text = "Timer Stopped!"
        binding.btnStart.text = "Start"
        //endregion
    }

    //Function to Start the Break Timer
    private fun breakTimer(){
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)//Set the Intent to the Service (TimerService)
        startService(serviceIntent)//Starts the Service (TimerService)
        timerStarted = true
        timerBreak = true

        //region Change of Visual Objects When the Timer Stops
        binding.btnStart.text = "stop"
        binding.txtMessage.text = "We are on a Break"
        binding.layoutPomodoro.setBackgroundColor(ContextCompat.getColor(this, R.color.green_Primary))
        binding.layoutTimer.setBackgroundResource(R.drawable.background_timer_green)
        binding.btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.green_Primary))
        binding.btnRestart.setBackgroundColor(ContextCompat.getColor(this, R.color.green_Primary))
        binding.imageview.setBackgroundResource(R.drawable.pomodoro_ico_green)
        //endregion
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timerPomodoro.text = getTimeStringFromDouble(time)

            if (time == 0.0 && timerBreak == false){
                vibrate()
                stopService(serviceIntent)  //Stops the Service (TimerService)
                time = 600.0    //Set the timer to a Break
                breakTimer()
            }
            if(time == 0.0 && timerBreak == true){
                vibrate()
                stopService(serviceIntent)  //Stops the Service (TimerService)
                time = 1500.0      //Set the Timer to Default
                startTimer()
            }
        }
    }

    //Function of Vibrator
    private fun vibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
    }

    //Function to Set the String of the Time
    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(minutes, seconds)
    }

    //Function to get the String of the Time
    private fun makeTimeString(min: Int, sec: Int): String = String.format("%02d:%02d", min, sec)


}