package de.fraunhofer.iosb.ilt.configurableexample;

import de.fraunhofer.iosb.ilt.configurable.Configurable;

/**
 * The interface that our shapes implement.
 *
 * @author scf
 */
public interface Shape extends Configurable<Void, Void> {

	public void paintMe();

}
