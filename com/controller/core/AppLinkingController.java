package com.controller.core;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Controller
@HorizonteController
@Slf4j
public class AppLinkingController {
    private static final Gson GSON = new Gson();
    private final Object androidAssetLinksObject = readAppAssociationFile("/resources/well-known/assetlinks.json");

    @Nullable
    private Object readAppAssociationFile(final String path) {
        BufferedReader reader = null;
        try {
            final InputStream inputStream = getClass().getResourceAsStream(path);
            if (inputStream == null) {
                log.info("Could not locate app-link file at path '{}'", path);
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.toString()));
            final String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return GSON.fromJson(contents, Object.class);
        } catch (Exception e) {
            log.error("Error reading app-link file: " + path, e);
            throw new Exception();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.warn("Error closing reader", e);
                }
            }
        }
    }


    /**
     * Reads the android-assetlinks.json file and outputs the contents directly to the response body in the
     * form of a basic Object which the response will serialize.
     * @return JSON object representing the Android assetlinks.json file.
     */
    @Start(name = "StartTime")
    @Time(name = "Time")
    @RequestMapping(value = { "/.well-known/assetlinks.json"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object androidAssetLinksFile() {
        return androidAssetLinksObject;
    }
}



