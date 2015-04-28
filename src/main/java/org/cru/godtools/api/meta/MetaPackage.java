package org.cru.godtools.api.meta;

import org.cru.godtools.domain.GodToolsVersion;

import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 3/27/14.
 */
public class MetaPackage  implements java.io.Serializable
{
    String code;
	String version;
	String status;

    public MetaPackage(String packageCode, GodToolsVersion versionNumber, boolean isReleased)
    {
        setCode(packageCode);
        if(versionNumber != null)setVersion(versionNumber.toString());
		setStatus(isReleased ? "live" : "draft");
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

    @XmlAttribute
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

	@XmlAttribute()
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
