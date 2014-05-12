package org.cru.godtools.api.meta;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by ryancarlson on 3/26/14.
 */

@XmlRootElement(name = "languages")
public class MetaResults implements java.io.Serializable
{

    Set<MetaLanguage> languages = Sets.newHashSet();


    public MetaResults()
    {

    }

    public void addLanguage(MetaLanguage metaLanguage)
    {
        languages.add(metaLanguage);
    }

    @XmlElement(name = "language")
    public Set<MetaLanguage> getLanguages()
    {
        return languages;
    }

    public void setLanguages(Set<MetaLanguage> languages)
    {
        this.languages = languages;
    }
}
