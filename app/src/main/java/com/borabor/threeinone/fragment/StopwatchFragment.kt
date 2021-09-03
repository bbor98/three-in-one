package com.borabor.threeinone.fragment

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.borabor.threeinone.R
import com.borabor.threeinone.util.ButtonUtil.vibratePhone
import com.borabor.threeinone.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import java.util.concurrent.TimeUnit


class StopwatchFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private var timerState = TimerState.Stopped

    private var startTime = 0L
    private var timeInMillis = 0L
    private var elapsedTime = 0L
    private var updateTime = 0L

    private var handler = Handler()
    private var runnable = object : Runnable {
        override fun run() {
            timeInMillis = SystemClock.uptimeMillis() - startTime
            updateTime = elapsedTime + timeInMillis
            updateStopwatchText()
            handler.postDelayed(this, 0)
        }
    }

    private var lapList: HashMap<Int, String>? = null
    private var isLapped = false
    private var lapCounter = 0

    private var hms = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stopwatch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabStartSW.setOnClickListener {
            vibratePhone(requireContext())

            startTime = SystemClock.uptimeMillis()
            handler.postDelayed(runnable, 0)

            if (timerState == TimerState.Stopped) {
                fabStopSW.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_500))
                fabLap.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_500))
            }

            timerState = TimerState.Running
            updateButtons()
        }

        fabPauseSW.setOnClickListener {
            vibratePhone(requireContext())

            elapsedTime += timeInMillis
            handler.removeCallbacks(runnable)

            timerState = TimerState.Paused
            updateButtons()
        }

        fabStopSW.setOnClickListener {
            vibratePhone(requireContext())

            elapsedTime += timeInMillis
            handler.removeCallbacks(runnable)
            tvStopwatch.text = "00:00:00"

            startTime = 0L
            timeInMillis = 0L
            elapsedTime = 0L
            updateTime = 0L

            timerState = TimerState.Stopped
            updateButtons()
        }

        fabLap.setOnClickListener {
            vibratePhone(requireContext())

            if (!isLapped) {
                ll.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_1000))
                fabClear.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_500))
                vLine.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_1000))

                tvStopwatch.animate().translationY(-450f).duration = 200
                fabStartSW.animate().translationY(-650f).duration = 200
                fabPauseSW.animate().translationY(-650f).duration = 200
                fabStopSW.animate().translationY(-650f).duration = 200
                fabLap.animate().translationY(-650f).duration = 200
            }
            isLapped = true

            updateLapsText(lapList, false, 0)
            lapCounter++
        }

        fabClear.setOnClickListener {
            vibratePhone(requireContext())

            llLaps.removeAllViewsInLayout()
            lapCounter = 0

            ll.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_200))
            vLine.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_200))
            fabClear.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_200))
            fabClear.isEnabled = false

            tvStopwatch.animate().translationY(0f).duration = 500
            fabStartSW.animate().translationY(0f).duration = 500
            fabPauseSW.animate().translationY(0f).duration = 500
            fabStopSW.animate().translationY(0f).duration = 500
            fabLap.animate().translationY(0f).duration = 500

            lapList!!.clear()
            isLapped = false
        }
    }

    override fun onStart() {
        super.onStart()

        timerState = PrefUtil.getTimerStateSW(requireContext())
        updateTime = PrefUtil.getUpdateTime(requireContext())
        startTime = PrefUtil.getStartTimeSW(requireContext())
        elapsedTime = PrefUtil.getElapsedTime(requireContext())
        isLapped = PrefUtil.getLapState(requireContext())
        lapCounter = PrefUtil.getLapCounter(requireContext())
        lapList = PrefUtil.getLapList(requireContext())

        if (timerState == TimerState.Running) {
            handler.postDelayed(runnable, 0)
            updateButtons()
        } else if (timerState == TimerState.Paused) {
            updateStopwatchText()
            updateButtons()
        }

        if (isLapped) {
            llLaps.removeAllViewsInLayout()
            for (i in 0 until lapList!!.size) updateLapsText(lapList, true, i)

            tvStopwatch.animate().translationY(-450f).duration = 0
            fabStartSW.animate().translationY(-650f).duration = 0
            fabPauseSW.animate().translationY(-650f).duration = 0
            fabStopSW.animate().translationY(-650f).duration = 0
            fabLap.animate().translationY(-650f).duration = 0
        }
    }

    override fun onStop() {
        super.onStop()

        PrefUtil.setTimerStateSW(requireContext(), timerState.ordinal)
        PrefUtil.setUpdateTime(requireContext(), updateTime)
        PrefUtil.setStartTimeSW(requireContext(), startTime)
        PrefUtil.setElapsedTime(requireContext(), elapsedTime)
        PrefUtil.setLapState(requireContext(), isLapped)
        PrefUtil.setLapCounter(requireContext(), lapCounter)
        PrefUtil.setLapList(requireContext(), lapList)
    }

    private fun updateStopwatchText() {
        val millis = (updateTime / 10) % 100

        if (updateTime >= 3600000) {
            hms = String.format(
                "%02d:%02d:%02d:%02d",
                (TimeUnit.MILLISECONDS.toHours(updateTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(updateTime))),
                (TimeUnit.MILLISECONDS.toMinutes(updateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updateTime))),
                (TimeUnit.MILLISECONDS.toSeconds(updateTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updateTime))),
                (TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)))
            )
        } else {
            hms = String.format(
                "%02d:%02d:%02d",
                (TimeUnit.MILLISECONDS.toMinutes(updateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updateTime))),
                (TimeUnit.MILLISECONDS.toSeconds(updateTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updateTime))),
                (TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)))
            )
        }

        if (tvStopwatch != null) tvStopwatch.text = hms
    }

    private fun updateLapsText(map: HashMap<Int, String>?, isFragmentStopped: Boolean, index: Int) {
        val inflater = requireActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflatedView = inflater.inflate(R.layout.scroll_row, null)
        val lapText = inflatedView.findViewById(R.id.sr_text) as TextView

        if (isFragmentStopped) {
            lapText.text = map?.get(index)
            llLaps.addView(inflatedView)
        } else {
            val str = "${lapCounter + 1}. Lap    -    " + tvStopwatch.text
            map?.set(lapCounter, str)

            lapText.text = map?.get(lapCounter)
            llLaps.addView(inflatedView)
        }

        svLaps.fullScroll(View.FOCUS_DOWN)

        ll.visibility = View.VISIBLE
        vLine.visibility = View.VISIBLE
        fabClear.visibility = View.VISIBLE
        fabClear.isEnabled = true
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Stopped -> {
                fabStartSW.visibility = View.VISIBLE
                fabStartSW.isEnabled = true

                fabPauseSW.visibility = View.INVISIBLE
                fabPauseSW.isEnabled = false

                fabStopSW.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_500))
                fabStopSW.isEnabled = false

                fabLap.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_500))
                fabLap.isEnabled = false
            }
            TimerState.Paused -> {
                fabStartSW.visibility = View.VISIBLE
                fabStartSW.isEnabled = true

                fabPauseSW.visibility = View.INVISIBLE
                fabPauseSW.isEnabled = false

                fabStopSW.visibility = View.VISIBLE
                fabStopSW.isEnabled = true

                fabLap.visibility = View.VISIBLE
                fabLap.isEnabled = true
            }
            TimerState.Running -> {
                fabStartSW.visibility = View.INVISIBLE
                fabStartSW.isEnabled = false

                fabPauseSW.visibility = View.VISIBLE
                fabPauseSW.isEnabled = true

                fabStopSW.visibility = View.VISIBLE
                fabStopSW.isEnabled = true

                fabLap.visibility = View.VISIBLE
                fabLap.isEnabled = true
            }
        }
    }
}