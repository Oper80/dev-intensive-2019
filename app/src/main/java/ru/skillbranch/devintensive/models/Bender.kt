package ru.skillbranch.devintensive.models

import android.os.MessageQueue

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        if (question.validate(answer) == "OK") {
            return when {
                question == Question.IDLE -> question.question to status.color
                (question.answers.contains(answer.toLowerCase())) -> {
                    question = question.nextQuestion()
                    "Отлично - ты справился\n${question.question}" to status.color
                }
                status == Status.CRITICAL -> {
                    status = status.nextStatus()
                    question = Question.NAME
                    "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                }
                else -> {
                    status = status.nextStatus()
                    "Это неправильный ответ\n${question.question}" to status.color
                }
            }
        } else {
            return "${question.validate(answer)}\n${question.question}" to status.color
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("bender", "бендер")) {
            override fun nextQuestion(): Question = PROFESSION

            override fun validate(ans: String): String {
                return when {
                    ans[0] in 'A'..'Z' -> "OK"
                    ans[0] in 'А'..'Я' -> "OK"
                    else -> "Имя должно начинаться с заглавной буквы"
                }
            }
        },
        PROFESSION("Назови мою профессию?", listOf("bender", "сгибальщик")) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validate(ans: String): String {
                return when {
                    ans[0] in 'a'..'z' -> "OK"
                    ans[0] in 'а'..'я' -> "OK"
                    else -> "Профессия должна начинаться со строчной буквы"
                }
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
            override fun validate(ans: String): String {
                for (i in ans) {
                    if (i.isDigit()) {
                        return "Материал не должен содержать цифр"
                    }
                }
                return "OK"
            }
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
            override fun validate(ans: String): String {
                for (i in ans) {
                    if (!i.isDigit()) {
                        return "Год моего рождения должен содержать только цифры"
                    }
                }
                return "OK"
            }
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(ans: String): String {
                if (ans.length != 7) {
                    return "Серийный номер содержит только цифры, и их 7"
                }
                for (i in ans) {
                    if (!i.isDigit()) {
                        return "Серийный номер содержит только цифры, и их 7"
                    }
                }
                return "OK"
            }

        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(ans: String): String {
                return "OK"
            }
        };

        abstract fun nextQuestion(): Question

        abstract fun validate(ans: String): String
    }
}