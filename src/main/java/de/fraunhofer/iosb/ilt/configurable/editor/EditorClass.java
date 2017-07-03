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

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import java.awt.BorderLayout;
import java.awt.Color;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An editor that wraps a single class. This is like an editorSubclass with only
 * a single implementing class. Useful in EditorList where you need an editor
 * that can create instances of the class it edits.
 *
 * @author Hylke van der Schaaf
 * @param <C> The class type that provides context at runtime.
 * @param <D> The class type that provides context while editing.
 * @param <T> The type of object returned by getValue.
 */
public final class EditorClass<C, D, T> extends EditorDefault<C, D, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorClass.class);
	private final Class<T> clazz;
	private JsonElement classConfig;
	private ConfigEditor classEditor;
	/**
	 * Flag indicating we are in JavaFX mode.
	 */
	private Boolean fx;
	// Swing components
	private JPanel component;
	// FX Nodes
	private BorderPane fxPaneRoot;

	private C context;
	private D edtCtx;

	/**
	 * @param clazz The class to wrap.
	 */
	public EditorClass(final Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void setConfig(final JsonElement classConfig, final C context, final D edtCtx) {
		this.context = context;
		this.edtCtx = edtCtx;
		this.classConfig = classConfig;
	}

	@Override
	public JsonElement getConfig() {
		readComponent();
		return classConfig;
	}

	private void setFx(boolean fxMode) {
		if (fx != null && fx != fxMode) {
			throw new IllegalStateException("Can not switch between swing and FX mode.");
		}
		fx = fxMode;
	}

	@Override
	public Node getNode() {
		setFx(true);
		if (fxPaneRoot == null) {
			createPane();
		}
		return fxPaneRoot;
	}

	@Override
	public JComponent getComponent() {
		setFx(false);
		if (component == null) {
			createComponent();
		}
		return component;
	}

	private void createPane() {
		fxPaneRoot = new BorderPane();
		fillComponent();
	}

	private void createComponent() {
		component = new JPanel(new BorderLayout());
		component.setBorder(new EtchedBorder());
		fillComponent();
	}

	/**
	 * Set the name of the class selected in this editor.
	 */
	private void initClass() {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException exc) {
			LOGGER.warn("Exception instantiating class {}.", clazz.getName());
			LOGGER.trace("Exception instantiating class.", exc);
		}

		if (instance instanceof Configurable) {
			final Configurable confInstance = (Configurable) instance;
			classEditor = confInstance.getConfigEditor(context, edtCtx);
			classEditor.setConfig(classConfig, context, edtCtx);
		} else {
			LOGGER.warn("Class {} is not configurable.", clazz.getName());
			classEditor = new EditorString("", 6);
			classEditor.setConfig(classConfig, context, edtCtx);
		}

		fillComponent();
	}

	private void fillComponent() {
		if (fx == null) {
			return;
		}
		if (classEditor == null) {
			initClass();
			return; // initClass calls fillComponent again.
		}
		if (fx) {
			fxPaneRoot.getChildren().clear();
			if (classEditor == null) {
				final Label noConf = new Label("Nothing to be configured.");
				noConf.setTextFill(javafx.scene.paint.Color.RED);
				fxPaneRoot.setCenter(noConf);
			} else {
				fxPaneRoot.setCenter(classEditor.getNode());
			}
		} else {
			component.removeAll();
			if (classEditor == null) {
				final JLabel noConf = new JLabel("Nothing to be configured.");
				noConf.setForeground(Color.RED);
				component.add(noConf, BorderLayout.CENTER);
			} else {
				component.add(classEditor.getComponent(), BorderLayout.CENTER);
			}
			component.revalidate();
			component.repaint();
		}
	}

	private void readComponent() {
		if (classEditor != null) {
			classConfig = classEditor.getConfig();
		}
	}

	/**
	 * Set the configuration of the selected class.
	 *
	 * @param classConfig the configuration of the selected class.
	 */
	public void setClassConfig(final JsonElement classConfig) {
		this.classConfig = classConfig;
	}

	public JsonElement getClassConfig() {
		return classConfig;
	}

	@Override
	public T getValue() {
		readComponent();
		try {
			final Object instance = clazz.newInstance();

			if (instance instanceof Configurable) {
				final Configurable confInstance = (Configurable) instance;
				confInstance.configure(classConfig, context, edtCtx);
			}
			return (T) instance;
		} catch (InstantiationException | IllegalAccessException | ClassCastException exc) {
			LOGGER.warn("Exception instantiating class {}.", clazz.getName());
			LOGGER.trace("Exception instantiating class.", exc);
			return null;
		}
	}

}
