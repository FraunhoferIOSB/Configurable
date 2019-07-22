package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.AnnotatedConfigurable;

/**
 * The interface that our shapes implement.
 *
 * @author scf
 */
public interface Shape extends AnnotatedConfigurable<Void, Void> {

	public void paintMe();

}
