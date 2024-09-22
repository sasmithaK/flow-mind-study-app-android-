package com.example.flowmind.ui.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.flowmind.R

class TimerFragment : Fragment() {

    private var sessionDurationInMinutes: Int = 0 // Default session time
    private var sessionCount: Int = 1 // Default session count
    private var currentSession: Int = 1 // Track current session
    private var timeLeftInMillis: Long = 0 // Remaining time in current session
    private var isTimerRunning = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var textViewSessionTime: TextView
    private lateinit var textViewSessionCount: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var buttonReset: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)

        textViewSessionTime = view.findViewById(R.id.textViewSessionTime)
        textViewSessionCount = view.findViewById(R.id.textViewSessionCount)
        textViewTimer = view.findViewById(R.id.textViewTimer)
        buttonReset = view.findViewById(R.id.buttonReset)

        view.findViewById<Button>(R.id.buttonDecreaseTime).setOnClickListener {
            if (sessionDurationInMinutes > 1) {
                sessionDurationInMinutes--
                updateSessionTimeDisplay()
            }
        }

        view.findViewById<Button>(R.id.buttonIncreaseTime).setOnClickListener {
            sessionDurationInMinutes++
            updateSessionTimeDisplay()
        }

        view.findViewById<Button>(R.id.buttonDecreaseCount).setOnClickListener {
            if (sessionCount > 1) {
                sessionCount--
                updateSessionCountDisplay()
            }
        }

        view.findViewById<Button>(R.id.buttonIncreaseCount).setOnClickListener {
            sessionCount++
            updateSessionCountDisplay()
        }

        view.findViewById<Button>(R.id.buttonStartTimer).setOnClickListener {
            if (!isTimerRunning) {
                startPomodoroTimer()
            }
        }

        buttonReset.setOnClickListener {
            resetTimer() // Reset button functionality
        }

        // Load previous timer state
        loadTimerState()

        updateSessionTimeDisplay()
        updateSessionCountDisplay()

        return view
    }

    private fun updateSessionTimeDisplay() {
        textViewSessionTime.text = "$sessionDurationInMinutes min"
    }

    private fun updateSessionCountDisplay() {
        textViewSessionCount.text = "$sessionCount"
    }

    private fun startPomodoroTimer() {
        // Reset session tracking and time for the first session
        currentSession = 1
        timeLeftInMillis = sessionDurationInMinutes * 60 * 1000L // time for one session
        val totalPomodoroTime = sessionCount * sessionDurationInMinutes * 60 * 1000L // total time for all sessions
        textViewTimer.text = formatTime(totalPomodoroTime)
        startSessionTimer()
    }

    private fun startSessionTimer() {
        textViewTimer.text = formatTime(timeLeftInMillis)

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                textViewTimer.text = formatTime(millisUntilFinished)
                saveTimerState() // Save current state
            }

            override fun onFinish() {
                notifyUser("Session $currentSession complete!")

                // Move to next session
                if (currentSession < sessionCount) {
                    currentSession++
                    timeLeftInMillis = sessionDurationInMinutes * 60 * 1000L
                    startSessionTimer() // Start the next session
                } else {
                    isTimerRunning = false
                    textViewTimer.text = "0:00"
                    clearTimerState() // Clear timer state when done
                }
            }
        }.start()

        isTimerRunning = true
        saveTimerState() // Save running timer state
    }

    private fun notifyUser(message: String) {
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "pomodoro_channel",
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Pomodoro Timer"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), "pomodoro_channel")
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Pomodoro Timer")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(currentSession, notificationBuilder.build())
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun saveTimerState() {
        sharedPreferences.edit().apply {
            putInt("sessionDurationInMinutes", sessionDurationInMinutes)
            putInt("sessionCount", sessionCount)
            putInt("currentSession", currentSession)
            putLong("timeLeftInMillis", timeLeftInMillis)
            putBoolean("isTimerRunning", isTimerRunning)
            apply()
        }
    }

    private fun loadTimerState() {
        sessionDurationInMinutes = sharedPreferences.getInt("sessionDurationInMinutes", 0)
        sessionCount = sharedPreferences.getInt("sessionCount", 1)
        currentSession = sharedPreferences.getInt("currentSession", 1)
        timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", 0)
        isTimerRunning = sharedPreferences.getBoolean("isTimerRunning", false)

        if (isTimerRunning) {
            startSessionTimer() // Resume timer if it was running
        }
    }

    private fun resetTimer() {
        countDownTimer?.cancel() // Stop the timer
        isTimerRunning = false
        sessionDurationInMinutes = 0
        sessionCount = 1
        timeLeftInMillis = 0
        currentSession = 1

        // Update UI
        textViewTimer.text = formatTime(timeLeftInMillis)
        updateSessionTimeDisplay()
        updateSessionCountDisplay()

        clearTimerState() // Clear the saved state
    }

    private fun clearTimerState() {
        sharedPreferences.edit().clear().apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
