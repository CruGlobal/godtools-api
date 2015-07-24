package org.cru.godtools.api.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public InputStream asStream()
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(byteArrayOutputStream, this);

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
        catch (IOException e)
        {
            throw Throwables.propagate(e);
        }
    }
}
