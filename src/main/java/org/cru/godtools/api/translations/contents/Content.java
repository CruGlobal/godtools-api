package org.cru.godtools.api.translations.contents;


import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.GuavaHashGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Set;

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