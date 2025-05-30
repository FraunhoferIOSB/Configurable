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
import de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;

/**
 * @author hylke
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 */
public abstract class AbstractConfigurable<C, D> implements Configurable<C, D> {

    private transient EditorMap<?> editor;

    @Override
    public void configure(JsonElement config, C context, D edtCtx, ConfigEditor<?> configEditor) throws ConfigurationException {
        if (configEditor instanceof ContentConfigEditor<?>) {
            ((ContentConfigEditor<?>) configEditor).setContentsOn(this);
            return;
        }

        getConfigEditor(context, edtCtx).setConfig(config);
        editor.setContentsOn(this);
    }

    @Override
    public EditorMap<?> getConfigEditor(C context, D edtCtx) {
        if (editor == null) {
            editor = AnnotationHelper.generateEditorFromAnnotations(this, context, edtCtx).get();
        }
        return editor;
    }

}
