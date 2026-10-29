package org.example

import java.awt.Color
import java.awt.Font
import java.awt.event.ActionEvent
import java.io.File
import java.io.IOException
import java.net.InetAddress
import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.KeyStroke
import kotlin.system.exitProcess

class TerminalWindow(
    private val config: EmulatorConfig,
) : JFrame() {
    private val username = System.getProperty("user.name")
    private val hostname = InetAddress.getLocalHost().hostName
    private val terminal = JTextArea()

    private val vfs = VirtualFileSystem(config.vfsPath)

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

        loadVfs()

        readStartupScript()
    }

    private fun loadVfs() {
        try {
            vfs.load()
            terminal.append("VFS: ${vfs.directoryCount} каталогов, ${vfs.fileCount} файлов\n")

            for ((path, isDirectory) in vfs.allEntries()) {
                val type = if (isDirectory) "DIR " else "FILE"
                terminal.append("  $type $path\n")
            }
        } catch (e: IOException) {
            terminal.append("Ошибка загрузки VFS: ${e.message}\n")
        }
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
            "vfs-init" -> resetVfs(command.args)
            else -> terminal.append("Ошибка: неизвестная команда '${command.name}'\n")
        }
    }

    private fun resetVfs(args: List<String>) {
        if (args.isNotEmpty()) {
            terminal.append("Ошибка: vfs-init не принимает аргументы\n")
            return
        }

        try {
            vfs.reset()
            terminal.append("VFS сброшена: данные в памяти и CSV очищены\n")
        } catch (e: IOException) {
            terminal.append("Ошибка сброса VFS: ${e.message}\n")
        }
    }

    private fun readStartupScript() {
        try {
            val lines = File(config.scriptPath).readLines()

            for (line in lines) {
                val command = line.trim()

                if (command.isEmpty() || command.startsWith("//")) {
                    continue
                }

                terminal.append("$command\n")
                val parsedCommand = CommandParser.parse(command)

                if (parsedCommand != null) {
                    executeCommand(parsedCommand)
                }

                terminal.append(prompt)
                inputStart = terminal.document.length
                terminal.caretPosition = inputStart
            }
        } catch (e: IOException) {
            println("Ошибка чтения скрипта: ${e.message}")
        }
    }
}