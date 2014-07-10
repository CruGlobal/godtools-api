package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Sets;
import org.cru.godtools.api.images.ImageLookup;
import org.cru.godtools.api.packages.utils.GuavHashGenerator;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Page
{
    private UUID id;
    private UUID versionId;
    private Document xmlContent;
    private String description;
    private String pageHash;

	/**
	 * used only during migration and processing of new packages.  is NOT to be stored in the database!
	 */
	private String filename;

	public void replaceImageNamesWithImageHashes(ImageLookup imageLookup)
	{
		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
		{
			String filename = element.getAttribute("backgroundimage");
			BufferedImage image = imageLookup.findByFilename(filename);
			element.setAttribute("backgroundimage", GuavHashGenerator.calculateHash(image) + ".png");
		}

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			String filename = element.getAttribute("watermark");
			BufferedImage image = imageLookup.findByFilename(filename);
			element.setAttribute("watermark", GuavHashGenerator.calculateHash(image) + ".png");
		}

		for (Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			String filename = element.getTextContent();
			BufferedImage image = imageLookup.findByFilename(filename);
			element.setTextContent(GuavHashGenerator.calculateHash(image) + ".png");
		}
	}

	public Set<String> getReferencedImages()
	{
		Set<String> referencedImages = Sets.newHashSet();

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "backgroundimage"))
		{
			referencedImages.add(element.getAttribute("backgroundimage"));
		}

		for (Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "watermark"))
		{
			referencedImages.add(element.getAttribute("watermark"));
		}

		for (Element element : XmlDocumentSearchUtilities.findElements(getXmlContent(), "image"))
		{
			referencedImages.add(element.getTextContent());
		}

		return referencedImages;
	}

	public void calculateHash()
	{
		setPageHash(GuavHashGenerator.calculateHash(getXmlContent()));
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

    public String getPageHash()
    {
        return pageHash;
    }

    public void setPageHash(String pageHash)
    {
        this.pageHash = pageHash;
    }

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}
}
