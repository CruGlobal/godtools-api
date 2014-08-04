package org.cru.godtools.api.meta;

import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 3/27/14.
 */
public class MetaPackage  implements java.io.Serializable
{
    String code;
	BigDecimal version;
	String status;

    public MetaPackage(String packageCode, BigDecimal versionNumber, boolean isReleased)
    {
        setCode(packageCode);
        setVersion(versionNumber);
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
    public BigDecimal getVersion()
    {
        return version;
    }

    public void setVersion(BigDecimal version)
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
