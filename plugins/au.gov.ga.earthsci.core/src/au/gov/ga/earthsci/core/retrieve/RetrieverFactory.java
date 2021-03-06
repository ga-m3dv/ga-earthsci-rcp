/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.core.retrieve;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ga.earthsci.core.retrieve.retriever.DefaultRetriever;

/**
 * Basic implementation of the {@link IRetrieverFactory} interface.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
@Creatable
@Singleton
public class RetrieverFactory implements IRetrieverFactory
{
	public static final String RETRIEVER_EXTENSION_POINT_ID = "au.gov.ga.earthsci.core.retrieve.retrievers"; //$NON-NLS-1$
	public static final String RETRIEVER_EXTENSION_POINT_CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private Set<IRetriever> retrievers = new LinkedHashSet<IRetriever>();
	private ReadWriteLock retrieversLock = new ReentrantReadWriteLock();

	private IRetriever defaultRetriever = new DefaultRetriever();

	private final static Logger logger = LoggerFactory.getLogger(RetrieverFactory.class);

	/**
	 * Load registered {@link IRetriever}s from the provided extension registry.
	 * <p/>
	 * This method will inject dependencies on loaded classes using the provided
	 * eclipse context, as appropriate.
	 * 
	 * @param registry
	 *            The extension registry to search for {@link IRetriever}s
	 * @param context
	 *            The context to use for dependency injection etc.
	 */
	@PostConstruct
	public void loadRetrievers(@Optional IExtensionRegistry registry, IEclipseContext context)
	{
		if (registry == null)
		{
			logger.warn("Extension registry not found, no retrieval service retrievers are registered"); //$NON-NLS-1$
			return;
		}

		logger.info("Registering retrieval service retrievers"); //$NON-NLS-1$
		IConfigurationElement[] config = registry.getConfigurationElementsFor(RETRIEVER_EXTENSION_POINT_ID);
		try
		{
			for (IConfigurationElement e : config)
			{
				final Object o = e.createExecutableExtension(RETRIEVER_EXTENSION_POINT_CLASS_ATTRIBUTE);
				if (o instanceof IRetriever)
				{
					ContextInjectionFactory.inject(o, context);
					context.set(e.getAttribute(RETRIEVER_EXTENSION_POINT_CLASS_ATTRIBUTE), o);
					registerRetriever((IRetriever) o);
				}
			}
		}
		catch (CoreException e)
		{
			logger.error("Exception while loading retrievers", e); //$NON-NLS-1$
		}
	}

	/**
	 * Register a retriever on this service instance.
	 * 
	 * @param retriever
	 *            The retriever to register
	 */
	@Override
	public void registerRetriever(IRetriever retriever)
	{
		if (retriever == null)
		{
			return;
		}

		retrieversLock.writeLock().lock();
		try
		{
			retrievers.add(retriever);
		}
		finally
		{
			retrieversLock.writeLock().unlock();
		}
	}

	@Override
	public IRetriever getRetriever(URL url)
	{
		if (url == null)
		{
			return null;
		}

		retrieversLock.readLock().lock();
		try
		{
			for (IRetriever r : retrievers)
			{
				if (r.supports(url))
				{
					return r;
				}
			}
			return getDefaultRetriever();
		}
		finally
		{
			retrieversLock.readLock().unlock();
		}
	}

	public IRetriever getDefaultRetriever()
	{
		return defaultRetriever;
	}

	public void setDefaultRetriever(IRetriever defaultRetriever)
	{
		this.defaultRetriever = defaultRetriever;
	}
}
