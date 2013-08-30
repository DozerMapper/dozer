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
package org.dozer.eclipse.plugin.editorpage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dozer.eclipse.plugin.editorpage.pages.DozerConfigurationEditorPage;
import org.dozer.eclipse.plugin.editorpage.pages.DozerMappingEditorPage;
import org.dozer.eclipse.plugin.editorpage.pages.composites.ConfigurationOptionComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.FieldOptionComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.MappingClassComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.MappingFieldComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.ObservableSingleSelectionObject;
import org.dozer.eclipse.plugin.editorpage.utils.DOMUtils;
import org.dozer.eclipse.plugin.editorpage.utils.ObservableUtils;
import org.dozer.eclipse.plugin.editorpage.utils.StringToFieldConverter;
import org.dozer.eclipse.plugin.editorpage.utils.StringToMethodConverter;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ufacekit.core.databinding.instance.IInstanceObservedContainer;
import org.eclipse.ufacekit.core.databinding.instance.InstanceObservables;
import org.eclipse.ufacekit.core.databinding.instance.observable.ILazyObserving;
import org.eclipse.ufacekit.core.databinding.sse.dom.DOMModelInstanceObservedContainer;
import org.eclipse.ufacekit.core.databinding.sse.dom.SSEDOMObservables;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.springframework.ide.eclipse.core.java.FlagsMethodFilter;
import org.springframework.ide.eclipse.core.java.IMethodFilter;
import org.springframework.ide.eclipse.core.java.Introspector;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DozerModelManager {

	class JavaMethodValueChangeListener implements IValueChangeListener {

		private ClassMethodHolder methodHolder;
		private MappingClassComposite mappingClassComposite;
		private MappingFieldComposite mappingFieldComposite;
		
		public JavaMethodValueChangeListener(ClassMethodHolder methodHolder, MappingClassComposite mappingClassComposite) {
			super();
			
			this.methodHolder = methodHolder;
			this.mappingClassComposite = mappingClassComposite;
		}
		
		public JavaMethodValueChangeListener(ClassMethodHolder methodHolder, MappingFieldComposite mappingFieldComposite) {
			super();
			
			this.methodHolder = methodHolder;
			this.mappingFieldComposite = mappingFieldComposite;
		}		
		
		public void handleValueChange(ValueChangeEvent event) {
			String className = (String)event.diff.getNewValue();
			refresh(className);
		}
		
		public void refresh(String className) {
			IFile file = getFile();
			IProject project = file.getProject();
			IType type = JdtUtils.getJavaType(project, className);
			
			methodHolder.clear();
			
			if (type != null) {
				IMethodFilter filter = null;			
				filter = new FlagsMethodFilter(FlagsMethodFilter.NOT_INTERFACE
						| FlagsMethodFilter.NOT_CONSTRUCTOR
						| FlagsMethodFilter.PUBLIC
						| FlagsMethodFilter.NOT_VOID);				
				for (IMethod method : Introspector.findAllMethods(type, filter)) {
					methodHolder.getGetMethods().add(method);
				}		
				
				filter = new FlagsMethodFilter(FlagsMethodFilter.NOT_INTERFACE
						| FlagsMethodFilter.NOT_CONSTRUCTOR
						| FlagsMethodFilter.PUBLIC);
				
				for (IMethod method : Introspector.findAllMethods(type, filter)) {
					try {
						if (method.getParameterNames().length > 0)
							methodHolder.getSetMethods().add(method);
					} catch (JavaModelException e) {
						//skip
					}
				}					
				
				filter = new FlagsMethodFilter(FlagsMethodFilter.NOT_INTERFACE
						| FlagsMethodFilter.STATIC
						| FlagsMethodFilter.PUBLIC);				
				for (IMethod method : Introspector.findAllMethods(type, filter)) {
					methodHolder.getCreateMethods().add(method);
				}
				
				try {
					methodHolder.getProperties().addAll(Arrays.asList(type.getFields()));
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//FIXME
			if (mappingClassComposite != null) {
				mappingClassComposite.getMapGetComboViewer().setInput(methodHolder.getGetMethods());
				//mappingClassComposite.getMapGetComboViewer().getControl().redraw();
				mappingClassComposite.getMapSetComboViewer().setInput(methodHolder.getSetMethods());
				//mappingClassComposite.getMapSetComboViewer().getControl().redraw();
				mappingClassComposite.getCreateComboViewer().setInput(methodHolder.getCreateMethods());
				//mappingClassComposite.getCreateComboViewer().getControl().redraw();
			} else if (mappingFieldComposite != null) {
				mappingFieldComposite.getMapGetComboViewer().setInput(methodHolder.getGetMethods());
				//mappingClassComposite.getMapGetComboViewer().getControl().redraw();
				mappingFieldComposite.getMapSetComboViewer().setInput(methodHolder.getSetMethods());
				//mappingClassComposite.getMapSetComboViewer().getControl().redraw();
				mappingFieldComposite.getCreateComboViewer().setInput(methodHolder.getCreateMethods());
				
				mappingFieldComposite.getGetComboViewer().setInput(methodHolder.getGetMethods());
				mappingFieldComposite.getSetComboViewer().setInput(methodHolder.getSetMethods());
				
				mappingFieldComposite.getFieldComboViewer().setInput(methodHolder.getProperties());
			}
		}
		
	};
	
	public class ClassMethodHolder {
		private List<IMethod> getMethods = new WritableList();
		private List<IMethod> setMethods = new WritableList();
		private List<IMethod> createMethods = new WritableList();
		private List<IField> properties = new WritableList();
		
		public void clear() {
			getMethods.clear();
			setMethods.clear();
			createMethods.clear();
			properties.clear();
		}
		public List<IMethod> getGetMethods() {
			return getMethods;
		}
		public void setGetMethods(List<IMethod> getMethods) {
			this.getMethods = getMethods;
		}
		public List<IMethod> getSetMethods() {
			return setMethods;
		}
		public void setSetMethods(List<IMethod> setMethods) {
			this.setMethods = setMethods;
		}
		public List<IMethod> getCreateMethods() {
			return createMethods;
		}
		public void setCreateMethods(List<IMethod> createMethods) {
			this.createMethods = createMethods;
		}
		public List<IField> getProperties() {
			return properties;
		}
		public void setProperties(List<IField> properties) {
			this.properties = properties;
		}
	}	
	
	//JFace Databinding
	private DataBindingContext dataBindingContext = new DataBindingContext();
	
	private IDOMModel model;	
	private IDOMDocument document;
	private IFile file;
	
	private IInstanceObservedContainer container;
	private List<Binding> mappingBindingList = new ArrayList<Binding>(10);
	private ILazyObserving mappings;
	
	private DozerConfigurationEditorPage configurationPage;
	private DozerMappingEditorPage mappingPage;
	
	private IValueChangeListener mappingListRefreshChangeListener;
		
	private ClassMethodHolder classAMethodHolder = new ClassMethodHolder();
	private ClassMethodHolder classBMethodHolder = new ClassMethodHolder();

	
	/**
	 * Refreshes or created the binding to the controls.
	 * 
	 * @param input
	 * @param configurationPage
	 * @param mappingPage
	 */
	public void createBindings(DozerConfigurationEditorPage configurationPage, DozerMappingEditorPage mappingPage) {
		mappings = 
			ObservableUtils.observeLazyValue(
					container, 
					document,
					"mappings");
		
		this.configurationPage = configurationPage;
		this.mappingPage = mappingPage;
		
		createConfigurationBindings();
		createMappingListBindings();
	}
	
	private void createConfigurationBindings() {
		//configuration node
		ILazyObserving observedConfiguration = 
			(ILazyObserving)ObservableUtils.observeLazyValue(
					container, 
					document, 
					mappings,
					"configuration");
		
		//from dtd:
		//stop-on-errors?, date-format?, wildcard?, trim-strings?, bean-factory?, relationship-type?, custom-converters*, copy-by-references*, allowed-exceptions* ) >
		
		bindValue(document, observedConfiguration, "stop-on-errors", configurationPage.getStopOnErrors());
		bindValue(document, observedConfiguration, "date-format", configurationPage.getDateFormat());
		bindValue(document, observedConfiguration, "wildcard", configurationPage.getWildcard());
		bindValue(document, observedConfiguration, "trim-strings", configurationPage.getTrimStrings());
		bindValue(document, observedConfiguration, "bean-factory", configurationPage.getBeanFactory());
		bindValue(document, observedConfiguration, "relationship-type", configurationPage.getRelationshipType());
		
		bindList(observedConfiguration, "allowed-exceptions", "exception", configurationPage.getAllowedExceptions());
		bindList(observedConfiguration, "custom-converters", "converter", configurationPage.getCustomConverters());
		bindList(observedConfiguration, "copy-by-references", "copy-by-reference",configurationPage.getCopyByReferences());
	}
	
	private void createMappingListBindings() {
		bindTree(mappings, null, "mapping", mappingPage.getDozerMappingListBlock().getMappings());		
	}
	
	public void createMappingBindings(
			ConfigurationOptionComposite configComposite, 
			MappingClassComposite classAComposite,
			MappingClassComposite classBComposite,
			final Element mapping) {
		
		IObserving observingMapping = new IObserving() {
			public Object getObserved() {
				return mapping;
			}
		};
		
		//mapping node
		IObservableValue observedMapping = InstanceObservables.observeValue(
				container, observingMapping);
		
		//remove existing bindings
		for (Binding binding : mappingBindingList) {
			binding.dispose();
		}
		
		mappingBindingList.add(bindAttr(document, observedMapping, "stop-on-errors", configComposite.getStopOnErrors()));
		mappingBindingList.add(bindAttr(document, observedMapping, "date-format", configComposite.getDateFormat()));
		mappingBindingList.add(bindAttr(document, observedMapping, "wildcard", configComposite.getWildcard()));
		mappingBindingList.add(bindAttr(document, observedMapping, "trim-strings", configComposite.getTrimStrings()));
		mappingBindingList.add(bindAttr(document, observedMapping, "bean-factory", configComposite.getBeanFactory()));
		mappingBindingList.add(bindAttr(document, observedMapping, "relationship-type", configComposite.getRelationshipType()));		
		mappingBindingList.add(bindAttr(document, observedMapping, "type", configComposite.getMappingType()));
		mappingBindingList.add(bindAttr(document, observedMapping, "map-null", configComposite.getMapNull()));
		mappingBindingList.add(bindAttr(document, observedMapping, "map-empty-string", configComposite.getMapEmpty()));
		mappingBindingList.add(bindAttr(document, observedMapping, "map-id", configComposite.getMapId()));

		//refresh tableviewer if classname changes
		mappingListRefreshChangeListener = new IValueChangeListener() {			
			public void handleValueChange(ValueChangeEvent event) {
				mappingPage.getDozerMappingListBlock().getMappings().refresh();
			}			
		};		
		
		createMappingClassBindings("class-a", observingMapping, classAComposite, classAMethodHolder);
		createMappingClassBindings("class-b", observingMapping, classBComposite, classBMethodHolder);
		
		dataBindingContext.updateTargets();
	}
	
	private void createMappingClassBindings(String elementName, IObserving observingMapping, MappingClassComposite classComposite, ClassMethodHolder methodHolder) {
		final IObservableValue observedElement = 
			ObservableUtils.observeLazyValue(
					container, 
					document, 
					observingMapping, 
					elementName);
		
		Binding classBinding = bindValue(document, observedElement, classComposite.getClassName());
		IObservableValue observedModel = (IObservableValue)classBinding.getModel();
		
		//refresh tableviewer if classname changes
		observedModel.addValueChangeListener(mappingListRefreshChangeListener);
		//calculate Methods for class
		JavaMethodValueChangeListener jmvcl = new JavaMethodValueChangeListener(methodHolder, classComposite);
		observedModel.addValueChangeListener(jmvcl);
		
		//initial refresh
		if(observedModel.getValue()!=null){
			jmvcl.refresh(observedModel.getValue().toString());
		}
		
		mappingBindingList.add(classBinding);
		mappingBindingList.add(bindAttr(document, observedElement, "bean-factory", classComposite.getBeanFactory()));
		mappingBindingList.add(bindAttr(document, observedElement, "factory-bean-id", classComposite.getBeanFactoryId()));
		mappingBindingList.add(bindAttr(document, observedElement, "map-null", classComposite.getMapNull()));
		mappingBindingList.add(bindAttr(document, observedElement, "map-empty-string", classComposite.getMapEmpty()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "map-get-method", classComposite.getMapGetMethod(), methodHolder.getGetMethods()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "map-set-method", classComposite.getMapSetMethod(), methodHolder.getSetMethods()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "create-method", classComposite.getCreateMethod(), methodHolder.getCreateMethods()));
	}
	
	public void createFieldBindings(
			FieldOptionComposite configComposite, 
			MappingFieldComposite fieldAComposite,
			MappingFieldComposite fieldBComposite,
			final Element field) {
			
		IObserving observingField = new IObserving() {
			public Object getObserved() {
				return field;
			}
		};
		
		//field node
		IObservableValue observedField = InstanceObservables.observeValue(container, observingField);
		
		//remove existing bindings
		for (Binding binding : mappingBindingList) {
			binding.dispose();
		}
		
		if (!configComposite.isExcluded()) {
			mappingBindingList.add(bindAttr(document, observedField, "custom-converter", configComposite.getCustomConverter()));
			mappingBindingList.add(bindAttr(document, observedField, "custom-converter-id", configComposite.getCustomConverterId()));
			mappingBindingList.add(bindAttr(document, observedField, "custom-converter-param", configComposite.getCustomConverterParam()));
			mappingBindingList.add(bindAttr(document, observedField, "copy-by-reference", configComposite.getCopyByReference()));
			mappingBindingList.add(bindAttr(document, observedField, "remove-orphans", configComposite.getRemoveOrphans()));
			mappingBindingList.add(bindAttr(document, observedField, "relationship-type", configComposite.getRelationshipType()));		
			mappingBindingList.add(bindAttr(document, observedField, "map-id", configComposite.getMapId()));
		}
		mappingBindingList.add(bindAttr(document, observedField, "type", configComposite.getMappingType()));

		//refresh tableviewer if classname changes
		mappingListRefreshChangeListener = new IValueChangeListener() {			
			public void handleValueChange(ValueChangeEvent event) {
				mappingPage.getDozerMappingListBlock().getMappings().refresh();
			}			
		};		
		
		createMappingFieldBindings("a", observingField, fieldAComposite, classAMethodHolder);
		createMappingFieldBindings("b", observingField, fieldBComposite, classBMethodHolder);
		
		dataBindingContext.updateTargets();
	}	
	
	private void createMappingFieldBindings(String elementName, IObserving observingField, MappingFieldComposite fieldComposite, ClassMethodHolder methodHolder) {
		final IObservableValue observedElement = 
			ObservableUtils.observeLazyValue(
					container, 
					document, 
					observingField, 
					elementName);
		
		final Element mapping = (Element)((Element)observingField.getObserved()).getParentNode(); 
		
		IObserving observingMapping = new IObserving() {
			public Object getObserved() {
				return mapping;
			}
		};
		final IObservableValue observedMappingNode = 
			ObservableUtils.observeLazyValue(
					container, 
					document, 
					observingMapping, 
					"class-"+elementName);		
		IObservableValue observedMapping = SSEDOMObservables.observeDetailCharacterData(Realm.getDefault(), observedMappingNode);
		
		//calculate Methods for class
		JavaMethodValueChangeListener jmvcl = new JavaMethodValueChangeListener(methodHolder, fieldComposite);
		observedMapping.addValueChangeListener(jmvcl);
		
		//initial refresh
		jmvcl.refresh(observedMapping.getValue().toString());		
		
		mappingBindingList.add(bindFieldValue(document, observedElement, fieldComposite.getFieldName(), methodHolder.getProperties()));
		mappingBindingList.add(bindAttr(document, observedElement, "date-format", fieldComposite.getDateFormat()));
		mappingBindingList.add(bindAttr(document, observedElement, "type", fieldComposite.getType()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "set-method", fieldComposite.getSetMethod(), methodHolder.getSetMethods()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "get-method", fieldComposite.getGetMethod(), methodHolder.getGetMethods()));
		mappingBindingList.add(bindAttr(document, observedElement, "key", fieldComposite.getKey()));
		mappingBindingList.add(bindAttr(document, observedElement, "is-accessible", fieldComposite.getIsAccessible()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "map-get-method", fieldComposite.getMapGetMethod(), methodHolder.getGetMethods()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "map-set-method", fieldComposite.getMapSetMethod(), methodHolder.getSetMethods()));
		mappingBindingList.add(bindMethodAttr(document, observedElement, "create-method", fieldComposite.getCreateMethod(), methodHolder.getCreateMethods()));
	}	
	
	public void updateUI(DozerConfigurationEditorPage configurationPage, DozerMappingEditorPage mappingPage) {
		Document document = model.getDocument();

		if (!configurationPage.getAllowedExceptions().getTable().isDisposed())
			configurationPage.getAllowedExceptions().refresh();
		if (!configurationPage.getCustomConverters().getTable().isDisposed())
			configurationPage.getCustomConverters().refresh();
		if (!configurationPage.getCopyByReferences().getTable().isDisposed())
			configurationPage.getCopyByReferences().refresh();
		
		if (!mappingPage.getDozerMappingListBlock().getMappings().getTree().isDisposed())
			mappingPage.getDozerMappingListBlock().getMappings().refresh();
		//dataBindingContext.updateTargets();
	}		

	private Binding bindFieldValue(IDOMDocument document, final IObservableValue observedElement, ObservableSingleSelectionObject observedView, List<IField> existingFields) {
		IObservableValue observedValue = SSEDOMObservables.observeDetailCharacterData(Realm.getDefault(), observedElement);
		
		//do the binding
		return dataBindingContext.bindValue(
				observedView.getValue(),
				observedValue,
				new UpdateValueStrategy().setConverter(new Converter(IMethod.class, String.class) {
					
					public Object convert(Object fromObject) {
						IField field = (IField)fromObject;
						String value = null;
						
						if (field != null)
							value = field.getElementName();
						
						return value;
					}

				}).setBeforeSetValidator(new IValidator() {

					public IStatus validate(Object value) {
						//dont set value if it is null. Thus no parent node gets created.
						//The parent node has just been deleted in converter
						return value == null ? Status.CANCEL_STATUS : Status.OK_STATUS;
					}
					
				}),
				new UpdateValueStrategy().setConverter(
						new StringToFieldConverter(
								existingFields,
								observedView.getViewer()
								)
						));			
	}

	private Binding bindMethodAttr(IDOMDocument document, final IObservableValue parent, final String attrName, ObservableSingleSelectionObject observedView, List<IMethod> existingMethods) {
		IObservableValue observedValue = SSEDOMObservables.observeDetailAttrValue(Realm.getDefault(), parent, attrName);
		
		//do the binding
		return dataBindingContext.bindValue(
				observedView.getValue(),
				observedValue,
				new UpdateValueStrategy().setConverter(new Converter(IMethod.class, String.class) {
					
					public Object convert(Object fromObject) {
						IMethod method = (IMethod)fromObject;
						String value = null;
						
						if (method != null)
							value = method.getElementName();
						
						//remove node if value is set to empty
						if ("".equals(value.toString()) || value == null) {
							Element element = (Element)parent.getValue();
							element.removeAttribute(attrName);
							return null;
						}
						
						return value;
					}

				}).setBeforeSetValidator(new IValidator() {

					public IStatus validate(Object value) {
						//dont set value if it is null. Thus no parent node gets created.
						//The parent node has just been deleted in converter
						return value == null ? Status.CANCEL_STATUS : Status.OK_STATUS;
					}
					
				}),
				new UpdateValueStrategy().setConverter(new StringToMethodConverter(
						existingMethods, 
						observedView.getViewer())));		
	}
	
	private Binding bindAttr(IDOMDocument document, final IObservableValue parent, final String attrName, IObservableValue observedView) {
		IObservableValue observedValue = SSEDOMObservables.observeDetailAttrValue(Realm.getDefault(), parent, attrName);
		
		//do the binding
		return dataBindingContext.bindValue(
				observedView,
				observedValue,
				new UpdateValueStrategy().setConverter(new Converter(String.class, String.class) {
					
					public Object convert(Object fromObject) {
						//remove node if value is set to empty
						if ("".equals(fromObject.toString())) {
							Element element = (Element)parent.getValue();
							element.removeAttribute(attrName);
							return null;
						}
						
						return fromObject;
					}

				}).setBeforeSetValidator(new IValidator() {

					public IStatus validate(Object value) {
						//dont set value if it is null. Thus no parent node gets created.
						//The parent node has been deleted in converter
						return value == null ? Status.CANCEL_STATUS : Status.OK_STATUS;
					}
					
				}),
				null);			
	}

	private Binding bindValue(IDOMDocument document, IObserving parent, String elementName, IObservableValue observedView) {
		final IObservableValue observedElement = 
			ObservableUtils.observeLazyValue(
					container, 
					document, 
					parent, 
					elementName);
		
		return bindValue(document, observedElement, observedView);
	}
	
	private Binding bindValue(IDOMDocument document, final IObservableValue observedElement, IObservableValue observedView) {
		IObservableValue observedValue = SSEDOMObservables.observeDetailCharacterData(Realm.getDefault(), observedElement);
		
		//do the binding
		return dataBindingContext.bindValue(
				observedView,
				observedValue,
				new UpdateValueStrategy().setConverter(new Converter(String.class, String.class) {
					
					public Object convert(Object fromObject) {
						Element element = (Element)((IObserving)observedElement).getObserved();
						
						//remove node if value is set to empty
						if ("".equals(fromObject.toString()) && element != null) {
							Element parentNode = (Element)element.getParentNode();
							parentNode.removeChild(element);

							//Remove the parent node also, if this was the last child
							Element[] remainingElements = DOMUtils.getElements(parentNode);
							if (remainingElements == null || remainingElements.length == 0) {
								parentNode.getParentNode().removeChild(parentNode);
							}
							
							return null;
						}
						
						return fromObject;
					}

				}).setBeforeSetValidator(new IValidator() {

					public IStatus validate(Object value) {
						//dont set value if it is null. Thus no parent node gets created.
						//The parent node has been deleted in converter
						return value == null ? Status.CANCEL_STATUS : Status.OK_STATUS;
					}
					
				}),
				null);			
	}
	
	private IObservableList createInputObservableList(ILazyObserving parent, String mainElementName, String elementName) {
		IObservableList observableList = null;
		if (mainElementName == null)
			observableList = SSEDOMObservables.observeDetailList(Realm.getDefault(), (IObservableValue)parent, elementName);
		else {
			IObservableValue observedMainElement = ObservableUtils.observeLazyValue(
					container, 
					document, 
					parent, 
					mainElementName);
			
			observableList = SSEDOMObservables.observeDetailList(Realm.getDefault(), observedMainElement, elementName) ;
		}
		
		return observableList;
	}
	
	private void bindList(ILazyObserving parent, String mainElementName, String elementName, final AbstractTableViewer view) {
		view.setContentProvider(new ObservableListContentProvider() {
			
			@Override
			public Object[] getElements(Object inputElement) {
				Object[] returnValue = super.getElements(inputElement);
				
				//FIXME ugly for collapsing section if no values
				IObservableList observableList = (IObservableList)inputElement;
				Section section = (Section)view.getData("section");
				int count = observableList.size();
	        	section.setExpanded(count > 0);
	        	
				return returnValue;
			}
			
		});	
		
		IObservableList observableList = createInputObservableList(parent, mainElementName, elementName);
		view.setInput(observableList);
	}

	private void bindTree(ILazyObserving parent, String mainElementName, String elementName, TreeViewer view) {
		//IObservableList observableList = createInputObservableList(parent, mainElementName, elementName);
		view.setInput(parent);
	}
	
	public void setInput(IEditorInput input, IModelStateListener listener) {
		file = ((IFileEditorInput) input).getFile();
		try {
			this.model = getDOMModel(file, listener);
			this.document = model.getDocument();
		} catch (Exception e) {
			throw new RuntimeException("Invalid Input: Must be DOM", e);
		}
		
		container = new DOMModelInstanceObservedContainer(model);
	}
	
	// Get DOM Model
	private IDOMModel getDOMModel(IFile file, IModelStateListener listener) throws Exception {

		IModelManager manager = StructuredModelManager.getModelManager();
		IStructuredModel model = manager.getExistingModelForRead(file);
		if (model == null) {
			model = manager.getModelForRead(file);
		}
		if (model == null) {
			throw new Exception(
					"DOM Model is null, check the content type of your file (it seems that it's not *.xml file)");
		}
		if (!(model instanceof IDOMModel)) {
			throw new Exception("Model getted is not DOM Model!!!");
		}

		model.addModelStateListener(listener);

		// model.setReinitializeNeeded(true);

		return (IDOMModel) model;
	}
	
	public IDOMModel getModel() {
		return model;
	}
	
	public IFile getFile() {
		return file;
	}

}