package ifgoiano.urt.cronometro

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var timerInput: EditText
    private var countDownTimer: CountDownTimer? = null
    private var running = false
    private var endTime: Long = 0L
    private var tempoRestante: Long = 0L
    private var estavaEmExecucao = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temporizador)

        timerInput = findViewById(R.id.etTimer)

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean("running", false)
            endTime = savedInstanceState.getLong("endTime", 0L)

            tempoRestante = endTime - System.currentTimeMillis()

            if (tempoRestante < 0) {
                tempoRestante = 0
                running = false
            }

            if (running) {
                startTimer()
            } else {
                updateTimerUI()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("running", running)
        outState.putLong("endTime", endTime)
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
                tempoRestante = ((minutes * 60) + seconds) * 1000L
            } else {
                tempoRestante = 60000L
            }

            endTime = System.currentTimeMillis() + tempoRestante
            startTimer()
            running = true
        }
    }

    fun onClickTimerStop(view: View) {
        countDownTimer?.cancel()
        running = false
    }

    private fun startTimer() {
        countDownTimer?.cancel() // evita duplicar timers

        countDownTimer = object : CountDownTimer(tempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempoRestante = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                running = false
                timerInput.setText("00:00")
            }
        }.start()
    }

    private fun updateTimerUI() {
        val minutes = (tempoRestante / 1000) / 60
        val seconds = (tempoRestante / 1000) % 60
        val time = String.format("%02d:%02d", minutes, seconds)
        timerInput.setText(time)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}