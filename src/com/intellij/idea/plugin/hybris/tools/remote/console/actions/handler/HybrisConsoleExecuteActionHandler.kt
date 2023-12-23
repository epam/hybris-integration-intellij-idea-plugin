/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019-2023 EPAM Systems <hybrisideaplugin@epam.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.tools.remote.console.actions.handler


import com.intellij.execution.console.ConsoleHistoryController
import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.ui.ConsoleViewContentType.*
import com.intellij.idea.plugin.hybris.impex.file.ImpexFileType
import com.intellij.idea.plugin.hybris.tools.remote.console.HybrisConsole
import com.intellij.idea.plugin.hybris.tools.remote.console.HybrisConsoleService
import com.intellij.idea.plugin.hybris.tools.remote.console.impl.HybrisImpexMonitorConsole
import com.intellij.idea.plugin.hybris.tools.remote.console.impl.HybrisSolrSearchConsole
import com.intellij.idea.plugin.hybris.tools.remote.http.impex.HybrisHttpResult
import com.intellij.idea.plugin.hybris.tools.remote.http.impex.HybrisHttpResult.HybrisHttpResultBuilder.createResult
import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil

class HybrisConsoleExecuteActionHandler(private val project: Project,
                                        private val preserveMarkup: Boolean) {

    private fun setEditorEnabled(console: HybrisConsole, enabled: Boolean) {
        console.consoleEditor.isRendererMode = !enabled
        ApplicationManager.getApplication().invokeLater { console.consoleEditor.component.updateUI() }
    }

    private fun processLine(console: HybrisConsole, query: String) {
        ApplicationManager.getApplication().runReadAction {
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Execute HTTP Call to SAP Commerce...") {
                override fun run(indicator: ProgressIndicator) {
                    isProcessRunning = true
                    try {
                        setEditorEnabled(console, false)
                        val httpResult = console.execute(query)

                        when (console) {
                            is HybrisImpexMonitorConsole -> {
                                console.clear()
                                printSyntaxText(console, httpResult.output, ImpexFileType)
                            }
                            is HybrisSolrSearchConsole -> {
                                console.clear()
                                if (httpResult.hasError()) {
                                    printSyntaxText(console, httpResult.errorMessage, PlainTextFileType.INSTANCE)
                                } else {
                                    printSyntaxText(console, httpResult.output, JsonFileType.INSTANCE)
                                }

                            }
                            else -> printPlainText(console, httpResult)
                        }
                    } finally {
                        isProcessRunning = false
                        setEditorEnabled(console, true)
                    }
                }
            })
        }
    }

    private fun printPlainText(console: HybrisConsole, httpResult: HybrisHttpResult?) {
        val result = createResult().errorMessage(httpResult?.errorMessage).output(httpResult?.output).result(httpResult?.result).detailMessage(httpResult?.detailMessage).build()
        val detailMessage = result.detailMessage
        val output = result.output
        val res = result.result
        val errorMessage = result.errorMessage

        if (result.hasError()) {
            console.print("[ERROR] \n", SYSTEM_OUTPUT)
            console.print("$errorMessage\n$detailMessage\n", ERROR_OUTPUT)
            return
        }
        if (!StringUtil.isEmptyOrSpaces(output)) {
            console.print("[OUTPUT] \n", SYSTEM_OUTPUT)
            console.print(output, NORMAL_OUTPUT)
        }
        if (!StringUtil.isEmptyOrSpaces(res)) {
            console.print("[RESULT] \n", SYSTEM_OUTPUT)
            console.print(res, NORMAL_OUTPUT)
        }
    }

    private fun printSyntaxText(console: HybrisConsole, output: String, fileType: FileType) {
        ConsoleViewUtil.printAsFileType(console, output, fileType)
    }

    fun runExecuteAction() {
        val activeConsole = HybrisConsoleService.getInstance(project).getActiveConsole() ?: return

        ConsoleHistoryController.getController(activeConsole)
            ?.let { execute(activeConsole, it) }
    }

    private fun execute(console: HybrisConsole,
                        consoleHistoryController: ConsoleHistoryController) {

        // Process input and add to history
        val document = console.currentEditor.document
        val textForHistory = document.text
        console.preProcessors().forEach { processor -> console.setInputText(processor.process(console)) }

        val query = document.text
        val range = TextRange(0, document.textLength)

        if (query.isNotEmpty() || console is HybrisImpexMonitorConsole) {
            console.currentEditor.selectionModel.setSelection(range.startOffset, range.endOffset)
            console.addToHistory(range, console.consoleEditor, preserveMarkup)
            console.printDefaultText()

            if (!StringUtil.isEmptyOrSpaces(textForHistory)) {
                consoleHistoryController.addToHistory(textForHistory.trim())
            }

            processLine(console, query)
        }
    }

    var isProcessRunning: Boolean = false

}
