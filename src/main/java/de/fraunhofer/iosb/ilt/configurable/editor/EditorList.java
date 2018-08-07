/*
 * Copyright (C) 2017 Fraunhofer IOSB
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
package de.fraunhofer.iosb.ilt.configurable.editor;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.EditorFactory;
import de.fraunhofer.iosb.ilt.configurable.GuiFactoryFx;
import de.fraunhofer.iosb.ilt.configurable.GuiFactorySwing;
import de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper;
import static de.fraunhofer.iosb.ilt.configurable.annotations.AnnotationHelper.csvToReadOnlySet;
import de.fraunhofer.iosb.ilt.configurable.editor.fx.FactoryListFx;
import de.fraunhofer.iosb.ilt.configurable.editor.swing.FactoryListSwing;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An editor for a list of editors, all of the same type.
 *
 * @author Hylke van der Schaaf
 * @param <T> The type of editors that edit the items in the list.
 * @param <U> The type of items in the list.
 */
public class EditorList<U, T extends ConfigEditor<U>> extends EditorDefault<List<U>> implements Iterable<T> {

	/**
	 * Container for repeated EdOptsList annotations.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface EdOptsListList {

		EdOptsList[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Repeatable(EdOptsListList.class)
	public static @interface EdOptsList {

		/**
		 * The class to use as editor for the items in the list. The editor
		 * should also supply an annotation type to use for defining further
		 * parameters. An annotation of this type should also be set on the
		 * field.
		 *
		 * This means you can not nest lists, since that would result in two
		 * EdListOpts annotations on the same field.
		 *
		 * @return The class to use as editor for the list items.
		 */
		Class<? extends ConfigEditor> editor();

		/**
		 * The key of the configuration for the nested ListEditor. This needs to
		 * be set if you want to have a nested List inside another List. Since
		 * you need multiple EdOptsList annotations on the field in this case.
		 * The editorKey of the parent list must match the myKey of the child
		 * list.
		 *
		 * @return The key of the configuration for the nested ListEditor.
		 */
		String editorKey() default "list-2";

		/**
		 * The key of the configuration for this ListEditor. This needs to be
		 * set if you want to have a nested List inside another List. Since you
		 * need multiple EdOptsList annotations on the field in this case. The
		 * editorKey of the parent list must match the myKey of the child list.
		 *
		 * @return The key of the configuration for this ListEditor.
		 */
		String myKey() default "list-1";

		/**
		 * The minimum number of items the list must have.
		 *
		 * @return The minimum number of items the list must have.
		 */
		int minCount() default 0;

		/**
		 * The maximum number of items the list must have. Once the list has
		 * this many items, the user can not add more items to the list. If a
		 * configuration is loaded with more items, only this many items are
		 * loaded, the rest is ignored.
		 *
		 * @return The maximum number of items the list may have.
		 */
		int maxCount() default Integer.MAX_VALUE;

		/**
		 * Flag indicating the editor should be laid out horizontally.
		 *
		 * @return true if the editor should be laid out horizontally.
		 */
		boolean horizontal() default false;

		/**
		 * The text to display next to the "add" button.
		 *
		 * @return The text to display next to the "add" button.
		 */
		String labelText() default "";

