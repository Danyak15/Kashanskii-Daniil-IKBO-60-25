package org.example

import java.awt.Color
import java.awt.Font
import java.awt.event.ActionEvent
import java.net.InetAddress
import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.KeyStroke
import kotlin.system.exitProcess

class TerminalWindow : JFrame() {
    private val username = System.getProperty("user.name")
    private val hostname = InetAddress.getLocalHost().hostName
    private val terminal = JTextArea()

    private val prompt = "$username@$hostname:~$ "
    private var inputStart = 0

    init {
        title = "Эмулятор - [$username@$hostname]"
        defaultCloseOperation = EXIT_ON_CLOSE

        setSize(800, 500)
        setLocationRelativeTo(null)

        terminal.background = Color.BLACK
        terminal.foreground = Color.WHITE
        terminal.caretColor = Color.WHITE
        terminal.font = Font(Font.MONOSPACED, Font.PLAIN, 16)

        add(JScrollPane(terminal))

        terminal.inputMap.put(
            KeyStroke.getKeyStroke("ENTER"), "submit"
        )

        terminal.actionMap.put("submit", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                handleEnter()
            }
        })

        terminal.text = prompt
        inputStart = terminal.document.length
        terminal.caretPosition = inputStart
    }

    private fun handleEnter() {
        val commandLine = terminal.text.substring(inputStart)

        terminal.append("\n")

        val parsedCommand = CommandParser.parse(commandLine)

        if (parsedCommand != null) {
            executeCommand(parsedCommand)
        }

        terminal.append(prompt)
        inputStart = terminal.document.length
        terminal.caretPosition = inputStart
    }

    private fun executeCommand(command: ParsedCommand) {
        when (command.name) {
            "ls" -> terminal.append("ls: ${command.args}\n")
            "cd" -> terminal.append("cd: ${command.args}\n")
            "exit" -> exitProcess(0)
            else -> terminal.append("Ошибка: неизвестная команда '${command.name}'\n")
        }
    }
}