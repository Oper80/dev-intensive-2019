package ru.skillbranch.devintensive.extensions

fun String.truncate(num: Int = 16) : String{
    var str = this.trim()
    return when{
        str.length > num -> "${str.substring(0, num).trim()}..."
        else -> str
    }
}

fun String.stripHtml() : String{
    val orig = this
    var tagFlag = 0
    var str = ""
    for (s in orig){
        str += when(s){
            '<' -> {tagFlag +=1
                    ""}
            '>' -> {tagFlag -=1
                ""}
            '&' -> ""
            '\"' -> ""
            '\'' -> ""
            else -> {if(tagFlag == 0) s else ""}
        }
    }
    val pattern = " {2}".toRegex()
    if (str != null) {
        while (pattern.containsMatchIn(str!!)) {
            str = str.replace("  ", " ")
        }
    }
    return str.trim()
}