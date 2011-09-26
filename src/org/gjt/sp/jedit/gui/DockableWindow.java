package org.gjt.sp.jedit.gui;

/**
 * <p>An interface for notifying MOVABLE dockable windows before their docking
 * position is changed. </p>
 * 
 * @author Shlomy Reinstein
 * @version $Id: DockableWindow.java 12504 2008-04-22 23:12:43Z ezust $
 * @since jEdit 4.3pre11
 */

public interface DockableWindow {
	//{{{ Move notification
	/**
	 * Notifies a dockable window before its docking position is changed.
	 * @param newPosition The docking position to which the window is moving.
	 * @since jEdit 4.3pre11
	 */
	void move(String newPosition);
	//}}}
}
