package org.cru.godtools.api.packages.domain;

import org.w3c.dom.Document;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PackageStructure
{
	private UUID id;
	private UUID packageId;
	private Integer versionNumber;
	private Document xmlContent;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getPackageId()
	{
		return packageId;
	}

	public void setPackageId(UUID packageId)
	{
		this.packageId = packageId;
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
}
