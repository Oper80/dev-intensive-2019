package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.min

const val SECONDS = 1000L
const val MINUTES = 60 * SECONDS
const val HOURS = 60 * MINUTES
const val DAYS = 24 * HOURS

fun Date.format(pattern:String = "HH:mm:ss dd.MM.yy"):String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when(units){
        TimeUnits.SECOND -> value * SECONDS
        TimeUnits.MINUTE -> value * MINUTES
        TimeUnits.HOUR -> value * HOURS
        TimeUnits.DAY -> value * DAYS
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date:Date = Date()): String {
    val diff = (date.time - this.time)

    fun pluralForm(number:Int, words:ArrayList<String>):String {
        val cases = arrayListOf (2, 0, 1, 1, 1, 2)
        return words[if(number%100 in 5..19) 2 else cases[min(number%10, 5)]]
    }

    return when{
        diff < -360 * DAYS-> "более чем через год"
        diff < -26 * HOURS -> "через ${abs(diff / DAYS)} ${pluralForm(abs((diff / DAYS).toInt()), arrayListOf("день", "дня", "дней"))}"
        diff < -22 * HOURS -> "через день"
        diff < -75 * MINUTES -> "через ${abs(diff / HOURS)} ${pluralForm(abs((diff / HOURS).toInt()), arrayListOf("час", "часа", "часов"))}"
        diff < -45 * MINUTES -> "через час"
        diff < -75 * SECONDS -> "через ${abs(diff / MINUTES)} ${pluralForm(abs((diff / MINUTES).toInt()), arrayListOf("минуту", "минуты", "минут"))}"
        diff < -45 * SECONDS -> "через минуту"
        diff < -1 -> "через несколько секунд"
        diff <= 1 * SECONDS -> "только что"
        diff <= 45 * SECONDS -> "несколько секунд назад"
        diff <= 75 * SECONDS-> "минуту назад"
        diff <= 45 * MINUTES -> "${abs(diff / MINUTES)} ${pluralForm(abs((diff / MINUTES).toInt()), arrayListOf("минуту", "минуты", "минут"))} назад"
        diff <= 75 * MINUTES -> "час назад"
        diff <= 22 * HOURS -> "${abs(diff / HOURS)} ${pluralForm(abs((diff / HOURS).toInt()), arrayListOf("час", "часа", "часов"))} назад"
        diff <= 26 * HOURS -> "день назад"
        diff <= 360 * DAYS -> "${abs(diff / DAYS)} ${pluralForm(abs((diff / DAYS).toInt()), arrayListOf("день", "дня", "дней"))} назад"
        diff > 360 * DAYS -> "более года назад"
        else -> "Wrong date"
    }
}

enum class TimeUnits{
    SECOND, MINUTE, HOUR, DAY;

    fun plural(number: Int):String {
        val cases = arrayListOf(2, 0, 1, 1, 1, 2)
        val words = when (this) {
            SECOND -> arrayListOf<String>("секунду", "секунды", "секунд")
            MINUTE -> arrayListOf<String>("минуту", "минуты", "минут")
            HOUR -> arrayListOf<String>("час", "часа", "часов")
            DAY -> arrayListOf<String>("день", "дня", "дней")
        }
        return "$number ${words[if (number % 100 in 5..19) 2 else cases[min(number % 10, 5)]]}"
    }
}