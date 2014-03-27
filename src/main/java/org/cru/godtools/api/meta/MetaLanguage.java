package org.cru.godtools.api.meta;


import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Set;

/**
 * Created by ryancarlson on 3/27/14.
 */
public class MetaLanguage implements java.io.Serializable
{
    @XmlAttribute
    String coder;

    @XmlElement(name = "package")
    @XmlElementWrapper(name = "packages")
    Set<MetaPackage> packages = Sets.newHashSet();

    public String getCode()
    {
        return coder;
    }

    public void setCode(String code)
    {
        this.coder = code;
    }

    public Set<MetaPackage> getPackages()
    {
        return packages;
    }

    public void setPackages(Set<MetaPackage> packages)
    {
        this.packages = packages;
    }
}
