package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Sets;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class ImagePageRelationshipService
{
    Connection sqlConnection;
    ImageService imageService;

    @Inject
    public ImagePageRelationshipService(Connection sqlConnection, ImageService imageService)
    {
        this.sqlConnection = sqlConnection;
        this.imageService = imageService;
    }

    public void insert(ImagePageRelationship imagePageRelationship)
    {
        sqlConnection.createQuery(ImagePageRelationshipQueries.insert)
                .addParameter("id", imagePageRelationship.getId())
                .addParameter("pageId", imagePageRelationship.getPageId())
                .addParameter("imageId", imagePageRelationship.getImageId())
                .executeUpdate();
    }

    public Set<Image> selectImagesByPageId(UUID pageId, PixelDensity pixelDensity)
    {
        List<ImagePageRelationship> relationships = sqlConnection.createQuery(ImagePageRelationshipQueries.selectByPageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("pageId", pageId)
                .executeAndFetch(ImagePageRelationship.class);

        Set<Image> images = Sets.newHashSet();

        for(ImagePageRelationship relationship : relationships)
        {
            Image image = imageService.selectById(relationship.getImageId());
            if(image != null && image.getResolution().equalsIgnoreCase(pixelDensity.toString())) images.add(image);
        }

        return images;
    }

    public Set<Image> selectImagesForAllPages(List<Page> pages, PixelDensity pixelDensity)
    {
        Set<Image> images = Sets.newHashSet();

        for(Page page : pages)
        {
            images.addAll(selectImagesByPageId(page.getId(), pixelDensity));
        }

        return images;
    }

    public void delete(UUID id)
    {
        sqlConnection.createQuery(ImagePageRelationshipQueries.deleteById)
                .addParameter("id", id)
                .executeUpdate();
    }

    public void delete(UUID pageId, UUID imageId)
    {
        sqlConnection.createQuery(ImagePageRelationshipQueries.deleteByPageIdImageId)
                .addParameter("pageId", pageId)
                .addParameter("imageId", imageId)
                .executeUpdate();
    }

    public static class ImagePageRelationshipQueries
    {
        public static final String selectByPageId = "SELECT * FROM page_images where page_id = :pageId";
        public static final String insert = "INSERT into page_images(id, page_id, image_id) VALUES(:id, :pageId, :imageId)";
        public static final String deleteById = "DELETE FROM page_images WHERE id = :id";
        public static final String deleteByPageIdImageId = "DELETE FROM page_images WHERE page_id = :pageId AND image_id = :imageId";
    }
}
