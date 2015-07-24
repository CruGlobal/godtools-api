package org.cru.godtools.domain.languages;

import com.google.common.base.Strings;
import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.packages.Package;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.*;

/**
 * Created by ryancarlson on 3/20/14.
 */
@Entity
@Table(name="languages")
public class Language implements Serializable
{
    @Id
    @Column(name="id")
    @Type(type="pg-uuid")
    UUID id;
    @Column(name="name")
    String name;
    @Column(name="code")
    String code;
    @Column(name="locale")
    String locale;
    @Column(name="subculture")
    String subculture;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "defaultLanguage", cascade = {CascadeType.PERSIST})
    Set<Package> packages = new HashSet<>();

    public String getPath()
    {
        String path = code;
        if(!Strings.isNullOrEmpty(locale)) path = path + "-" + locale;
        if(!Strings.isNullOrEmpty(subculture)) path = path + "-" + subculture;
        return path;
    }

	public void setFromLanguageCode(LanguageCode languageCode)
	{
		setCode(languageCode.getLanguageCode());
		setLocale(languageCode.getLocaleCode());
		setSubculture(languageCode.getSubculture());
	}

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getSubculture()
    {
        return subculture;
    }

    public void setSubculture(String subculture)
    {
        this.subculture = subculture;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public void setPackages(Set<Package> packages) {
        this.packages = packages;
    }

    public void addPackageWithDefaultLanguage(Package packageWithDefaultLanguage)
    {
        packages.add(packageWithDefaultLanguage);
    }
}
