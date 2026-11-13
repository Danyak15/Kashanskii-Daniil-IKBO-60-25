package org.example

import javax.swing.SwingUtilities

private const val REQUIRED_ARGUMENTS = 2

fun main(args: Array<String>) {
    if (args.size != REQUIRED_ARGUMENTS) {
        println("Ошибка: ожидается два аргумента")
        return
    }

    val config = EmulatorConfig(
        args[0],
        args[1]
    )

    println("Путь к VFS: ${config.vfsPath}")
    println("Путь к скрипту: ${config.scriptPath}")

    SwingUtilities.invokeLater {
        TerminalWindow(config).isVisible = true
    }
}