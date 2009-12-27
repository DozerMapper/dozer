package org.dozer.eclipse.plugin.editorpage.utils;

import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.ufacekit.core.databinding.instance.IInstanceObservedContainer;
import org.eclipse.ufacekit.core.databinding.instance.InstanceObservables;
import org.eclipse.ufacekit.core.databinding.instance.observable.ILazyObserving;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObservableUtils {

	/**
	 * Support method for creating chains of LazyObservingValues. 
	 * 
	 * @param container
	 * @param document
	 * @param parentObserver
	 * @param elementName
	 * @return
	 */
	public static IObservableValue observeLazyValue(
			final IInstanceObservedContainer container,
			final IDOMDocument document,
			final IObserving parentObserver,
			final String elementName) {
		
		IObservableValue observableConfigValue = InstanceObservables.observeValue(
			container, 
			new LocatedLazyObserving() {
				public Object getObserved() {
					Element e = (Element)parentObserver.getObserved();
					return DOMUtils.getElement(e, elementName);
				}

				public Object createObserved() {
					Element newChild = document.createElement(elementName);
					Element parentElement = (Element)parentObserver.getObserved();
					
					//Create parent node if necessary
					if (parentObserver instanceof ILazyObserving && parentElement == null)
						parentElement = (Element)((ILazyObserving)parentObserver).createObserved();
					
					Node refChild = getNextExistentElement(parentElement, newChild);
					return parentElement.insertBefore(newChild, refChild);
				}
				
			});	
		
		return observableConfigValue;
	}
	
	/**
	 * Support method for creating a LazyObserver from a parent node.
	 * 
	 * @param container
	 * @param document
	 * @param parent
	 * @param elementName
	 * @return
	 */
	public static ILazyObserving observeLazyValue(
			final IInstanceObservedContainer container,
			final IDOMDocument document,
			final String elementName) {
		
		ILazyObserving observableConfigValue = (ILazyObserving)InstanceObservables.observeValue(
			container, 
			new LocatedLazyObserving() {
				public Object getObserved() {
					return document.getDocumentElement();
				}

				public Object createObserved() {
					Element newChild = document.createElement(elementName);
					return document.appendChild(newChild);
				}			
								
			});	
		
		return observableConfigValue;
	}		
	
}
