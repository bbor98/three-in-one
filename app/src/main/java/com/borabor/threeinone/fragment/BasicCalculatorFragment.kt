package com.borabor.threeinone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.borabor.threeinone.R
import com.borabor.threeinone.util.ButtonUtil.addNumberValueToText
import com.borabor.threeinone.util.ButtonUtil.addOperatorValueToText
import com.borabor.threeinone.util.ButtonUtil.enterNumberToast
import com.borabor.threeinone.util.ButtonUtil.invalidInputToast
import com.borabor.threeinone.util.ButtonUtil.vibratePhone
import com.borabor.threeinone.util.CalculationUtil
import com.borabor.threeinone.util.PrefUtil
import kotlinx.android.synthetic.main.fragment_basic_calculator.*


class BasicCalculatorFragment : Fragment() {

    companion object {
        var addedBC = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addNumberValueToText(requireContext(), bt0BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt1BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt2BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt3BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt4BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt5BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt6BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt7BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt8BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), bt9BC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), btBracketOpenBC, tvPrimaryBC, 0)
        addNumberValueToText(requireContext(), btBracketCloseBC, tvPrimaryBC, 0)

        addOperatorValueToText(requireContext(), btAdditionBC, tvPrimaryBC, "+", 0)
        addOperatorValueToText(requireContext(), btSubtractionBC, tvPrimaryBC, "-", 0)
        addOperatorValueToText(requireContext(), btMultiplicationBC, tvPrimaryBC, "*", 0)
        addOperatorValueToText(requireContext(), btDivisionBC, tvPrimaryBC, "/", 0)

        btDotBC.setOnClickListener {
            vibratePhone(requireContext())
            if (!tvPrimaryBC.text.contains(".")) tvPrimaryBC.text = tvPrimaryBC.text.toString() + "."
        }

        btPercentage.setOnClickListener {
            vibratePhone(requireContext())

            try {
                if (tvPrimaryBC.text.isEmpty()) enterNumberToast(requireContext())
                else {
                    val input = tvPrimaryBC.text.toString()
                    val result = (input.toFloat() / 100).toString()
                    tvPrimaryBC.text = CalculationUtil.trimResult(result)
                    tvSecondaryBC.text = "${input}%"
                }
            } catch (e: Exception) {
                invalidInputToast(requireContext())
            }
        }

        btACBC.setOnClickListener {
            vibratePhone(requireContext())

            tvPrimaryBC.text = ""
            tvSecondaryBC.text = ""
            addedBC = false
        }

        btDeleteBC.setOnClickListener {
            vibratePhone(requireContext())

            if (tvPrimaryBC.text.contains("+") ||
                tvPrimaryBC.text.contains("-") ||
                tvPrimaryBC.text.contains("*") ||
                tvPrimaryBC.text.contains("/")
            ) addedBC = false

            if (tvPrimaryBC.text.isNotEmpty()) tvPrimaryBC.text = tvPrimaryBC.text.subSequence(0, tvPrimaryBC.length() - 1)
        }

        btEqualBC.setOnClickListener {
            vibratePhone(requireContext())

            try {
                if (tvPrimaryBC.text.isNotEmpty()) {
                    val input = tvPrimaryBC.text.toString()
                    val result = CalculationUtil.evaluate(input).toString()
                    tvPrimaryBC.text = CalculationUtil.trimResult(result)
                    tvSecondaryBC.text = input
                    addedBC = false
                }
            } catch (e: Exception) {
                invalidInputToast(requireContext())
            }
        }
    }

    override fun onStart() {
        super.onStart()

        tvPrimaryBC.text = PrefUtil.getPrimaryTextBC(requireContext())
        tvSecondaryBC.text = PrefUtil.getSecondaryTextBC(requireContext())
    }

    override fun onStop() {
        super.onStop()

        PrefUtil.setPrimaryTextBC(requireContext(), tvPrimaryBC.text.toString())
        PrefUtil.setSecondaryTextBC(requireContext(), tvSecondaryBC.text.toString())
    }
}