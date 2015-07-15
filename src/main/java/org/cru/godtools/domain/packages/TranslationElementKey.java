package org.cru.godtools.domain.packages;

import javax.persistence.*;
import java.io.*;
import java.util.*;

/**
 * Created by justinsturm on 7/15/15.
 */
@Embeddable
public class TranslationElementKey implements Serializable
{
    private UUID id;
    private UUID translationId;

    @Column(name="id")
    public UUID getImageId()
    {
        return id;
    }

    public void setImageId(UUID id)
    {
        this.id = id;
    }

    @Column(name="translation_id")
    public UUID getPackageStructureId()
    {
        return translationId;
    }

    public void setPackageStructureId(UUID translationId)
    {
        this.translationId = translationId;
    }
}