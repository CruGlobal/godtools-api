package org.cru.godtools.api.meta;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            JAXBContext jaxbContext = JAXBContext.newInstance(getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(this, byteArrayOutputStream);

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }
}
