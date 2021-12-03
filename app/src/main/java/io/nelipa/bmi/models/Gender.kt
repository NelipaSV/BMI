package io.nelipa.bmi.models

enum class Gender(val id: Int) {
    MALE(0),
    FEMALE(1),
    UNKNOWN(-1);

    companion object {
        fun toGender(gender: Int) : Gender {
            return values().associateBy(Gender::id)[gender] ?: UNKNOWN
        }
    }
}