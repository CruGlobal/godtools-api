package org.cru.godtools.api.translations.model;


import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.GuavaHashGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a contents file that has 'meta' information about a God Tools resource.
 *
 * It contains a set of @see Resource s as XML elements or JSON objects..  Each resource represents a package or resource - a piece of content in the God Tools world.
 * A resource could be "Knowing God Personally" for example.  There may be one or many Resources in the set.
 *
 * Resources are not nested inside a 'resources' element -- FIXME: uhh they currently are.
 *
 * Its XML representation looks like this example (some pages omitted):
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 *     <content>
 *         <resource config="2c510fda-fb80-4c22-9792-195f36232b48.xml" icon="30adeee865dd2ff568b11715e9077ffbb851bb65.png" language="fr" name="Connaître Dieu Personnellement" package="kgp" status="live" version="1.1"/>
 *     </content>
 */
@XmlRootElement
public class Content
{
	Set<Resource> resourceSet = Sets.newHashSet();

	public static Content createContentsFile(Collection<GodToolsTranslation> godToolsTranslations, String languageCode)
	{
		Content content = new Content();

		for (GodToolsTranslation godToolsTranslation : godToolsTranslations)
		{
			Resource resource = new Resource();
			resource.setPackageCode(godToolsTranslation.getPackageCode());
			resource.setLanguage(languageCode);
			resource.setConfig(godToolsTranslation.getTranslation().getId() + ".xml");
			resource.setStatus(godToolsTranslation.isDraft() ? "draft" : "live");
			resource.setName(godToolsTranslation.getPackageName());
			resource.setVersion(godToolsTranslation.getVersionNumber().toPlainString());

			if (godToolsTranslation.getIcon() != null)
			{
				resource.setIcon(GuavaHashGenerator.calculateHash(godToolsTranslation.getIcon().getImageContent()) + ".png");
			} else
			{
				resource.setIcon("missing");
			}

			content.resourceSet.add(resource);
		}

		return content;
	}

	@XmlElementWrapper(name = "resources")
	@XmlElement(name = "resource")
	public Set<Resource> getResourceSet()
	{
		return resourceSet;
	}

	public void setResourceSet(Set<Resource> resourceSet)
	{
		this.resourceSet = resourceSet;
	}
}