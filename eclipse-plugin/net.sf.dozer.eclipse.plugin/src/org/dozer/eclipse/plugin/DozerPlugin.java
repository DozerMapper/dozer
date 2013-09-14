/**
 * Copyright 2005-2013 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.eclipse.plugin;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DozerPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static DozerPlugin plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;

	private FormColors formColors;
	public static final String IMG_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	public static final String IMG_VERTICAL = "vertical"; //$NON-NLS-1$

	public static final String IMG_CONFIG_PAGE = "config"; //$NON-NLS-1$
	public static final String IMG_MAPPINGS_PAGE = "mappings"; //$NON-NLS-1$

	public static final String IMG_PACKAGE = "package"; //$NON-NLS-1$
	public static final String IMG_CLASS = "class"; //$NON-NLS-1$
	public static final String IMG_INTERFACE = "interface"; //$NON-NLS-1$
	public static final String IMG_ATTRIBUTE = "attribute"; //$NON-NLS-1$

	public static final String IMG_CUSTOMCONVERTER = "customconverter"; //$NON-NLS-1$
	public static final String IMG_BYREF = "byref"; //$NON-NLS-1$
	public static final String IMG_EXCLUDE = "exclude"; //$NON-NLS-1$
	public static final String IMG_ONEWAY = "oneway"; //$NON-NLS-1$

	public static final String IMG_COLLAPSE_ALL = "collapse"; //$NON-NLS-1$
	public static final String IMG_EXPAND_ALL = "expand"; //$NON-NLS-1$

	public static final String IMG_ADD_CLASSMAPPING = "add_classmapping"; //$NON-NLS-1$
	public static final String IMG_ADD_FIELDMAPPING = "add_fieldmapping"; //$NON-NLS-1$
	public static final String IMG_ADD_FIELDEXCLUDEMAPPING = "add_fieldexcludemapping";

	/**
	 * The constructor.
	 */
	public DozerPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("org.dozer.eclipse.plugin.DozerPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_CUSTOMCONVERTER, "cc.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_BYREF, "byref.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_EXCLUDE, "exclude.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_ONEWAY, "oneway.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_HORIZONTAL, "th_horizontal.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_VERTICAL, "th_vertical.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_CONFIG_PAGE, "config.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_MAPPINGS_PAGE, "mappings.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_CLASS, "class.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_INTERFACE, "interface.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_PACKAGE, "package_obj.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_ATTRIBUTE, "attribute.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_COLLAPSE_ALL, "collapseall.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_EXPAND_ALL, "expandall.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_ADD_CLASSMAPPING, "class.gif", "add.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_ADD_FIELDMAPPING,
				"attribute.gif", "add.gif"); //$NON-NLS-1$
		registerImage(registry, IMG_ADD_FIELDEXCLUDEMAPPING,
				"exclude.gif", "add.gif"); //$NON-NLS-1$

	}

	private void registerImage(ImageRegistry registry, String key,
			String fileName, String overlayFileName) {
		try {
			IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
			URL url = find(path);
			if (url != null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);

				if (overlayFileName != null) {
					path = new Path("icons/" + overlayFileName); //$NON-NLS-1$
					url = find(path);
					ImageDescriptor descOverlay = ImageDescriptor
							.createFromURL(url);
					Image overlayImg = createOverlayImg(desc.createImage(),
							descOverlay.createImage());
					desc = ImageDescriptor.createFromImage(overlayImg);
				}
				registry.put(key, desc);
			}
		} catch (Exception e) {
		}
	}

	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
		registerImage(registry, key, fileName, null);
	}

	private Image createOverlayImg(Image mainImg, Image overlay) {
		Image resultImg = new Image(Display.getCurrent(), mainImg,
				SWT.IMAGE_COPY);

		GC gc = new GC(resultImg);
		try {
			gc.drawImage(overlay, -2, 1);
		} finally {
			gc.dispose();
		}

		return resultImg;
	}

	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DozerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DozerPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	@Override
	public void start(BundleContext context)throws Exception {
		super.start(context);
		
	}
	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			if (formColors != null) {
				formColors.dispose();
				formColors = null;
			}
		} finally {
			super.stop(context);
		}
	}

	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	public ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
	}
}