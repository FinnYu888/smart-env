/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.flow.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * flowable实例配置
 *
 * @author Chill
 */
@Slf4j
@Configuration
public class FlowableBeanConfiguration {

	private static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";

	@Bean
	public FlowableModelerAppProperties flowableModelerAppProperties() {
		return new FlowableModelerAppProperties();
	}

	@Bean
	public Liquibase modelerLiquibase(DataSource dataSource) {
		Liquibase liquibase = null;
		try {
			DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
			database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
			database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());
			liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update("flowable");
			return liquibase;
		} catch (Exception e) {
			throw new InternalServerErrorException("Error creating liquibase database", e);
		} finally {
			closeDatabase(liquibase);
		}
	}

	private void closeDatabase(Liquibase liquibase) {
		if (liquibase != null) {
			Database database = liquibase.getDatabase();
			if (database != null) {
				try {
					database.close();
				} catch (DatabaseException e) {
					log.warn("Error closing database", e);
				}
			}
		}
	}

}
