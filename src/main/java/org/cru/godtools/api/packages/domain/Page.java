package org.cru.godtools.api.packages.domain;

import org.w3c.dom.Document;

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
}
