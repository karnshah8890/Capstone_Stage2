package com.ks.redditreader.model;

import com.ks.redditreader.utils.Utils;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

@SimpleSQLConfig(
        name = "SubmissionsProvider",
        authority = Utils.CONTENT_PROVIDER_AUTHORITY,
        database = "database.db",
        version = 1)
public class SubmissionsProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}