package com.azamovhudstc.soplay.utils

// Function to display a simple loading animation with color
fun displayLoadingAnimation(message: String, color: Color) {
    val loadingChars = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
    var counter = 0

    val loadingAnimation = Thread {
        try {
            while (true) {
                printColored("\r${loadingChars[counter % loadingChars.size]} $message", color)
                counter++
                Thread.sleep(100) // Adjust the delay based on your preference
            }
        } catch (e: InterruptedException) {
            // Restore interrupted status
            Thread.currentThread().interrupt()
        }
    }

    loadingAnimation.start()

    // Run the loading animation for a few seconds (you can adjust the duration)
    Thread.sleep(3000)

    loadingAnimation.interrupt()

    // Clear the loading line
    printColored("\r${" ".repeat(message.length + 3)}\r", color)
}

// Function to print colored text
fun printlnColored(text: String, color: Color) {
    val colorCode = when (color) {
        Color.RED -> "\u001B[31m"
        Color.GREEN -> "\u001B[32m"
        Color.YELLOW -> "\u001B[33m"
        Color.BLUE -> "\u001B[34m"
        Color.MAGENTA -> "\u001B[35m"
        Color.CYAN -> "\u001B[36m"
        Color.WHITE -> "\u001B[37m"
        Color.DARK_ORANGE -> "\u001B[38;2;170;85;0m"

    }
    val resetColor = "\u001B[0m"

    println("$colorCode$text$resetColor")
}

fun printColored(text: String, color: Color) {
    val colorCode = when (color) {
        Color.RED -> "\u001B[31m"
        Color.GREEN -> "\u001B[32m"
        Color.YELLOW -> "\u001B[33m"
        Color.BLUE -> "\u001B[34m"
        Color.MAGENTA -> "\u001B[35m"
        Color.CYAN -> "\u001B[36m"
        Color.WHITE -> "\u001B[37m"
        Color.DARK_ORANGE -> "\u001B[38;2;170;85;0m"
    }
    val resetColor = "\u001B[0m"

    print("$colorCode$text$resetColor")
}

// Enum to represent ANSI color codes
enum class Color {
    RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE, DARK_ORANGE
}
