/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.configurable;

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;

/**
 * @author hylke
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 */
@ConfigurableClass
public interface AnnotatedConfigurable<C, D> extends Configurable<C, D> {

    /**
     * Configure the instance using the given configuration.
     *
     * @param config The configuration to use for this instance.
     * @param context the object that defines the context at runtime.
     * @param edtCtx the object that defines the context while editing.
     * @param configEditor optional {@code ConfigEditor} that may be used to
     * access and assign configured contents.
     * @throws ConfigurationException If the configuration can not be loaded.
     */
    @Override
    public default void configure(final JsonElement config, final C context, final D edtCtx, ConfigEditor<?> configEditor) throws ConfigurationException {

        final ContentConfigEditor<?> editor = configEditor instanceof ContentConfigEditor
                ? (ContentConfigEditor<?>) configEditor
                : (ContentConfigEditor<?>) ConfigEditors
                        .buildEditorFromClass(this.getClass(), context, edtCtx)
                        .get();
        editor.setConfig(config);
        editor.setContentsOn(this);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException always
     */
    @Override
    default ConfigEditor<?> getConfigEditor(final C context, final D edtCtx) {
        throw new IllegalStateException(
                "should never be invoked by annotated configuarbles which is either created from annotations or a custom static factory");
    }
}
