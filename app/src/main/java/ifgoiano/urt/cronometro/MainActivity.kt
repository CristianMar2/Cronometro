package ifgoiano.urt.cronometro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var seconds = 0
    private var running = false
    private lateinit var job: Job

    private var estavaEmExecucao = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
        }
        runTimer()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("seconds", seconds)
        savedInstanceState.putBoolean("running", running)
    }

    fun onClickStart(view: View){
        running = true
    }

    fun onClickStop(view: View){
        running = false
    }

    fun onClickReset(view: View){
        running = false
        seconds = 0
        updateTimerUI()
    }

    fun onClickTimer(view: View) {
        val intent = Intent(this, TimerActivity::class.java)
        startActivity(intent)
    }

    private fun updateTimerUI() {
        val timeView = findViewById<TextView>(R.id.tvCrono)
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        val time = String.format("%02d:%02d:%02d", hours, minutes, secs)
        timeView.text = time
    }
    fun runTimer(){
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if(running) {
                    // atualiza a UI
                    updateTimerUI()
                    seconds++
                    // Aguarda 1 segundo antes de atualizar o cronômetro
                    delay(1000L)
                } else {
                    // Se o cronômetro estiver pausado, aguarda brevemente antes de checar novamente
                    delay(100L)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        estavaEmExecucao = running
        running = false
    }

    override fun onPause() {
        super.onPause()
        estavaEmExecucao = running
        running = false
    }

    override fun onResume() {
        super.onResume()
        if (estavaEmExecucao) {
            running = true
        }
    }

    override fun onStart() {
        super.onStart()
        if (estavaEmExecucao) {
            running = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}