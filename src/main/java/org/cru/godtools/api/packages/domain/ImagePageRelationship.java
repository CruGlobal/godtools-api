package org.cru.godtools.api.packages.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class ImagePageRelationship
{
    private UUID id;
    private UUID pageId;
    private UUID imageId;

    public ImagePageRelationship()
    {

    }

    public ImagePageRelationship(Page page, Image image)
    {
        setId(UUID.randomUUID());
        setPageId(page.getId());
        setImageId(image.getId());
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getPageId()
    {
        return pageId;
    }

    public void setPageId(UUID pageId)
    {
        this.pageId = pageId;
    }

    public UUID getImageId()
    {
        return imageId;
    }

    public void setImageId(UUID imageId)
    {
        this.imageId = imageId;
    }
}
