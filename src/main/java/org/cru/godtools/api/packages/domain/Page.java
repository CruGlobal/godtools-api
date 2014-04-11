package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Page
{
    private UUID id;
    private UUID versionId;
    private Integer ordinal;
    private Document xmlContent;
    private String description;
    private String filename;
    private String pageHash;

	public void replaceImageNamesWithImageHashes(Map<String, Image> currentTranslationImages)
	{
		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
		{
			String filename = element.getAttribute("backgroundimage");
			Image image = currentTranslationImages.get(filename);
			element.setAttribute("backgroundimage", image.getImageHash() + ".png");
		}

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			String filename = element.getAttribute("watermark");
			Image image = currentTranslationImages.get(filename);
			element.setAttribute("watermark", image.getImageHash() + ".png");
		}

		for (Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			String filename = element.getTextContent();
			Image image = currentTranslationImages.get(filename);
			element.setTextContent(image.getImageHash() + ".png");
		}
	}

	public Set<String> getReferencedImageHashSet()
	{
		Set<String> referencedImages = Sets.newHashSet();

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
		{
			referencedImages.add(element.getAttribute("backgroundimage").replace(".png", ""));
		}

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			referencedImages.add(element.getAttribute("watermark").replace(".png", ""));
		}

		for (Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			referencedImages.add(element.getTextContent().replace(".png", ""));
		}

		return referencedImages;
	}

	public void calculateHash()
	{
		setPageHash(ShaGenerator.calculateHash(getXmlContent()));
	}

	public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getVersionId()
    {
        return versionId;
    }

    public void setVersionId(UUID versionId)
    {
        this.versionId = versionId;
    }

    public Integer getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal)
    {
        this.ordinal = ordinal;
    }

    public Document getXmlContent()
    {
        return xmlContent;
    }

    public void setXmlContent(Document xmlContent)
    {
        this.xmlContent = xmlContent;

    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getPageHash()
    {
        return pageHash;
    }

    public void setPageHash(String pageHash)
    {
        this.pageHash = pageHash;
    }

}
