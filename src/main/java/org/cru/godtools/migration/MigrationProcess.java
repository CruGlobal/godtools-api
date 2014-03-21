package org.cru.godtools.migration;

import com.googlecode.flyway.core.Flyway;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class MigrationProcess
{
    public static void main(String[] args)
    {

        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:postgresql://localhost/godtools", "godtoolsuser", "godtoolsuser");
        flyway.setInitVersion("0");
        flyway.clean();
        flyway.init();
        flyway.migrate();

    }
}
