package com.intellij.javascript.karma;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.javascript.karma.util.GsonUtil;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Simonchik
 */
public class KarmaConfig {

  private static final Logger LOG = Logger.getInstance(KarmaConfig.class);
  private static final String AUTO_WATCH = "autoWatch";
  private static final String BASE_PATH = "basePath";
  private static final String BROWSERS = "browsers";
  private static final String HOST_NAME = "hostname";
  private static final String URL_ROOT = "urlRoot";

  private final boolean myAutoWatch;
  private final List<String> myBrowsers;
  private final String myBasePath;
  private final String myHostname;
  private final String myUrlRoot;

  public KarmaConfig(boolean autoWatch,
                     @NotNull String basePath,
                     @NotNull List<String> browsers,
                     @NotNull String hostname,
                     @NotNull String urlRoot) {
    myAutoWatch = autoWatch;
    myBasePath = basePath;
    myBrowsers = ImmutableList.copyOf(browsers);
    myHostname = hostname;
    myUrlRoot = urlRoot;
  }

  public boolean isAutoWatch() {
    return myAutoWatch;
  }

  @NotNull
  public String getBasePath() {
    return myBasePath;
  }

  @NotNull
  public List<String> getBrowsers() {
    return myBrowsers;
  }

  @NotNull
  public String getHostname() {
    return myHostname;
  }

  @NotNull
  public String getUrlRoot() {
    return myUrlRoot;
  }

  @Nullable
  public static KarmaConfig parseFromJson(@NotNull JsonElement jsonElement,
                                          @NotNull File configurationFileDir) {
    if (jsonElement.isJsonObject()) {
      JsonObject rootObject = jsonElement.getAsJsonObject();

      boolean autoWatch = GsonUtil.getBooleanProperty(rootObject, AUTO_WATCH, false);
      List<String> browsers = parseBrowsers(rootObject);
      String basePath = parseBasePath(jsonElement, rootObject, configurationFileDir);
      String hostname = parseHostname(jsonElement, rootObject);
      String urlRoot = parseUrlRoot(jsonElement, rootObject);

      return new KarmaConfig(autoWatch, basePath, browsers, hostname, urlRoot);
    }
    return null;
  }

  @NotNull
  private static String parseBasePath(@NotNull JsonElement all,
                                      @NotNull JsonObject obj,
                                      @NotNull File configurationFileDir) {
    String basePath = GsonUtil.getStringProperty(obj, BASE_PATH);
    if (basePath == null) {
      LOG.warn("Can not parse Karma config.basePath from " + all.toString());
      basePath = configurationFileDir.getAbsolutePath();
    }
    return basePath;
  }

  private static String parseUrlRoot(@NotNull JsonElement all, @NotNull JsonObject obj) {
    String urlRoot = GsonUtil.getStringProperty(obj, URL_ROOT);
    if (urlRoot == null) {
      LOG.warn("Can not parse Karma config.urlRoot from " + all.toString());
      urlRoot = "/";
    }
    if (!urlRoot.startsWith("/")) {
      urlRoot = "/" + urlRoot;
    }
    if (urlRoot.length() > 1 && urlRoot.endsWith("/")) {
      urlRoot = urlRoot.substring(0, urlRoot.length() - 1);
    }
    return urlRoot;
  }

  private static String parseHostname(@NotNull JsonElement all, @NotNull JsonObject obj) {
    String hostname = GsonUtil.getStringProperty(obj, HOST_NAME);
    if (hostname == null) {
      LOG.warn("Can not parse Karma config.hostname from " + all.toString());
      hostname = "localhost";
    }
    hostname = hostname.toLowerCase();
    return hostname;
  }

  @NotNull
  private static List<String> parseBrowsers(@NotNull JsonObject obj) {
    JsonElement browsersElement = obj.get(BROWSERS);
    if (browsersElement != null && browsersElement.isJsonArray()) {
      JsonArray browsersArray = browsersElement.getAsJsonArray();
      List<String> browsers = Lists.newArrayList();
      for (JsonElement browserElement : browsersArray) {
        String browser = GsonUtil.getAsString(browserElement);
        if (browser != null) {
          browsers.add(browser);
        }
      }
      return browsers;
    }
    return Collections.emptyList();
  }

}
