package org.sayandev.loader.common;

import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.adapters.LogAdapter;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class GenericLibraryManager extends LibraryManager {
    public GenericLibraryManager(@NotNull LogAdapter logAdapter, @NotNull Path dataDirectory, @NotNull String directoryName) {
        super(logAdapter, dataDirectory, directoryName);
    }

    @Override
    protected void addToClasspath(@NotNull Path file) {
        // TODO: Implement
    }
}