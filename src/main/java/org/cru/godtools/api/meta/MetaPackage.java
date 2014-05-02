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
    String name;

    public MetaPackage()
    {
    }

    public MetaPackage(String packageName, String packageCode, BigDecimal versionNumber)
    {
        setName(packageName);
        setCode(packageCode);
        setVersion(versionNumber);
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

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
