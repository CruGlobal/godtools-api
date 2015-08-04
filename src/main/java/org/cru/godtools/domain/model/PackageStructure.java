package org.cru.godtools.domain.model;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.GuavaHashGenerator;
import org.hibernate.annotations.*;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * Created by ryancarlson on 4/30/14.
 */
@Entity
@Table(name="package_structure")
public class PackageStructure implements Serializable
{
	@Id
	@Column(name="id")
	@Type(type="pg-uuid")
	private UUID id;
	@ManyToOne
	@JoinColumn(name="package_id")
	Package gtPackage;
	@Transient
	private UUID packageId; //Keep for SQL2O
	@Column(name="version_number")
	private Integer versionNumber;
	@Column(name="xml_content")
	@Type(type= "org.cru.godtools.domain.services.usertypes.SQLXMLType")
	private Document xmlContent;
	@Column(name="filename")
	private String filename;

	@Transient
	private final Logger logger = Logger.getLogger(PackageStructure.class);

	public void setTranslatedFields(Map<UUID, TranslationElement> translationElementMap)
	{
		for(Element translatableElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "gtapi-trx-id"))
		{
			try
			{
				UUID translationElementId = UUID.fromString(translatableElement.getAttribute("gtapi-trx-id"));

				if (translationElementMap.containsKey(translationElementId))
				{
					String translatedText = translationElementMap.get(translationElementId).getTranslatedText();
					String elementType = translatableElement.getTagName();

					logger.debug(String.format("Setting translation element: %s with ID: %s to value: %s", elementType, translationElementId.toString(), translatedText));
					translatableElement.setTextContent(translatedText);
				}
			}
			catch(IllegalArgumentException e)
			{
				logger.warn("Invalid UUID... oh well.  Move along");
			}
		}
	}

	public void replacePageNamesWithPageHashes(Map<String, PageStructure> pageStructures)
	{
		for(Element pageElement : XmlDocumentSearchUtilities.findElements(getXmlContent(), "page"))
		{
			String filenameFromXml = pageElement.getAttribute("filename");
			pageElement.setAttribute("filename", pageStructures.get(filenameFromXml).getId()+ ".xml");
		}

		for(Element pageElement : XmlDocumentSearchUtilities.findElements(getXmlContent(), "about"))
		{
			String filenameFromXml = pageElement.getAttribute("filename");
			pageElement.setAttribute("filename", pageStructures.get(filenameFromXml).getId() + ".xml");
		}
	}

	public void replaceImageNamesWithImageHashes(Map<String, Image> images)
	{
		for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "page", "thumb"))
		{
			try
			{
				String filenameFromXml = pageElement.getAttribute("thumb");
				pageElement.setAttribute("thumb", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
			}
			catch (NullPointerException npe)
			{
				//missing image, life goes on...
			}
		}

		for(Element pageElement : XmlDocumentSearchUtilities.findElementsWithAttribute(getXmlContent(), "about", "thumb"))
		{
			try
			{
				String filenameFromXml = pageElement.getAttribute("thumb");
				pageElement.setAttribute("thumb", GuavaHashGenerator.calculateHash(images.get(filenameFromXml).getImageContent()) + ".png");
			}
			catch(NullPointerException npe)
			{
				//missing image, life goes on...
			}
		}

	}

	public String getPackageName()
	{
		if(xmlContent == null) return "";

		List<Element> packageNameElements = XmlDocumentSearchUtilities.findElements(xmlContent, "packagename");

		if(packageNameElements.size() != 1) throw new IllegalStateException("Expected one packagename element");

		return packageNameElements.get(0).getTextContent();
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public Package getPackage() {
		return gtPackage;
	}

	public void setPackage(Package gtPackage)
	{
		this.gtPackage = gtPackage;
		this.packageId = gtPackage != null ? gtPackage.getId() : null;
	}

	//Required for SQL2O to test properly
	public void setPackageId(UUID packageId)
	{
		if(gtPackage == null)
		{
			gtPackage = new Package();
			gtPackage.setId(packageId);
			this.packageId = packageId;
		}
		else
		{
			this.packageId = gtPackage.getId();
		}
	}

	public Integer getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber)
	{
		this.versionNumber = versionNumber;
	}

	public Document getXmlContent()
	{
		return xmlContent;
	}

	public void setXmlContent(Document xmlContent)
	{
		this.xmlContent = xmlContent;
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