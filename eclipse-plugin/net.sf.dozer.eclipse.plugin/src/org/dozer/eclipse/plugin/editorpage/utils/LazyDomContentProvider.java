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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ufacekit.core.databinding.instance.IInstanceObservedContainer;
import org.eclipse.ufacekit.core.databinding.instance.observable.ILazyObserving;
import org.eclipse.ufacekit.core.databinding.sse.dom.SSEDOMObservables;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Element;

/**
 * ContentProvider for SWT.Table and SWT.Tree that supports LazyObservedValues. 
 * 
 * @author Markus
 *
 */
public class LazyDomContentProvider implements
		IStructuredContentProvider,
		ITreeContentProvider {

	private IInstanceObservedContainer container;
	private ILazyObserving parent;
	private String mainElementName;
	private String elementName;
	private IDOMDocument document;
	
	private IObservableValue observedMainElement;
	private IObservableList observableList;
	private Viewer viewer;
	
	public LazyDomContentProvider(IDOMDocument document, IInstanceObservedContainer container, ILazyObserving parent, String mainElementName, String elementName) {
		this.container = container;
		this.parent = parent;
		this.mainElementName = mainElementName;
		this.elementName = elementName;
		this.document = document;
	}
	
	public IObservableList getObservableList() {
		return observableList;
	}
	
	public void createObservableList() {
		if (mainElementName == null) {
			observableList = SSEDOMObservables.observeDetailList(Realm.getDefault(), (IObservableValue)parent, elementName);
		} else {
			observedMainElement = ObservableUtils.observeLazyValue(
					container, 
					document, 
					parent, 
					mainElementName);

			observableList = SSEDOMObservables.observeDetailList(Realm.getDefault(), observedMainElement, elementName) ;
		}		
	}
	
	public Object[] getElements(Object inputElement) {
		createObservableList();
		
		return observableList.toArray();
	}

	public void dispose() {
		observableList.dispose();
		
		if (observedMainElement != null)
			observedMainElement.dispose();
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer=viewer;
	}

	public Object[] getChildren(Object parentElement) {
		Element parentDomElement = (Element)parentElement;
		
		Element[] domChildren = org.dozer.eclipse.plugin.editorpage.utils.DOMUtils.getElements(parentDomElement, new String[]{"field","field-exclude"});
		
		return domChildren;
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		if (!(element instanceof Element))
			return false;
		
		Element domElement = (Element)element;
		return "mapping".equals(domElement.getNodeName());
	}
	
}
