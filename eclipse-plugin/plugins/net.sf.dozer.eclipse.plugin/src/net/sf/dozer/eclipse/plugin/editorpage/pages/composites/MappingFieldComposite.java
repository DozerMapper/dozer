package net.sf.dozer.eclipse.plugin.editorpage.pages.composites;

import net.sf.dozer.eclipse.plugin.editorpage.DozerModelManager;
import net.sf.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class MappingFieldComposite extends Composite {

	private IObservableValue fieldName;
	private IObservableValue dateFormat;
	private IObservableValue type;
	private IObservableValue setMethod;
	private IObservableValue getMethod;
	private IObservableValue key;
	private IObservableValue isAccessible;
	private IObservableValue mapSetMethod;
	private IObservableValue mapGetMethod;
	private IObservableValue createMethod;
	
	private AbstractListViewer fieldComboViewer;
	private AbstractListViewer getComboViewer;
	private AbstractListViewer setComboViewer;	
	private AbstractListViewer mapGetComboViewer;
	private AbstractListViewer mapSetComboViewer;
	private AbstractListViewer createComboViewer;	
	
	private Section optionSection;
	
	public MappingFieldComposite(Composite parent, FormToolkit toolkit, final DozerModelManager modelManager) {
		super(parent, SWT.NULL);
		
		//FIXME otherwise its transparent!?!?
		this.setBackground(new Color(Display.getCurrent(), 255,255,255));
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		this.setLayout(layout);

		fieldComboViewer = DozerUiUtils.createLabelFieldCombobox(
				this, 
				"MappingSection.fieldName"); //$NON-NLS-1$		
		fieldName = ViewersObservables.observeSingleSelection(fieldComboViewer);

		optionSection = toolkit.createSection(this, Section.TWISTIE | Section.SHORT_TITLE_BAR);
		optionSection.setText("Field Options");
		
		Composite sectionComposite = toolkit.createComposite(optionSection);
		optionSection.setClient(sectionComposite);
		
		TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		optionSection.setLayoutData(td);
	
		TableWrapLayout layout2 = new TableWrapLayout();
		layout2.numColumns = 2;
		sectionComposite.setLayout(layout2);
		
		dateFormat = DozerUiUtils.createLabelText(sectionComposite, "ConfigSection.dateformat"); //$NON-NLS-1$	
		
		DozerUiUtils.createLabel(sectionComposite, "FieldSection.type");
		Composite comboComposite = toolkit.createComposite(sectionComposite);
		TableWrapLayout comboLayout = new TableWrapLayout();
		comboLayout.numColumns = 5;
		comboLayout.bottomMargin = 0;
		comboLayout.horizontalSpacing = 5;
		comboLayout.leftMargin = 0;
		comboLayout.rightMargin = 0;
		comboLayout.topMargin = 0;
		comboComposite.setLayout(comboLayout);
		
		td = new TableWrapData();
		comboComposite.setLayoutData(td);
		
		type = DozerUiUtils.createCombo(
				comboComposite, 
				"FieldSection.type", new String[] { "", "generic", "iterate" }); //$NON-NLS-1$
		key = DozerUiUtils.createLabelText(
				comboComposite, 
				"FieldSection.key"); //$NON-NLS-1$
		isAccessible = DozerUiUtils.createLabelCombo(
				comboComposite, 
				"FieldSection.isAccessible", new String[] { "", "true", "false" }); //$NON-NLS-1$
		
		getComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.getMethod"); //$NON-NLS-1$
		getMethod = ViewersObservables.observeSingleSelection(getComboViewer);
		
		setComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.setMethod"); //$NON-NLS-1$	
		setMethod = ViewersObservables.observeSingleSelection(setComboViewer);
		
		mapGetComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.mapGetMethod"); //$NON-NLS-1$
		mapGetMethod = ViewersObservables.observeSingleSelection(mapGetComboViewer);
		
		mapSetComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.mapSetMethod"); //$NON-NLS-1$	
		mapSetMethod = ViewersObservables.observeSingleSelection(mapSetComboViewer);

		createComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.createMethod"); //$NON-NLS-1$
		createMethod =  ViewersObservables.observeSingleSelection(createComboViewer);	
	}
	
	public IObservableValue getMapSetMethod() {
		return mapSetMethod;
	}

	public IObservableValue getMapGetMethod() {
		return mapGetMethod;
	}

	public IObservableValue getCreateMethod() {
		return createMethod;
	}

	public Section getOptionSection() {
		return optionSection;
	}

	public IObservableValue getFieldName() {
		return fieldName;
	}

	public IObservableValue getDateFormat() {
		return dateFormat;
	}

	public IObservableValue getType() {
		return type;
	}

	public IObservableValue getSetMethod() {
		return setMethod;
	}

	public IObservableValue getGetMethod() {
		return getMethod;
	}

	public IObservableValue getKey() {
		return key;
	}

	public IObservableValue getIsAccessible() {
		return isAccessible;
	}

	public AbstractListViewer getFieldComboViewer() {
		return fieldComboViewer;
	}

	public AbstractListViewer getGetComboViewer() {
		return getComboViewer;
	}

	public AbstractListViewer getSetComboViewer() {
		return setComboViewer;
	}

	public AbstractListViewer getMapGetComboViewer() {
		return mapGetComboViewer;
	}

	public AbstractListViewer getMapSetComboViewer() {
		return mapSetComboViewer;
	}

	public AbstractListViewer getCreateComboViewer() {
		return createComboViewer;
	}
}