/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted 
 * by the author tags.
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
import java.net.Proxy;

import com.redhat.ceylon.cmr.api.CmrRepository;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.cmr.spi.StructureBuilder;
import com.redhat.ceylon.common.log.Logger;

/**
 * Maven repository helper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MavenRepositoryHelper {

    static File getMavenHome() {
        File mvnHome = new File(System.getProperty("user.home"), ".m2/repository");
        if (mvnHome.exists())
            return mvnHome;

        final String property = System.getProperty("maven.home");
        if (property != null) {
            mvnHome = new File(property, "repository");
            if (mvnHome.exists())
                return mvnHome;
        }

        throw new IllegalArgumentException("No Maven repository found!");
    }

    public static CmrRepository getMavenRepository() {
        return new MavenRepository(new MavenContentStore().createRoot());
    }

    public static CmrRepository getMavenRepository(File mvnRepository) {
        return new MavenRepository(new MavenContentStore(mvnRepository).createRoot());
    }

    public static CmrRepository getMavenRepository(String repositoryURL, Logger log, boolean offline, int timeout, Proxy proxy) {
        return new MavenRepository(new RemoteContentStore(repositoryURL, log, offline, timeout, proxy).createRoot());
    }

    public static CmrRepository getMavenRepository(StructureBuilder structureBuilder) {
        return new MavenRepository(structureBuilder.createRoot());
    }

    private static class MavenContentStore extends FileContentStore {
        private MavenContentStore() {
            this(getMavenHome());
        }

        private MavenContentStore(File root) {
            super(root);
        }

        @Override
        protected void delete(File file, Node node) {
            // cannot delete from Maven repo
        }

        @Override
        public boolean canHandleFolders() {
            return false;
        }
    }
}
