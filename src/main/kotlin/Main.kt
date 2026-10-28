package org.example

import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        TerminalWindow().isVisible = true
    }
}