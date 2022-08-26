package io.shcm.shsupercm.fabric.fletchingtable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.compile.JavaCompile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Settings for Fletching Table
 */
public abstract class FletchingTableExtension {
    private final Project project;

    /**
     * Controls whether the annotation processor should run. When false, enableEntrypoints and enableMixins are ignored.
     */
    public abstract Property<Boolean> getEnableAnnotationProcessor();

    /**
     * Enables annotation processing for @Entrypoint.
     */
    public abstract Property<Boolean> getEnableEntrypoints();

    /**
     * Enables annotation processing for @Mixin.
     */
    public abstract Property<Boolean> getEnableMixins();

    /**
     * Sets the default mixin environment when not overridden by @MixinEnvironment.<br>
     * "mixins" will process mixins for both sides, "client" will process mixins as client mixins, "server" will process mixins as server mixins.
     * "none"(default) will ignore mixins entirely. "auto" will put everything in "mixins" unless one of the targets matches a client/server prefix.
     */
    public abstract Property<String> getDefaultMixinEnvironment();

    /**
     * Sets the prefix required for the "auto" environment to be replaced with "client".
     */
    public abstract Property<String> getAutoMixinEnvironmentClientPrefix();
    /**
     * Sets the prefix required for the "auto" environment to be replaced with "server".
     */
    public abstract Property<String> getAutoMixinEnvironmentServerPrefix();

    public FletchingTableExtension(Project project) {
        this.project = project;
        getEnableAnnotationProcessor().convention(true);

        getEnableEntrypoints().convention(true);
        getEnableMixins().convention(true);

        getDefaultMixinEnvironment().convention("none");
        getAutoMixinEnvironmentClientPrefix().convention("net.minecraft.client");
        getAutoMixinEnvironmentServerPrefix().convention("null");
    }

    protected void writeAPSettings(JavaCompile compileTask) {
        List<String> compilerArgs = compileTask.getOptions().getCompilerArgs();

        compilerArgs.add("-Afletchingtable.entrypoints=" + getEnableEntrypoints().get());
        compilerArgs.add("-Afletchingtable.mixins=" + getEnableMixins().get());
        compilerArgs.add("-Afletchingtable.mixins.default=" + getDefaultMixinEnvironment().get());
        compilerArgs.add("-Afletchingtable.mixins.prefix.client=" + getAutoMixinEnvironmentClientPrefix().get());
        compilerArgs.add("-Afletchingtable.mixins.prefix.server=" + getAutoMixinEnvironmentServerPrefix().get());
    }

    /**
     * Downloads and adds Fungible(by magistermaks) to the running development environment.
     * @see <a href="https://github.com/magistermaks/mod-fungible">github.com/magistermaks/mod-fungible</a>
     * @param tag release tag from <a href="https://github.com/magistermaks/mod-fungible/tags">fungible's github releases page</a>.
     */
    public void fungible(String tag) {
        System.out.println("Fletching Table: Applying fungible by magistermaks");
        try {
            File jarsDir = new File(project.getProjectDir(), ".gradle/fletchingtable/jars");
            jarsDir.mkdirs();

            File fungibleJar = new File(jarsDir, "fungible-" + tag + ".jar");

            if (!fungibleJar.exists()) {
                System.out.println("Fletching Table: Downloading fungible...");
                URL downloadUrl;
                try (InputStreamReader isr = new InputStreamReader(new URL("https://api.github.com/repos/magistermaks/mod-fungible/releases/tags/" + tag).openStream())) {
                    JsonObject tagInfo = new Gson().fromJson(isr, JsonObject.class);
                    JsonArray assets = tagInfo.getAsJsonArray("assets");
                    if (assets == null || assets.size() < 1)
                        throw new Exception("Could not resolve tag.");
                    JsonObject asset = assets.get(0).getAsJsonObject();
                    JsonElement jsonDownloadUrl = asset.get("browser_download_url");
                    if (jsonDownloadUrl == null)
                        throw new Exception("Could not resolve tag.");
                    downloadUrl = new URL(jsonDownloadUrl.getAsString());
                }

                FileUtils.copyURLToFile(downloadUrl, fungibleJar);
            }

            project.getDependencies().add("modRuntimeOnly", project.files(new File(project.getProjectDir(), ".gradle/fletchingtable/jars/fungible-" + tag + ".jar")));
        } catch (Exception e) {
            System.out.println("Could not apply fungible to the running environment with tag \"" + tag + "\"");
            e.printStackTrace();
        }
    }
}
