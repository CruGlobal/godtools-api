package org.cru.godtools.domain.services.annotations;

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;

import java.io.*;
import java.sql.*;

/**
 * Created by justinsturm on 7/14/15.
 */
public class SQLXMLType implements UserType
{
    private final int[] sqlTypesSupported = new int[] { Types.VARCHAR };

    @Override
    public int[] sqlTypes() {
        return sqlTypesSupported;
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? null : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException {
        assert(names.length == 1);
        String xmldoc = rs.getString( names[0] );
        return rs.wasNull() ? null : xmldoc;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value, Types.OTHER);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null)
            return null;
        return new String( (String)value );
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (String) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return (String) cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
