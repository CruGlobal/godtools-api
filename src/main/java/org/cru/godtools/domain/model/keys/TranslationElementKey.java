package org.cru.godtools.domain.model.keys;

import org.cru.godtools.domain.model.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

/**
 * Created by justinsturm on 7/15/15.
 */
@Embeddable
public class TranslationElementKey implements Serializable
{
    @Column(name="id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name="translation_id")
    private Translation translation;


    public UUID getId()
    {
        return id;
    }
    public void setId(UUID id) { this.id = id; }

    public Translation getTranslation()
    {
        return translation;
    }
    public void setTranslation(Translation translation)
    {
        this.translation = translation;
    }
}