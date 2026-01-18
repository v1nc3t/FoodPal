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

import client.scenes.AddIngredientCtrl;
import client.scenes.AddRecipeCtrl;
import client.scenes.MainApplicationCtrl;
import client.scenes.SidebarListCtrl;
import client.services.LocaleManager;
import client.services.RecipeManager;
import client.utils.IServerUtils;
import client.utils.ServerUtils;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {

    private final client.config.ConfigManager configManager;

    public MyModule(client.config.ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(MainApplicationCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddRecipeCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddIngredientCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SidebarListCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LocaleManager.class).in(Scopes.SINGLETON);
        binder.bind(RecipeManager.class).in(Scopes.SINGLETON);
        binder.bind(client.services.ShoppingListManager.class).in(Scopes.SINGLETON);
        binder.bind(client.config.ConfigManager.class).toInstance(configManager);
        binder.bind(IServerUtils.class).to(ServerUtils.class).in(Scopes.SINGLETON);
    }
}