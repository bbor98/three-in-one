package com.borabor.threeinone.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.borabor.threeinone.R
import com.borabor.threeinone.util.ButtonUtil.vibratePhone
import com.borabor.threeinone.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_counter.*


class CounterFragment : Fragment() {

    private var count = 0
    private var interval: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }

        tvInterval.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    tvInterval.clearFocus()
                    tvInterval.isCursorVisible = false
                    view.hideKeyboard()

                    interval = tvInterval.text.toString().toIntOrNull()

                    if (interval == null) {
                        interval = 1
                        tvInterval.setText("1")
                    }
                    return true
                }
                return false
            }
        })

        fabIncrement.setOnClickListener {
            vibratePhone(requireContext())
            count += interval!!
            tvCounter.text = "$count"
        }

        fabDecrement.setOnClickListener {
            vibratePhone(requireContext())
            count -= interval!!
            tvCounter.text = "$count"
        }

        fabReset.setOnClickListener {
            vibratePhone(requireContext())
            count = 0
            tvCounter.text = "0"
        }

        btIncrement.setOnClickListener {
            interval = interval?.plus(1)
            tvInterval.setText(interval.toString())
        }

        btDecrement.setOnClickListener {
            interval = interval?.minus(1)
            tvInterval.setText(interval.toString())
        }
    }

    override fun onStart() {
        super.onStart()

        count = PrefUtil.getCount(requireContext())
        interval = PrefUtil.getInterval(requireContext())

        tvCounter.text = "$count"
        tvInterval.setText(interval.toString())
    }

    override fun onStop() {
        super.onStop()

        PrefUtil.setCount(requireContext(), count)
        PrefUtil.setInterval(requireContext(), interval!!)
    }
}

