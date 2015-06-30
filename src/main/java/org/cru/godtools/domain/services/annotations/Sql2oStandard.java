package org.cru.godtools.domain.services.annotations;

import javax.inject.*;
import java.lang.annotation.*;

/**
 * Created by justinsturm on 6/30/15.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD, ElementType.PARAMETER})
public @interface Sql2oStandard {}
