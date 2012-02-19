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

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.Repository;
import com.redhat.ceylon.cmr.api.Resolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default resolver.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultResolver implements Resolver {
    private Repository repository;

    public DefaultResolver(Repository repository) {
        if (repository == null)
            throw new IllegalArgumentException("Null repository!");
        this.repository = repository;
    }

    public File[] resolve(ArtifactContext context) throws IOException {
        final ArtifactResult result = repository.getArtifactResult(context);
        return flatten(result);
    }

    /**
     * Flatten.
     *
     * @param result the artifact result
     * @return all dependencies
     * @throws IOException for any I/O error
     */
    public static File[] flatten(ArtifactResult result) throws IOException {
        if (result == null)
            return null;

        List<File> files = new ArrayList<File>();
        recurse(files, result);
        return files.toArray(new File[files.size()]);
    }

    private static void recurse(final List<File> files, final ArtifactResult current) throws IOException {
        files.add(current.artifact());
        for (ArtifactResult ar : current.dependecies())
            recurse(files, ar);
    }
}
