package ifgoiano.urt.cronometro

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TimerActivity : AppCompatActivity() {

    private lateinit var timerInput: EditText
    private var countDownTimer: CountDownTimer? = null
    private var running = false
    private var timeLeftInMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temporizador)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        timerInput = findViewById(R.id.etTimer)
    }

    fun onClickGoToCrono(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onClickTimerStart(view: View) {
        if (!running) {
            val inputText = timerInput.text.toString()
            val parts = inputText.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                timeLeftInMillis = ((minutes * 60) + seconds) * 1000L
            } else {
                timeLeftInMillis = 60000L // fallback: 1 minuto
            }

            startTimer()
            running = true
        }
    }

    fun onClickTimerStop(view: View) {
        countDownTimer?.cancel()
        running = false
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                running = false
                timerInput.setText("00:00")
            }
        }.start()
    }

    private fun updateTimerUI() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val time = String.format("%02d:%02d", minutes, seconds)
        timerInput.setText(time)
    }
}
