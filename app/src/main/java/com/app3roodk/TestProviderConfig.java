package com.app3roodk;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by Refaat on 6/17/2016.
 */
@SimpleSQLConfig(
        name = "TestProvider",
        authority = "just.some.test_provider.authority",
        database = "test.db",
        version = 1)
public class TestProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}