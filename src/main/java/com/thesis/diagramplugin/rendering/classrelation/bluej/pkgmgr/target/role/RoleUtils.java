package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

class RoleUtils {
	private RoleUtils() {
	}

	private static final String spaces = "                                 ";

	/**
	 * Get a string of whitespace corresponding to an indentation.
	 */
	public static String getIndentString() {
		int ts = Math.min(4, spaces.length());
		return spaces.substring(0, ts);
	}
}
