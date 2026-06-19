package com.example.snaphunt.image_recognition

enum class DailyObjects(val keyword: String) {
    PERSON("person"),
    BICYCLE("bicycle"),
    CAR("car"),
    BENCH("bench"),
    BACKPACK("backpack"),
    UMBRELLA("umbrella"),
    HANDBAG("handbag"),
    TIE("tie"),
    SUITCASE("suitcase"),
    BOTTLE("bottle"),
    WINE_GLASS("wine glass"),
    CUP("cup"),
    FORK("fork"),
    KNIFE("knife"),
    SPOON("spoon"),
    BOWL("bowl"),
    BANANA("banana"),
    APPLE("apple"),
    SANDWICH("sandwich"),
    ORANGE("orange"),
    CHAIR("chair"),
    COUCH("couch"),
    BED("bed"),
    DINING_TABLE("dining table"),
    TV("tv"),
    LAPTOP("laptop"),
    MOUSE("mouse"),
    REMOTE("remote"),
    KEYBOARD("keyboard"),
    CELL_PHONE("cell phone"),
    MICROWAVE("microwave"),
    OVEN("oven"),
    TOASTER("toaster"),
    SINK("sink"),
    REFRIGERATOR("refrigerator"),
    BOOK("book"),
    CLOCK("clock"),
    VASE("vase"),
    SCISSORS("scissors"),
    TOOTHBRUSH("toothbrush");

    companion object {
        fun fromString(keyword: String): DailyObjects? {
            return entries.find { it.keyword == keyword }
        }
    }
}