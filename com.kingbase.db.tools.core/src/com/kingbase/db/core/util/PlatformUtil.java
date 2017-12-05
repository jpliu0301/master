package com.kingbase.db.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

import com.kingbase.db.core.KBCorePlugin;

/**
 * 
 * @author Duke
 *
 */
public class PlatformUtil {

	/**
	 * 打开向导
	 * 
	 * @param wizard
	 * @param selection
	 * @param shell
	 */

	/**
	 * 获得插件下指定路径的文件对象
	 * 
	 * @param path
	 * @return
	 */
	public static File getConfigurationFile(String path) {
		return getConfigurationFile(KBCorePlugin.PLUGIN_ID, path);
	}

	public static File getConfigurationFile(String pluginId, String path) {
		Bundle bundle = Platform.getBundle(pluginId);
		URL fullPathString = BundleUtility.find(bundle, path);
		URL localURL = null;
		try {
			if (fullPathString == null) {
				File newfile = new File(path);
				if (!newfile.exists()) {
					newfile.createNewFile();
				}
				return newfile;
			}
			localURL = Platform.asLocalURL(fullPathString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(localURL.getFile());
		return file;
	}

	/**
	 * 获得工作区路径
	 * 
	 * @return
	 */
	public static String getWorkspaceLocation() {
		return getWorkspaceRoot().getLocation().toString();
	}

	/**
	 * 获得工作区
	 * 
	 * @return
	 */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * 打开编辑器
	 * 
	 * @param editInput
	 * @param editorId
	 */
	public static IEditorPart openEditor(IEditorInput editInput, String editorId) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activityPage = window.getActivePage();
		try {
			IEditorPart editor = activityPage.findEditor(editInput);
			if (editor != null) {
				activityPage.bringToTop(editor);
				return null;
			} else {
				editor = activityPage.openEditor(editInput, editorId);
				return editor;
			}

		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭已经打开的编辑器
	 * 
	 * @param editInput
	 * @param editorId
	 * @param isSave
	 * @return
	 */
	public static boolean closeEditor(IEditorInput editInput, String editorId, boolean isSave) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activityPage = window.getActivePage();
		IEditorPart editor = activityPage.findEditor(editInput);
		if (editor != null) {
			return activityPage.closeEditor(editor, isSave);
		} else {
			return true;
		}
	}



}
