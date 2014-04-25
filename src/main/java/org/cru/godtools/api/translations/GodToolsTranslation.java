package org.cru.godtools.api.translations;

import com.google.common.collect.Lists;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsTranslation
{
    protected Document packageXml;
	protected List<Page> pageFiles = Lists.newArrayList();
	protected String languageCode;
	protected String packageCode;
	protected String packageXmlHash;

	protected Version currentVersion;

    public GodToolsTranslation()
    {
    }

    public GodToolsTranslation(Document packageXml, List<Page> pageFiles, Version version, String languageCode, String packageCode)
    {
        this.packageXml = packageXml;
        this.pageFiles = pageFiles;
		this.currentVersion = version;
        this.languageCode = languageCode;
        this.packageCode = packageCode;
        this.packageXmlHash = ShaGenerator.calculateHash(packageXml);
    }

    public Document getPackageXml()
    {
        return packageXml;
    }

    public void setPackageXml(Document packageXml)
    {
        this.packageXml = packageXml;
    }

    public List<Page> getPageFiles()
    {
        return pageFiles;
    }

    public void setPageFiles(List<Page> pageFiles)
    {
        this.pageFiles = pageFiles;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public String getPackageCode()
    {
        return packageCode;
    }

    public void setPackageCode(String packageCode)
    {
        this.packageCode = packageCode;
    }

    public String getPackageXmlHash()
    {
        return packageXmlHash;
    }

    public void setPackageXmlHash(String packageXmlHash)
    {
        this.packageXmlHash = packageXmlHash;
    }

	public Version getCurrentVersion()
	{
		return currentVersion;
	}

	public void setCurrentVersion(Version currentVersion)
	{
		this.currentVersion = currentVersion;
	}
}
