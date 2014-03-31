package org.cru.godtools.api.meta;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.utils.LanguageCode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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

    public MetaLanguage withPackage(String packageName, String packageCode, Integer versionNumber)
    {
        packages.add(new MetaPackage(packageName, packageCode, versionNumber));
        return this;
    }

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
