package org.example

import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    if (args.size != 2) {
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