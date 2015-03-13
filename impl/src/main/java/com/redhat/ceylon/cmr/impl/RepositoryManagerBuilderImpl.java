/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted 
 * by the author tags.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.ceylon.cmr.impl;

import java.io.File;
import java.util.List;

import com.redhat.ceylon.common.log.Logger;
import com.redhat.ceylon.cmr.api.Repository;
import com.redhat.ceylon.cmr.api.RepositoryBuilder;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.cmr.spi.ContentTransformer;
import com.redhat.ceylon.cmr.spi.MergeStrategy;
import com.redhat.ceylon.cmr.spi.OpenNode;

/**
 * Root repository builder.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class RepositoryManagerBuilderImpl extends RepositoryManagerBuilder {

    private RootRepositoryManager repository;
    private Logger log;
    private int timeout;
    private boolean offline;
    private String mavenOverrides;

    public RepositoryManagerBuilderImpl(Logger log, boolean offline, int timeout, String mavenOverrides) {
        repository = new RootRepositoryManager(log, mavenOverrides);
        this.log = log;
        this.timeout = timeout;
        this.offline = offline;
        this.mavenOverrides = mavenOverrides;
        init();
    }

    public RepositoryManagerBuilderImpl(File mainRepository, Logger log, boolean offline, int timeout, String mavenOverrides) {
        repository = new RootRepositoryManager(mainRepository, log, mavenOverrides);
        this.log = log;
        this.timeout = timeout;
        this.offline = offline;
        this.mavenOverrides = mavenOverrides;
        init();
    }

    protected void init() {
        getCache().addService(MergeStrategy.class, new DefaultMergeStrategy());
    }

    private OpenNode getCache() {
        return repository.getCache();
    }

    @Override
    public RepositoryBuilder repositoryBuilder() {
        return new RepositoryBuilderImpl(log, offline, timeout);
    }

    public RepositoryManagerBuilderImpl mergeStrategy(MergeStrategy strategy) {
        getCache().addService(MergeStrategy.class, strategy);
        return this;
    }

    public RepositoryManagerBuilderImpl contentTransformer(ContentTransformer transformer) {
        getCache().addService(ContentTransformer.class, transformer);
        return this;
    }

    public RepositoryManagerBuilderImpl cacheContent() {
        getCache().addService(ContentTransformer.class, new CachingContentTransformer());
        return this;
    }

    protected RepositoryManagerBuilderImpl addExternalRoot(OpenNode externalRoot) {
        repository.addRepository(new DefaultRepository(externalRoot));
        return this;
    }

    public RepositoryManagerBuilderImpl addRepository(Repository externalRoot) {
        repository.addRepository(externalRoot);
        return this;
    }

    public List<String> getRepositoriesDisplayString() {
        return repository.getRepositoriesDisplayString();
    }

    public RepositoryManager buildRepository() {
        return repository;
    }

}
