package org.cru.godtools.api.meta;

import com.google.common.collect.Sets;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.api.packages.utils.LanguageCode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by ryancarlson on 3/31/14.
 */
public class MetaLanguage
{
    String code;
    Set<MetaPackage> packages = Sets.newHashSet();

    public MetaLanguage()
    {

    }

    public MetaLanguage(Language language)
    {
        setCode(LanguageCode.fromLanguage(language).toString());
    }

    public void addPackage(String packageName, String packageCode, BigDecimal versionNumber, boolean isReleased)
    {
        packages.add(new MetaPackage(packageName, packageCode, versionNumber, isReleased));
    }

    @XmlAttribute
    public String getCode()
    {
        return code;
    }

    public MetaLanguage setCode(String code)
    {
        this.code = code;
        return this;
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
