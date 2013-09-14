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
package org.dozer.eclipse.plugin.editorpage.utils;

import org.dozer.eclipse.plugin.DozerMultiPageEditor;
import org.dozer.eclipse.plugin.DozerPlugin;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.springframework.ide.eclipse.beans.ui.editor.Activator;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.w3c.dom.Element;

import java.util.List;

public class DozerUiUtils {

	private static DozerUiUtils instance;
	
	private IEditorInput editorInput;
	private static FormToolkit toolkit;
	
	private Image classImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_CLASS).createImage();
	private Image interfaceImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_INTERFACE).createImage();
	
	private IProject project;
	private IJavaProject javaProject;
	private DozerMultiPageEditor dozerEditor;
	
	public static DozerUiUtils getInstance() {
		if (instance == null)
			instance = new DozerUiUtils();
		
		return instance;
	}
	
	public IEditorInput getEditorInput() {
		return editorInput;
	}
	
	public static void setToolKit(FormToolkit toolkit) {
		DozerUiUtils.toolkit = toolkit;
	}
	
	public static FormToolkit getToolKit() {
		return DozerUiUtils.toolkit;
	}	
	
	public void setEditorInput(IEditorInput editorInput, FormToolkit toolkit) {
		IEditorInput oldInput = this.editorInput;
		this.editorInput = editorInput;
		DozerUiUtils.setToolKit(toolkit);
		
		if (oldInput != editorInput) {
			init();
		}
	}
	
	public void setMultiEditor(DozerMultiPageEditor dozerEditor) {
		this.dozerEditor = dozerEditor;
	}
	
	private void init() {
		project = getProject();
		javaProject = JavaCore.create(project);
	}
	
	private IProject getProject() {
		IFile file = null;
		if (editorInput instanceof IFileEditorInput) {
			file = ((IFileEditorInput) editorInput).getFile();
		} else
			return null; //Error

		return file.getProject();
	}
	
	public String doOpenClassSelectionDialog(String superType, boolean allowInterfaces) {
		IJavaSearchScope searchScope = null;
	
		IProject project = getProject();
		try {
			IJavaProject javaProject = JavaCore.create(project);
			if (superType != null) {
				IType superTypeType = javaProject.findType(superType);
				if (superTypeType != null) {
					searchScope = SearchEngine.createHierarchyScope(superTypeType);
				} else
					return null; //Error
			}
	
			SelectionDialog dialog = 
				JavaUI.createTypeDialog(
					DozerPlugin.getActiveWorkbenchShell(), 
					PlatformUI.getWorkbench().getProgressService(), 
					searchScope,
					allowInterfaces?IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES:IJavaElementSearchConstants.CONSIDER_CLASSES,
					false);
			dialog.setTitle("Browse...");
			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getResult()[0];
				return type.getFullyQualifiedName('$');
			}
		} catch (Exception e) {
			Logger.logException(e);
		}
		return null;
	}
	
	public static String nullSafeTrimString(String value) {
		if (value == null)
			return "";
		else
			return value.trim();
	}
	
	public Image getImageFromClassName(String className) {
		if (className == null)
			return null;
		
		try {
			IType superTypeType = javaProject.findType(className);
			if (superTypeType == null)
				return null;
			else if (superTypeType.isInterface())
				return interfaceImg;
			else
				return classImg;
		} catch (JavaModelException e) {
			Logger.logException(e);
		}
			
		return null;
	}	
	
	public static Composite createComposite(Composite parent) {
		return toolkit.createComposite(parent);
	}
	
	public static Label createLabel(Composite client, String messagePrefix) {
		//Controls
		Label label = toolkit.createLabel(client, Messages.getString(messagePrefix)); 
		label.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		
		//Format
		TableWrapData td = new TableWrapData();
		label.setLayoutData(td);	
		
		return label;
	}

	public static IObservableValue createText(Composite client, String messagePrefix) {
		Text text = toolkit.createText(client, "", SWT.SINGLE); //$NON-NLS-1$
		text.setBackground(new Color(Display.getCurrent(), 255,255,255));
		text.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		
		//Format
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);	
		
		return SWTObservables.observeText(text, SWT.Modify);		
	}
	
	public static IObservableValue createLabelCheckbox(Composite client, String messagePrefix) {
		createLabel(client, messagePrefix);
		
		Button checkbox = toolkit.createButton(client, "", SWT.CHECK);
		checkbox.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		checkbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		//Format
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		checkbox.setLayoutData(td);	
		
		return SWTObservables.observeSelection(checkbox);			
	}	
	
	public static IObservableValue createLabelText(Composite client, String messagePrefix) {
		createLabel(client, messagePrefix);
		return createText(client, messagePrefix);		
	}	

	public static IObservableValue createLabelCombo(Composite client, String messagePrefix, String[] values) {
		createLabel(client, messagePrefix);
		return createCombo(client, messagePrefix, values);
	}
	
	public static IObservableValue createCombo(Composite client, String messagePrefix, String[] values) {
		CCombo combo = new CCombo(client, SWT.DROP_DOWN | SWT.READ_ONLY
				| SWT.FLAT | SWT.BORDER);
		for (String value : values) {
			combo.add(value);
		}

		toolkit.adapt(combo);
		toolkit.paintBordersFor(combo);
		
		combo.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		
		//Format
		TableWrapData td = new TableWrapData();
		combo.setLayoutData(td);
		
		return SWTObservables.observeSelection(combo);
	}
	
	public static IObservableValue createLabelClassBrowse(Composite client, String messagePrefix, final String superType, final boolean allowInterfaces) {
		createLabel(client, messagePrefix);

		Composite tableClient = toolkit.createComposite(client, SWT.WRAP);
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin=0;
		layout.leftMargin=0;
		layout.rightMargin=0;
		layout.topMargin=0;
		layout.numColumns = 2;
		tableClient.setLayout(layout);
		
		final IObservableValue text = DozerUiUtils.createText(tableClient, messagePrefix);

		Button browseBtn = toolkit.createButton(tableClient, "Browse...", SWT.PUSH);
		browseBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String className = DozerUiUtils.getInstance().doOpenClassSelectionDialog(superType, allowInterfaces);
				if (className != null) {
					text.setValue(className);
					
				}
			}
		});
		TableWrapData td = new TableWrapData();
		browseBtn.setLayoutData(td);
		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		tableClient.setLayoutData(td);
		
		return text;
	}
	
	public static AbstractListViewer createLabelFieldCombobox(Composite client, String messagePrefix) {
		createLabel(client, messagePrefix);
		
		final CCombo combo = new CCombo(client, SWT.FLAT | SWT.BORDER);
		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		//td.maxWidth = 400;
		combo.setLayoutData(td);

		toolkit.adapt(combo);
		toolkit.paintBordersFor(combo);
		
		combo.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		final ComboViewer comboViewer = new ComboViewer(combo);
		
		//Add Focus-Lost Listener so that the entered text gets converted to a IField and gets selected.
		//This way the databinding works
		combo.addFocusListener(new FocusListener() {

			private StringToFieldConverter converter = new StringToFieldConverter(null, comboViewer);			
			
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				//already selected (due to combobox select)
				if (!comboViewer.getSelection().isEmpty())
					return;
				
				converter.setExistingFields((List<IField>)comboViewer.getInput());
				Object convertedText = converter.convert(combo.getText());
				comboViewer.setSelection(new StructuredSelection(convertedText));
			}
			
		});		
		
		comboViewer.setContentProvider(new ObservableListContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				//FIXME never gets invoced, as a CCombo only shows text
				IMethod method = (IMethod)element;
				Image image = null;
				try {
					image = Activator.getDefault().getJavaElementLabelProvider().getImageLabel(
							method, method.getFlags() | JavaElementImageProvider.SMALL_ICONS);
				} catch (Exception e) {
					Logger.logException(e);
				}
				
				return image;
			}
			
			@Override
			public String getText(Object element) {
				IField field = (IField)element;
				StringBuilder buf = new StringBuilder();

				//Copied from org.springframework.ide.eclipse.beans.ui.editor.contentassist.MethodContentAssistCalculator
				String fieldName = field.getElementName();
				// add method name
				String replaceText = fieldName;
				buf.append(replaceText);
				
				String displayText = buf.toString();
				return displayText;
			}
			
		});
		
		return comboViewer;
	}

	public static AbstractListViewer createLabelMethodCombobox(Composite client, String messagePrefix) {
		createLabel(client, messagePrefix);
		
		final CCombo combo = new CCombo(client, SWT.FLAT | SWT.BORDER);
		TableWrapData td = new TableWrapData();
		td.grabHorizontal = true;
		//td.maxWidth = 400;
		combo.setLayoutData(td);

		toolkit.adapt(combo);
		toolkit.paintBordersFor(combo);
		
		combo.setToolTipText(Messages.getString(messagePrefix + "_hint")); //$NON-NLS-1$
		
		final ComboViewer comboViewer = new ComboViewer(combo);
		
		//Add Focus-Lost Listener so that the entered text gets converted to a IMethod and gets selected.
		//This way the databinding works
		combo.addFocusListener(new FocusListener() {

			private StringToMethodConverter converter = new StringToMethodConverter(null, comboViewer);
			
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				//already selected (due to combobox select)
				if (!comboViewer.getSelection().isEmpty())
					return;
				
				converter.setExistingMethods((List<IMethod>)comboViewer.getInput());
				Object convertedText = converter.convert(combo.getText());
				comboViewer.setSelection(new StructuredSelection(convertedText));
			}
			
		});		
		
		comboViewer.setContentProvider(new ObservableListContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				//FIXME never gets invoced, as a CCombo only shows text
				IMethod method = (IMethod)element;
				Image image = null;
				try {
					image = Activator.getDefault().getJavaElementLabelProvider().getImageLabel(
							method, method.getFlags() | JavaElementImageProvider.SMALL_ICONS);
				} catch (Exception e) {
					Logger.logException(e);
				}
				
				return image;
			}
			
			@Override
			public String getText(Object element) {
				IMethod method = (IMethod)element;
				StringBuilder buf = new StringBuilder();

				//Copied from org.springframework.ide.eclipse.beans.ui.editor.contentassist.MethodContentAssistCalculator
				String methodName = method.getElementName();
				// add method name
				String replaceText = methodName;
				buf.append(replaceText);

				String[] parameterNames = new String[]{"?"};
				String[] parameterTypes = new String[]{"?"};
				String className = "?";
				String returnType = "?";
				try {
					parameterNames = method.getParameterNames();
					parameterTypes = JdtUtils.getParameterTypesString(method);
					returnType = JdtUtils.getReturnTypeString(method, true);
					className = method.getParent().getElementName();
				} catch (JavaModelException e) {
					//do nothing
				}
				
				
				// add method parameters
				if (parameterTypes.length > 0 && parameterNames.length > 0) {
					buf.append(" (");
					for (int i = 0; i < parameterTypes.length; i++) {
						buf.append(parameterTypes[i]);
						buf.append(' ');
						buf.append(parameterNames[i]);
						if (i < (parameterTypes.length - 1)) {
							buf.append(", ");
						}
					}
					buf.append(") ");
				}
				else {
					buf.append("() ");
				}

				// add return type
				if (returnType != null) {
					buf.append(Signature.getSimpleName(returnType));
					buf.append(" - ");
				}
				else {
					buf.append(" void - ");
				}

				// add class name
				buf.append(className);			
				
				String displayText = buf.toString();
				return displayText;
			}
			
		});
		
		return comboViewer;
	}
	
	public static TableViewerSelectionListener createAddClassSelectionListener(
			IDOMModel model, 
			final String elementName, 
			final String lookupClass) {
		
		TableViewerSelectionListener listener = new TableViewerSelectionListener(model) {

			@Override
			protected void invoceSelected(Object selected) {
				final String className = DozerUiUtils.getInstance().doOpenClassSelectionDialog(lookupClass, true);
				if (className != null) {
					BusyIndicator.showWhile(
						Display.getCurrent(), 
						new Runnable() {
							public void run() {
								Element element = getModel().getDocument().createElement(elementName);
								
								org.eclipse.core.dom.utils.DOMUtils.setTextContent(element, className);
								
								IObservableList values = (IObservableList)getTableViewer().getInput();
								values.add(element);							
							}
					});					
				}
			}
			
		};
		
		return listener;
	}
	
	public static TableViewerSelectionListener createDeleteSelectionListener(IDOMModel model) {
		TableViewerSelectionListener listener = new TableViewerSelectionListener(model) {

			@Override
			protected void invoceSelected(Object selected) {
				Element selection = (Element)selected;
				Element parentNode = (Element)selection.getParentNode();
				
				IObservableList values = (IObservableList)getTableViewer().getInput();
				values.remove(selection);
				
				//if last node had been deleted, delete parent
				if (DOMUtils.getElements(parentNode).length == 0) {
					Element parentParentNode = (Element)parentNode.getParentNode();
					
					parentNode.getParentNode().removeChild(parentNode);	
					
					if (DOMUtils.getElements(parentParentNode).length == 0) {
						parentParentNode.getParentNode().removeChild(parentParentNode);					
					}
				}				
			}
			
		};
		
		return listener;
	}
	
	public static Section createTwistieSection(
			Composite parentClient,
			String messagePrefix) {
		Section section = toolkit.createSection(parentClient,
				ExpandableComposite.TITLE_BAR | 
				ExpandableComposite.TWISTIE);
		section.setText(Messages.getString(messagePrefix)); 
		section.setDescriptionControl(toolkit.createLabel(section, Messages.getString(messagePrefix+"_sdesc")));
		section.setExpanded(true);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.BEGINNING;
		section.setLayoutData(gd);

		return section;
	}
	
	/**
	 * Highlights the given element in the sourceview of the document
	 * 
	 * @param element The element that should be highlighted
	 */
	public void jumpToElement(Element element) {
		ElementImpl elementImpl = (ElementImpl)element;

		dozerEditor.getSourceEditor().setHighlightRange(elementImpl.getStartOffset()+1, elementImpl.getLength(), true);
		dozerEditor.changeToSourcePage();
	}
	
	public IDOMDocument getDomDocument(ITextEditor editor) {
		IDocument document = editor.getDocumentProvider().getDocument(editorInput);
		IDOMModel sModel = (IDOMModel)org.eclipse.wst.sse.core.StructuredModelManager.getModelManager().getExistingModelForRead(document);
		return sModel.getDocument();
	}

}
