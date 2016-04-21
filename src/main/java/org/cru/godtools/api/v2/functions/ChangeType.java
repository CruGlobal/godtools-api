package org.cru.godtools.api.v2.functions;

/**
 * Created by laelwatkins on 3/29/16.
 */
public enum ChangeType
{
    ADD_ELEMENTS,REMOVE_ELEMENTS,ADD_REMOVE_ELEMENTS,UPDATE_ELEMENTS,OVERWRITE;

    public static ChangeType fromStringSafely(String possibleChangeType)
    {
        try
        {
            return valueOf(possibleChangeType);
        }
        catch(RuntimeException exception) //todo: actual exception?
        {
            return null;
        }
    }
}
