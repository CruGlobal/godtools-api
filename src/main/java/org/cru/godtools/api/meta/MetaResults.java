package org.cru.godtools.api.meta;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.LanguageCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/26/14.
 */

@XmlRootElement(name = "language")
public class MetaResults implements java.io.Serializable
{
    String code;

    Set<MetaPackage> packages = Sets.newHashSet();

    public MetaResults()
    {

    }

    public MetaResults(Language language)
    {
        setCode(LanguageCode.fromLanguage(language).toString());
    }

    public MetaResults withPackage(String packageName, String packageCode, Integer versionNumber)
    {
        packages.add(new MetaPackage(packageName, packageCode, versionNumber));
        return this;
    }

    @XmlAttribute
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    public Set<MetaPackage> getPackages()
    {
        return packages;
    }

    public void setPackages(Set<MetaPackage> packages)
    {
        this.packages = packages;
    }
}
