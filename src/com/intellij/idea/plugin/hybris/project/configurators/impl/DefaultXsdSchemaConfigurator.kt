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
package com.intellij.idea.plugin.hybris.project.configurators.impl

import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.project.configurators.XsdSchemaConfigurator
import com.intellij.idea.plugin.hybris.project.descriptors.HybrisProjectDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.ModuleDescriptor
import com.intellij.idea.plugin.hybris.system.cockpitng.CngConfigDomFileDescription
import com.intellij.javaee.ExternalResourceManagerEx
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import java.nio.file.Path
import kotlin.io.path.exists

class DefaultXsdSchemaConfigurator : XsdSchemaConfigurator {

    override fun configure(
        project: Project,
        hybrisProjectDescriptor: HybrisProjectDescriptor,
        moduleDescriptors: List<ModuleDescriptor>,
    ) {
        val cockpitCoreJarFileSystem = moduleDescriptors
            .firstOrNull { it.name == HybrisConstants.EXTENSION_NAME_BACK_OFFICE }
            ?.moduleRootDirectory
            ?.toPath()
            ?.resolve(Path.of("web", "webroot", "WEB-INF", "lib"))
            ?.takeIf { it.exists() }
            ?.toFile()
            ?.listFiles { _, name -> name.startsWith("cockpitcore-", true) && name.endsWith(".jar", true) }
            ?.firstOrNull()
            ?.absolutePath
            ?: return

        runWriteAction {
            val externalResourceManager = ExternalResourceManagerEx.getInstanceEx()

            externalResourceManager.addResource(
                CngConfigDomFileDescription.NAMESPACE_COCKPIT_NG_CONFIG_HYBRIS,
                "$cockpitCoreJarFileSystem!/schemas/config/hybris/cockpit-configuration-hybris.xsd",
                project
            )
            externalResourceManager.addResource(
                "http://www.hybris.com/schema/cockpitng/editor-definition.xsd",
                "$cockpitCoreJarFileSystem!/schemas/editor-definition.xsd",
                project
            )
        }
    }

}
