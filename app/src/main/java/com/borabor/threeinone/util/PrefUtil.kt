package com.borabor.threeinone.util

import android.content.Context
import com.borabor.threeinone.fragment.CountdownTimerFragment
import com.borabor.threeinone.fragment.StopwatchFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object PrefUtil {
    /*** Basic calculator consts ***/
    private const val PRIMARY_TEXT_BC = "primary_text_bc"
    private const val SECONDARY_TEXT_BC = "secondary_text_bc"

    /*** Scientific calculator consts ***/
    private const val PRIMARY_TEXT_SC = "primary_text_sc"
    private const val SECONDARY_TEXT_SC = "secondary_text_sc"

    /*** Stopwatch consts ***/
    private const val TIMER_STATE_SW = "timer_state_sw"
    private const val UPDATE_TIME = "update_time"
    private const val START_TIME_SW = "start_time_sw"
    private const val ELAPSED_TIME = "elapsed_time"
    private const val LAP_STATE = "lap_state"
    private const val LAP_LIST = "lap_list"
    private const val LAP_COUNTER = "lap_counter"

    /*** Countdown timer consts ***/
    private const val TIMER_STATE_CD = "timer_state_cd"
    private const val REMAINING_TIME = "remaining_time"
    private const val START_TIME_CD = "start_time_cd"
    private const val END_TIME = "end_time"
    private const val SB_PROGRESS_LIST = "sb_progress_list"


    /*** Counter consts ***/
    private const val COUNT = "count"
    private const val INTERVAL = "interval"

    /*** Basic calculator prefs ***/
    fun setPrimaryTextBC(context: Context, value: String) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(PRIMARY_TEXT_BC, value)
            .apply()
    }

    fun getPrimaryTextBC(context: Context): String? {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(PRIMARY_TEXT_BC, "")
    }

    fun setSecondaryTextBC(context: Context, value: String) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(SECONDARY_TEXT_BC, value)
            .apply()
    }

    fun getSecondaryTextBC(context: Context): String? {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(SECONDARY_TEXT_BC, "")
    }

    /*** Scientific calculator prefs ***/
    fun setPrimaryTextSC(context: Context, value: String) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(PRIMARY_TEXT_SC, value)
            .apply()
    }

    fun getPrimaryTextSC(context: Context): String? {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(PRIMARY_TEXT_SC, "")
    }

    fun setSecondaryTextSC(context: Context, value: String) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(SECONDARY_TEXT_SC, value)
            .apply()
    }

    fun getSecondaryTextSC(context: Context): String? {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(SECONDARY_TEXT_SC, "")
    }

    /*** Stopwatch prefs ***/
    fun setTimerStateSW(context: Context, value: Int) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putInt(TIMER_STATE_SW, value)
            .apply()
    }

    fun getTimerStateSW(context: Context): StopwatchFragment.TimerState {
        val ordinal = context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getInt(TIMER_STATE_SW, 0)
        return StopwatchFragment.TimerState.values()[ordinal]
    }

    fun setUpdateTime(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putLong(UPDATE_TIME, value)
            .apply()
    }

    fun getUpdateTime(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(UPDATE_TIME, 0)
    }

    fun setStartTimeSW(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit().putLong(START_TIME_SW, value)
            .apply()
    }

    fun getStartTimeSW(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(START_TIME_SW, 0)
    }

    fun setElapsedTime(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putLong(ELAPSED_TIME, value)
            .apply()
    }

    fun getElapsedTime(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(ELAPSED_TIME, 0)
    }

    fun setLapState(context: Context, value: Boolean) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit().putBoolean(LAP_STATE, value)
            .apply()
    }

    fun getLapState(context: Context): Boolean {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getBoolean(LAP_STATE, false)
    }

    fun setLapList(context: Context, value: HashMap<Int, String>?) {
        val jsonString = Gson().toJson(value)
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(LAP_LIST, jsonString)
            .apply()
    }

    fun getLapList(context: Context): HashMap<Int, String>? {
        val defValue = Gson().toJson(HashMap<String, String>())
        val json = context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(LAP_LIST, defValue)
        val token = object : TypeToken<HashMap<Int, String>?>() {}
        return Gson().fromJson<HashMap<Int, String>?>(json, token.type)
    }

    fun setLapCounter(context: Context, value: Int) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putInt(LAP_COUNTER, value)
            .apply()
    }

    fun getLapCounter(context: Context): Int {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getInt(LAP_COUNTER, 0)
    }

    /*** Countdown timer prefs ***/
    fun setTimerStateCD(context: Context, state: CountdownTimerFragment.TimerState) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putInt(TIMER_STATE_CD, state.ordinal)
            .apply()
    }

    fun getTimerStateCD(context: Context): CountdownTimerFragment.TimerState {
        val ordinal = context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getInt(TIMER_STATE_CD, 0)
        return CountdownTimerFragment.TimerState.values()[ordinal]
    }

    fun setRemainingTime(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putLong(REMAINING_TIME, value)
            .apply()
    }

    fun getRemainingTime(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(REMAINING_TIME, 10)
    }

    fun setStartTimeCD(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putLong(START_TIME_CD, value)
            .apply()
    }

    fun getStartTimeCD(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(START_TIME_CD, 0)
    }

    fun setEndTime(context: Context, value: Long) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putLong(END_TIME, value)
            .apply()
    }

    fun getEndTime(context: Context): Long {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getLong(END_TIME, 0)
    }

    fun setSbProgressList(context: Context, value: HashMap<String, Int>) {
        val jsonString = Gson().toJson(value)
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(SB_PROGRESS_LIST, jsonString)
            .apply()
    }

    fun getSbProgressList(context: Context): HashMap<String, Int> {
        val defValue = Gson().toJson(hashMapOf("hour" to 0, "minute" to 0, "second" to 0))
        val json = context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(SB_PROGRESS_LIST, defValue)
        val token = object : TypeToken<HashMap<String, Int>>() {}
        return Gson().fromJson(json, token.type)
    }

    /*** Counter prefs ***/
    fun setCount(context: Context, value: Int) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putInt(COUNT, value)
            .apply()
    }

    fun getCount(context: Context): Int {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getInt(COUNT, 0)
    }

    fun setInterval(context: Context, value: Int) {
        context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putInt(INTERVAL, value)
            .apply()
    }

    fun getInterval(context: Context): Int {
        return context
            .getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getInt(INTERVAL, 1)
    }
}