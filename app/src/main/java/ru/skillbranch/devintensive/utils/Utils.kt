package ru.skillbranch.devintensive.utils

import android.R
import android.R.attr.data
import android.content.res.TypedArray
import android.util.TypedValue



object Utils {
    fun parseFullName(isFullName: String?): Pair<String?, String?> {
        var fullName = isFullName
        val pattern = " {2}".toRegex()
        if (fullName != null) {
            while (pattern.containsMatchIn(fullName!!)) {
                fullName = fullName.replace("  ", " ")
            }
        }
        fullName = fullName?.trim()

        val parts: List<String>? = fullName?.split(" ")

        val firstName = when (parts?.getOrNull(0)) {
            "" -> null
            " " -> null
            else -> parts?.getOrNull(0)
        }

        val lastName = when (parts?.getOrNull(1)) {
            "", " " -> null
            else -> parts?.getOrNull(1)
        }
        return firstName to lastName
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val first: String? = when (firstName) {
            null, "", " " -> null
            else -> firstName[0].toUpperCase().toString()
        }

        val second = when (lastName) {
            null, "", " " -> null
            else -> lastName[0].toUpperCase().toString()
        }
        return if (first == null) second else if (second == null) first else first + second

    }

    fun transliteration(payload: String, divider: String = " "): String {
        val map: HashMap<String, String> = hashMapOf(
                "а" to "a",
                "б" to "b",
                "в" to "v",
                "г" to "g",
                "д" to "d",
                "е" to "e",
                "ё" to "e",
                "ж" to "zh",
                "з" to "z",
                "и" to "i",
                "й" to "i",
                "к" to "k",
                "л" to "l",
                "м" to "m",
                "н" to "n",
                "о" to "o",
                "п" to "p",
                "р" to "r",
                "с" to "s",
                "т" to "t",
                "у" to "u",
                "ф" to "f",
                "х" to "h",
                "ц" to "c",
                "ч" to "ch",
                "ш" to "sh",
                "щ" to "sh'",
                "ъ" to "",
                "ы" to "i",
                "ь" to "",
                "э" to "e",
                "ю" to "yu",
                "я" to "ya")
        var s = ""
        for (i in payload) {
            when {
                map.containsKey(i.toString()) -> s += map[i.toString()]
                map.containsKey(i.toString().toLowerCase()) -> s += map[i.toString().toLowerCase()]!!.capitalize()
                i.toString() == " " -> s += divider
                else -> s += i
            }
        }
        return s
    }

    fun isInvalidGithub(path: String?): Boolean {
        if (path.isNullOrEmpty()) return false
        var account = ""
        val except = listOf(
                "enterprise",
                "features",
                "topics",
                "collections",
                "trending",
                "events",
                "marketplace",
                "pricing",
                "nonprofit",
                "customer-stories",
                "security",
                "login",
                "join"
        )
        val prefixes = listOf(
                "github.com/",
                "https://github.com/",
                "www.github.com/",
                "https://www.github.com/"
        )
        for (prefix in prefixes) {
            if (path.toLowerCase().startsWith(prefix)) {
                account = path.toLowerCase().removePrefix(prefix)
                return account.isEmpty() || account in except || account.contains("/") || account.contains(" ")
            }
        }
        return true
    }

}
