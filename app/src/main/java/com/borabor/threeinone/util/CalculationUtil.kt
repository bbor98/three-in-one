package com.borabor.threeinone.util

import kotlin.math.*

object CalculationUtil {
    fun evaluate(input: String): Float {
        return object : Any() {
            var position = -1
            var char = 0
            var x = 0F

            fun moveToNextChar() {
                char = if (++position < input.length) input[position].code else -1
            }

            fun checkAndRemove(charToRemove: Int): Boolean {
                while (char == ' '.code) moveToNextChar()

                if (char == charToRemove) {
                    moveToNextChar()
                    return true
                }
                return false
            }

            fun parse(): Float {
                moveToNextChar()
                x = parseAddSub()

                if (position < input.length) throw RuntimeException("Unexpected: " + char.toChar())
                return x
            }

            fun parseAddSub(): Float {
                x = parseMulDiv()

                while (true) {
                    when {
                        checkAndRemove('+'.code) -> x += parseMulDiv()
                        checkAndRemove('-'.code) -> x -= parseMulDiv()
                        else -> return x
                    }
                }
            }

            fun parseMulDiv(): Float {
                x = parseOther()

                while (true) {
                    when {
                        checkAndRemove('*'.code) -> x *= parseOther()
                        checkAndRemove('/'.code) -> x /= parseOther()
                        else -> return x
                    }
                }
            }

            fun parseOther(): Float {
                if (checkAndRemove('+'.code)) return parseOther()
                if (checkAndRemove('-'.code)) return -parseOther()

                val startPosition = position

                if (checkAndRemove('('.code)) {
                    x = parseAddSub()
                    checkAndRemove(')'.code)
                } else if (char >= '0'.code && char <= '9'.code || char == '.'.code) {
                    while (char >= '0'.code && char <= '9'.code || char == '.'.code) moveToNextChar()
                    x = input.substring(startPosition, position).toFloat()
                } else if (char >= 'a'.code && char <= 'z'.code) {
                    while (char >= 'a'.code && char <= 'z'.code) moveToNextChar()
                    val function = input.substring(startPosition, position)
                    x = parseOther()
                    x = when (function) {
                        //"sqrt" -> sqrt(x)
                        "sin" -> sin(Math.toRadians(x.toDouble())).toFloat()
                        "cos" -> cos(Math.toRadians(x.toDouble())).toFloat()
                        "tan" -> tan(Math.toRadians(x.toDouble())).toFloat()
                        "log" -> log10(x)
                        "ln" -> ln(x)
                        else -> throw RuntimeException("Unknown function: $function")
                    }
                } else throw RuntimeException("Unexpected: " + char.toChar())
                if (checkAndRemove('^'.code)) x = x.pow(parseOther())
                return x
            }
        }.parse()
    }

    fun trimResult(result: String?): String? {
        return if (!result.isNullOrEmpty()) {
            if (result.indexOf(".") < 0) result
            else result.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        } else result
    }
}