package org.cru.godtools.api.translations;


import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.NewTranslation;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.*;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class NewTranslationPostData implements Set<NewTranslation>
{
	Set<NewTranslation> newPackageSet;


	public NewTranslationPostData(MultipartFormDataInput formDataInput)
	{
		newPackageSet = Sets.newHashSet();

		Map<String, List<InputPart>> formDataMap = formDataInput.getFormDataMap();

		for (String filename : formDataMap.keySet())
		{
			for (InputPart inputPart : formDataMap.get(filename))
			{
				newPackageSet.add(new NewTranslation(inputPart));
			}
		}
	}

	public Set<NewTranslation> getNewPackageSet()
	{
		return newPackageSet;
	}

	@Override
	public int size()
	{
		return newPackageSet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return newPackageSet.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return newPackageSet.contains(o);
	}

	@Override
	public Iterator<NewTranslation> iterator()
	{
		return newPackageSet.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return newPackageSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return newPackageSet.toArray(a);
	}

	@Override
	public boolean add(NewTranslation newPackage)
	{
		return newPackageSet.add(newPackage);
	}

	@Override
	public boolean remove(Object o)
	{
		return newPackageSet.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return newPackageSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends NewTranslation> c)
	{
		return newPackageSet.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return newPackageSet.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return newPackageSet.removeAll(c);
	}

	@Override
	public void clear()
	{
		newPackageSet.clear();
	}

	@Override
	public boolean equals(Object o)
	{
		return newPackageSet.equals(o);
	}

	@Override
	public int hashCode()
	{
		return newPackageSet.hashCode();
	}
}
