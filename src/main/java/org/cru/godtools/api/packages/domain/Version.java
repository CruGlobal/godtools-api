package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Sets;
import org.cru.godtools.api.images.ImageLookup;
import org.cru.godtools.api.packages.PageLookup;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Version
{
	public static final Integer LATEST_VERSION_NUMBER = -13528;

    UUID id;
    Integer versionNumber;
    boolean released;
    UUID translationId;
    Integer minimumInterpreterVersion;
    Document packageStructure;
    String packageStructureHash;

    public Version()
    {

    }

    public Version(Translation translation, Integer versionNumber, boolean released)
    {
        setId(UUID.randomUUID());
        setTranslationId(translation.getId());
        setVersionNumber(versionNumber);
        setReleased(released);
        setMinimumInterpreterVersion(1);
    }

	public void replacePageNamesWithPageHashes(PageLookup pageLookup)
	{
		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getPackageStructure(), "page", "filename"))
		{
			String filename = element.getAttribute("filename");
			Document xmlPage = pageLookup.findByFilename(filename);
			element.setAttribute("filename", ShaGenerator.calculateHash(xmlPage) + ".xml");
		}
		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getPackageStructure(), "about", "filename"))
		{
			String filename = element.getAttribute("filename");
			Document xmlPage = pageLookup.findByFilename(filename);
			element.setAttribute("filename", ShaGenerator.calculateHash(xmlPage) + ".xml");
		}
	}

	public void replaceThumbnailNamesWithImageHashes(ImageLookup imageLookup)
	{
		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getPackageStructure(), "page", "thumb"))
		{
			String filename = element.getAttribute("thumb");
			BufferedImage image = imageLookup.findByFilename(filename);
			element.setAttribute("thumb", ShaGenerator.calculateHash(image) + ".xml");
		}
	}

	public Set<String> getReferencedImages()
	{
		Set<String> referencedImages = Sets.newHashSet();

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getPackageStructure(), "page", "thumb"))
		{
			referencedImages.add(element.getAttribute("thumb"));
		}

		return referencedImages;
	}

	public void setDraftVersionNumber(Version latestVersion)
	{
		setVersionNumber(latestVersion.getVersionNumber() + 1);
	}

	public void calculateHash()
	{
		setPackageStructureHash(ShaGenerator.calculateHash(getPackageStructure()));
	}

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public Integer getVersionNumber()
    {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber)
    {
        this.versionNumber = versionNumber;
    }

    public boolean isReleased()
    {
        return released;
    }

    public void setReleased(boolean released)
    {
        this.released = released;
    }

    public UUID getTranslationId()
    {
        return translationId;
    }

    public void setTranslationId(UUID translationId)
    {
        this.translationId = translationId;
    }

    public Integer getMinimumInterpreterVersion()
    {
        return minimumInterpreterVersion;
    }

    public void setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
        this.minimumInterpreterVersion = minimumInterpreterVersion;
    }

    public Document getPackageStructure()
    {
        return packageStructure;
    }

    public void setPackageStructure(Document packageStructure)
    {
        this.packageStructure = packageStructure;
    }

    public String getPackageStructureHash()
    {
        return packageStructureHash;
    }

    public void setPackageStructureHash(String packageStructureHash)
    {
        this.packageStructureHash = packageStructureHash;
    }
}
