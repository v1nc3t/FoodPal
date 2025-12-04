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
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Injector;

import client.config.ConfigManager;
import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final String BUNDLE_NAME = "client.language";
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private ConfigManager configManager;

    public static ConfigManager getConfigManager() {
        return INJECTOR.getInstance(ConfigManager.class);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // load config
        String cfgPath = getParameters().getNamed().get("cfg");
        configManager = new ConfigManager(cfgPath);

        configManager.load();


        var bundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);

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

        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        /*
        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, add);
        */
    }

    @Override
    public void stop() {
        configManager.save();       // save config on exit
    }
}