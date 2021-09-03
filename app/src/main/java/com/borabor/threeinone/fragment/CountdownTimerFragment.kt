package com.borabor.threeinone.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.borabor.threeinone.R
import com.borabor.threeinone.util.ButtonUtil.vibratePhone
import com.borabor.threeinone.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_countdown_timer.*
import java.util.concurrent.TimeUnit


class CountdownTimerFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private var timer: CountDownTimer? = null
    private var timerState = TimerState.Stopped

    private var remainingTime = 0L
    private var startTime = 0L
    private var endTime = 0L


    private var sbProgressList = hashMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countdown_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabStartCD.setOnClickListener {
            vibratePhone(requireContext())

            if (timerState == TimerState.Stopped) {
                remainingTime = sbProgressList["hour"]!! * 3600000L +
                        sbProgressList["minute"]!! * 60000L +
                        sbProgressList["second"]!! * 1000L +
                        1000L
                startTime = remainingTime
                cl.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
            }

            startTimer()
        }

        fabPauseCD.setOnClickListener {
            vibratePhone(requireContext())
            pauseTimer()
        }

        fabStopCD.setOnClickListener {
            vibratePhone(requireContext())
            stopTimer()
            cl.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down))
        }

        sbHour.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvHour.text = "$progress"
                tvCountdownTimer.text = String.format("%02d:%02d:%02d", progress, sbProgressList["minute"], sbProgressList["second"])
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sbProgressList["hour"] = seekBar!!.progress
            }
        })

        sbMinute.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMinute.text = "$progress"
                tvCountdownTimer.text = String.format("%02d:%02d:%02d", sbProgressList["hour"], progress, sbProgressList["second"])
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sbProgressList["minute"] = seekBar!!.progress
            }
        })

        sbSecond.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSecond.text = "$progress"
                tvCountdownTimer.text = String.format("%02d:%02d:%02d", sbProgressList["hour"], sbProgressList["minute"], progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sbProgressList["second"] = seekBar!!.progress
            }
        })
    }

    override fun onStart() {
        super.onStart()

        remainingTime = PrefUtil.getRemainingTime(requireContext())
        startTime = PrefUtil.getStartTimeCD(requireContext())
        timerState = PrefUtil.getTimerStateCD(requireContext())
        sbProgressList = PrefUtil.getSbProgressList(requireContext())

        sbHour.progress = sbProgressList["hour"]!!
        sbMinute.progress = sbProgressList["minute"]!!
        sbSecond.progress = sbProgressList["second"]!!

        tvHour.text = sbHour.progress.toString()
        tvMinute.text = sbMinute.progress.toString()
        tvSecond.text = sbSecond.progress.toString()

        if (timerState == TimerState.Running) {
            endTime = PrefUtil.getEndTime(requireContext())
            remainingTime = endTime - System.currentTimeMillis()

            if (remainingTime <= 0) {
                remainingTime = 0
                timerState = TimerState.Stopped
                updateButtons()
            } else startTimer()
        } else if (timerState == TimerState.Paused) pauseTimer()

        updateCountDownText()
    }

    override fun onStop() {
        super.onStop()

        PrefUtil.setRemainingTime(requireContext(), remainingTime)
        PrefUtil.setStartTimeCD(requireContext(), startTime)
        PrefUtil.setEndTime(requireContext(), endTime)
        PrefUtil.setTimerStateCD(requireContext(), timerState)
        PrefUtil.setSbProgressList(requireContext(), sbProgressList)

        timer?.cancel()
    }

    private fun startTimer() {
        endTime = System.currentTimeMillis() + remainingTime
        timer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                stopTimer()
                tvCountdownTimer.text = "00:00:00"
            }
        }.start()

        timerState = TimerState.Running
        updateButtons()
    }

    private fun pauseTimer() {
        timer?.cancel()
        timerState = TimerState.Paused
        updateButtons()
    }

    private fun stopTimer() {
        timer?.cancel()
        remainingTime = startTime - 1000

        timerState = TimerState.Stopped
        updateCountDownText()
        updateButtons()
    }

    fun updateCountDownText() {
        val hms = String.format(
            "%02d:%02d:%02d",
            (TimeUnit.MILLISECONDS.toHours(remainingTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainingTime))),
            (TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime))),
            (TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)))
        )

        tvCountdownTimer.text = hms
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Stopped -> {
                fabStartCD.visibility = View.VISIBLE
                fabStartCD.isEnabled = true
                fabStartCD.animate().translationY(0f).translationX(0f).duration = 500

                fabPauseCD.animate().translationY(0f).translationX(0f).duration = 500
                val handlerPause = Handler()
                handlerPause.postDelayed({
                    fabPauseCD.visibility = View.INVISIBLE
                    fabPauseCD.isEnabled = false
                }, 500)

                fabStopCD.animate().translationY(0f).translationX(0f).duration = 500
                val handlerStop = Handler()
                handlerStop.postDelayed({
                    fabStopCD.visibility = View.INVISIBLE
                    fabStopCD.isEnabled = false
                }, 500)

                cl.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down))
                cl.visibility = View.VISIBLE

                sbHour.isEnabled = true
                sbMinute.isEnabled = true
                sbSecond.isEnabled = true
            }
            TimerState.Paused -> {
                fabStartCD.visibility = View.VISIBLE
                fabStartCD.isEnabled = true

                fabPauseCD.visibility = View.INVISIBLE
                fabPauseCD.isEnabled = false

                fabStopCD.visibility = View.VISIBLE
                fabStopCD.isEnabled = true

                fabStartCD.animate().translationY(-500f).translationX(165f).duration = 0
                fabPauseCD.animate().translationY(-500f).translationX(165f).duration = 0
                fabStopCD.animate().translationY(-500f).translationX(-165f).duration = 0

                cl.visibility = View.INVISIBLE

                sbHour.isEnabled = false
                sbMinute.isEnabled = false
                sbSecond.isEnabled = false
            }
            TimerState.Running -> {
                fabStartCD.visibility = View.INVISIBLE
                fabStartCD.isEnabled = false
                fabStartCD.animate().translationY(-500f).translationX(165f).duration = 0

                fabPauseCD.visibility = View.VISIBLE
                fabPauseCD.isEnabled = true
                fabPauseCD.animate().translationY(-500f).translationX(165f).duration = 500

                fabStopCD.visibility = View.VISIBLE
                fabStopCD.isEnabled = true
                fabStopCD.animate().translationY(-500f).translationX(-165f).duration = 500

                cl.visibility = View.INVISIBLE

                sbHour.isEnabled = false
                sbMinute.isEnabled = false
                sbSecond.isEnabled = false
            }
        }
    }
}