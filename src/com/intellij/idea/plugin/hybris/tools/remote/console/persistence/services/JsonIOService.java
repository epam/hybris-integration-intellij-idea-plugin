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

package com.intellij.idea.plugin.hybris.tools.remote.console.persistence.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Service(Service.Level.PROJECT)
public final class JsonIOService {

    private static final Logger LOG = Logger.getInstance(RegionPersistenceService.class);

    private final ObjectMapper mapper;

    private JsonIOService() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static JsonIOService getInstance(@NotNull final Project project) {
        return project.getService(JsonIOService.class);
    }

    public <T> void persistData(@NotNull final Path destination, @NotNull final T data) {
        FileUtil.createIfDoesntExist(new File(String.valueOf(destination)));
        try {
            Files.write(destination, Collections.singleton(mapper.writeValueAsString(data)));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Nullable
    public <T> T loadPersistedData(@NotNull final Path source, final Class<T> clazz) {
        if (Files.exists(source)) {
            try {
                return readDataFromFile(source, clazz);
            } catch (IOException e) {
                throw new IllegalStateException(String.format(
                    "Path [ %s ] is not valid source file path",
                    source
                ), e);
            }
        }
        return null;
    }

    private <T> T readDataFromFile(@NotNull final Path filePath, final Class<T> clazz) throws IOException {
        final byte[] jsonData = Files.readAllBytes(filePath);
        return mapper.readValue(jsonData, clazz);
    }
}
