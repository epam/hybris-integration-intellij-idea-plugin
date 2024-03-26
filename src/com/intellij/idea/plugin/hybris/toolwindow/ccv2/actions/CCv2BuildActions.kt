/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for IntelliJ IDEA.
 * Copyright (C) 2019-2024 EPAM Systems <hybrisideaplugin@epam.com> and contributors
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

package com.intellij.idea.plugin.hybris.toolwindow.ccv2.actions

import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.settings.CCv2Subscription
import com.intellij.idea.plugin.hybris.tools.ccv2.CCv2Service
import com.intellij.idea.plugin.hybris.tools.ccv2.ui.CreateBuildDialog
import com.intellij.idea.plugin.hybris.toolwindow.ccv2.CCv2Tab
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class CreateBuildAction : AbstractCCv2Action(
    tab = CCv2Tab.BUILDS,
    text = "Schedule a Build",
    icon = HybrisIcons.CCV2_BUILD_CREATE
) {
    override fun actionPerformed(e: AnActionEvent) {
        e.project
            ?.let { CreateBuildDialog(it) }
            ?.showAndGet()
    }
}

class FetchBuildsAction : AbstractFetchAction(
    tab = CCv2Tab.BUILDS,
    taskTitle = "Fetching CCv2 Builds...",
    text = "Fetch Builds",
    icon = HybrisIcons.CCV2_FETCH
) {

    override fun fetch(project: Project, ccv2Subscriptions: List<CCv2Subscription>) {
        CCv2Service.getInstance(project).fetchBuilds(ccv2Subscriptions)
    }
}