package dev.xirado.ktbin.api

/**
 * Formatter used for formatting files for different environments
 *
 * @see [dev.xirado.ktbin.api.entity.DocumentFile.formatted]
 */
enum class Formatter(val id: String) {
    TERMINAL_8("terminal8"),
    TERMINAL_16("terminal16"),
    TERMINAL_256("terminal256"),
    TERMINAL_16M("terminal16m"),
    HTML("html"),
    HTML_STANDALONE("html-standalone"),
    SVG("svg"),
}