		/**
		 * A comma separated, case insensitive list of profile names. This field
		 * is only editable when one of these profiles is active. The "default"
		 * profile is automatically added to the list.
		 *
		 * @return A comma separated, case insensitive list of profile names.
		 */
		String profilesEdit() default "";
	}

	private Object context;
	private Object edtCtx;
	private EditorFactory<T> factory;
	private final List<T> value = new ArrayList<>();
	private int minCount = 0;
	private int maxCount = Integer.MAX_VALUE;
	private boolean horizontal = false;
	private String labelText = "";

	public Set<String> profilesEdit = csvToReadOnlySet("");
	private String profile = DEFAULT_PROFILE_NAME;

	private FactoryListSwing factorySwing;
	private FactoryListFx factoryFx;

	public EditorList() {
	}

	public EditorList(EditorFactory<T> factory) {
		this.factory = factory;
	}

	public EditorList(EditorFactory<T> factory, String label, String description) {
		this.factory = factory;
		setLabel(label);
		setDescription(description);
	}

	/**
	 * The contexts to pass on to item editors. Only needed when using
	 * annotations, since then the factory is not externally supplied.
	 *
	 * @param context the context.
	 * @param edtCtx the edit context.
	 */
	public final void setContexts(final Object context, final Object edtCtx) {
		this.context = context;
		this.edtCtx = edtCtx;
	}

	@Override
	public void initFor(final Field field) {
		initFor(field, "list-1");
	}

	@Override
	public void initFor(final Field field, String key) {
		final EdOptsList[] annotations = field.getAnnotationsByType(EdOptsList.class);
		EdOptsList annotation = null;
		for (EdOptsList a : annotations) {
			if (a.myKey().equalsIgnoreCase(key)) {
				annotation = a;
				break;
			}
		}

		if (annotation == null) {
			throw new IllegalArgumentException("Field must have an EdListOpts annotation to use this editor: " + field.getName());
		}
		minCount = annotation.minCount();
		maxCount = annotation.maxCount();
		horizontal = annotation.horizontal();
		labelText = annotation.labelText();
		profilesEdit = csvToReadOnlySet(annotation.profilesEdit());

		final String editorKey = annotation.editorKey();
		// TODO: find a way to check this cast
		final Class<T> editorClass = (Class<T>) annotation.editor();
		factory = () -> {
			try {
				T editor = AnnotationHelper.createEditor(editorClass, field, context, edtCtx, editorKey);
				return editor;
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new IllegalArgumentException("Can not create new editor.", ex);
			}
		};
	}

	@Override
	public void setConfig(JsonElement config) {
		value.clear();
		if (config != null && config.isJsonArray()) {
			JsonArray asArray = config.getAsJsonArray();
			for (JsonElement subConf : asArray) {
				if (value.size() >= maxCount) {
					break;
				}
				T item = factory.createEditor();
				item.setConfig(subConf);
				item.setProfile(profile);
				value.add(item);
			}
		}
		fillComponent();
	}

	@Override
	public JsonElement getConfig() {
		JsonArray result = new JsonArray();
		for (T item : value) {
			result.add(item.getConfig());
		}
		return result;
	}

	@Override
	public GuiFactorySwing getGuiFactorySwing() {
		if (factoryFx != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factorySwing == null) {
			factorySwing = new FactoryListSwing(this, !horizontal);
			if (!Strings.isNullOrEmpty(labelText)) {
				factorySwing.setText(labelText);
			}
		}
		fillComponent();
		return factorySwing;
	}

	@Override
	public GuiFactoryFx getGuiFactoryFx() {
		if (factorySwing != null) {
			throw new IllegalArgumentException("Can not mix different types of editors.");
		}
		if (factoryFx == null) {
			factoryFx = new FactoryListFx(this, !horizontal);
			if (!Strings.isNullOrEmpty(labelText)) {
				factoryFx.setText(labelText);
			}
		}
		fillComponent();
		return factoryFx;
	}

	private void fillComponent() {
		while (value.size() < minCount) {
			T item = factory.createEditor();
			item.setProfile(profile);
			value.add(item);
		}
		if (factorySwing != null) {
			factorySwing.fillComponent();
		}
		if (factoryFx != null) {
			factoryFx.fillComponent();
		}
	}

	public void addItem() {
		if (value.size() >= maxCount) {
			return;
		}
		final T item = factory.createEditor();
		item.setProfile(profile);
		value.add(item);
		fillComponent();
	}

	public void removeItem(T item) {
		if (value.size() > minCount) {
			value.remove(item);
		}
		fillComponent();
	}

	@Override
	public Iterator<T> iterator() {
		return value.iterator();
	}

	public List<T> getRawValue() {
		return value;
	}

	@Override
	public List<U> getValue() {
		List<U> valList = new ArrayList<>();
		for (T val : value) {
			valList.add(val.getValue());
		}
		return valList;
	}

	@Override
	public void setValue(List<U> value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMinCount() {
		return minCount;
	}

	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setProfilesEdit(String csv) {
		profilesEdit = csvToReadOnlySet(csv);
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile.toLowerCase();
		for (T val : value) {
			val.setProfile(this.profile);
		}
	}

	@Override
	public boolean canEdit() {
		return profilesEdit.contains(profile) && minCount != maxCount;
	}
}
