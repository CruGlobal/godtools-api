package org.cru.godtools.api.meta;

import com.google.common.collect.Sets;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;

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
    String name;
    Set<MetaPackage> packages = Sets.newHashSet();

    public MetaLanguage()
    {

    }

    public MetaLanguage(Language language)
    {
        code = LanguageCode.fromLanguage(language).toString();
        name = language.getName();
    }

    public void addPackage(String packageCode, String versionNumber, boolean isReleased)
    {
        packages.add(new MetaPackage(packageCode, versionNumber, isReleased));
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

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public MetaLanguage setName(String name)
    {
        this.name = name;
        return this;
    }

    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    public Set<MetaPackage> getPackages()
    {
        return packages;
    }
}
