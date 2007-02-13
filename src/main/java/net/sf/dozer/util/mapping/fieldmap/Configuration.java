/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.fieldmap;

import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.util.MapperConstants;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class Configuration {

  private boolean wildcard = MapperConstants.DEFAULT_WILDCARD_POLICY;
  private boolean stopOnErrors = MapperConstants.DEFAULT_ERROR_POLICY;
  private String dateFormat;
  private String beanFactory;
  private CustomConverterContainer customConverters;
  private CopyByReferenceContainer copyByReferences;
	private AllowedExceptionContainer allowedExceptions;
  private boolean isAccessible;

  public AllowedExceptionContainer getAllowedExceptions() {
		return allowedExceptions;
	}

	public void setAllowedExceptions(AllowedExceptionContainer allowedExceptions) {
		this.allowedExceptions = allowedExceptions;
	}

  public CustomConverterContainer getCustomConverters() {
    return customConverters;
  }

  public void setCustomConverters(CustomConverterContainer customConverters) {
    this.customConverters = customConverters;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String format) {
    dateFormat = format;
  }

  public boolean getWildcard() {
    return wildcard;
  }

  public void setWildcard(boolean globalWildcardPolicy) {
    wildcard = globalWildcardPolicy;
  }

  public boolean getStopOnErrors() {
    return stopOnErrors;
  }

  public void setStopOnErrors(boolean stopOnErrors) {
    this.stopOnErrors = stopOnErrors;
  }

  public String getBeanFactory() {
    return beanFactory;
  }

  public void setBeanFactory(String beanFactory) {
    this.beanFactory = beanFactory;
  }

  public CopyByReferenceContainer getCopyByReferences() {
    return copyByReferences;
  }

  public void setCopyByReferences(CopyByReferenceContainer copyByReferenceContainer) {
    this.copyByReferences = copyByReferenceContainer;
  }

  public boolean isAccessible() {
    return isAccessible;
  }

  public void setAccessible(boolean isAccessible) {
    this.isAccessible = isAccessible;
  }
}