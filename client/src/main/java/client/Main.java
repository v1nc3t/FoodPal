/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.services.LocaleManager;
import com.google.inject.Injector;
import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Injector INJECTOR;
    private static MyFXML FXML;

    private client.config.ConfigManager configManager;

    public static client.config.ConfigManager getConfigManager() {
        return INJECTOR.getInstance(client.config.ConfigManager.class);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // load config
        String cfgPath = getParameters().getNamed().get("cfg");
        configManager = new client.config.ConfigManager(cfgPath);
        configManager.load();

        INJECTOR = createInjector(new MyModule(configManager));
        FXML = new MyFXML(INJECTOR);

        LocaleManager localeManager = INJECTOR.getInstance(LocaleManager.class);
        localeManager.init(configManager);

        var bundle = localeManager.getCurrentBundle();

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, " +
                    "but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var pair = FXML.load(client.scenes.MainApplicationCtrl.class, bundle,
                "client", "scenes", "MainApplication.fxml");

        Parent root = pair.getValue();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/client/styles/light.css").toExternalForm()
        );

        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(scene);
        // primaryStage.setResizable(false);
        primaryStage.setMinWidth(1300);
        primaryStage.setMinHeight(480);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (configManager != null) {
            configManager.save(); // save config on exit
        }
    }
}