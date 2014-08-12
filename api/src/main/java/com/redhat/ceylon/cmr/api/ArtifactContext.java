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

package com.redhat.ceylon.cmr.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.redhat.ceylon.cmr.spi.ContentOptions;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.cmr.spi.OpenNode;

/**
 * Artifact lookup context.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings("serial")
public class ArtifactContext implements Serializable, ContentOptions {
    public static final String CAR = ".car";
    public static final String JAR = ".jar";
    public static final String JS_MODEL = "-model.js";
    public static final String JS = ".js";
    public static final String RESOURCES = "module-resources";
    public static final String SRC = ".src";
    public static final String MAVEN_SRC = "-sources.jar";
    public static final String DOCS = "module-doc";
    public static final String DOCS_ZIPPED = ".doc.zip";
    public static final String MODULE_PROPERTIES = "module.properties";
    public static final String MODULE_XML = "module.xml";
    public static final String SHA1 = ".sha1";
    public static final String INFO = ".info";
    public static final String SCRIPTS_ZIPPED = ".scripts.zip";
    public static final String ZIP = ".zip";

    // IMPORTANT: Makes sure the elements in this array are ordered in such
    // a way that no ambiguities can occur when matching them one at a time.
    // So for example DOCS_ZIPPED must appear before ZIP
    private static final String fileSuffixes[] = {
        CAR, JAR, JS_MODEL, JS, RESOURCES, SRC, MAVEN_SRC, DOCS,
        DOCS_ZIPPED, SHA1, INFO, SCRIPTS_ZIPPED, ZIP
    };
    
    private static final String fileNames[] = {
        MODULE_PROPERTIES, MODULE_XML
    };
    
    private static final String directoryNames[] = {
        RESOURCES, DOCS
    };
    
    public static final List<String> allSuffixes() {
        ArrayList<String> all =  new ArrayList<String>(fileSuffixes.length + fileNames.length + directoryNames.length);
        all.addAll(Arrays.asList(fileSuffixes));
        all.addAll(Arrays.asList(fileNames));
        all.addAll(Arrays.asList(directoryNames));
        return all;
    }
    
    private String name;
    private String version;
    private String[] suffixes = {CAR};
    private boolean localOnly;
    private boolean ignoreSHA;
    private boolean ignoreCache;
    private boolean throwErrorIfMissing;
    private boolean forceOperation;
    private boolean forceDescriptorCheck;
    private boolean fetchSingleArtifact;
    private ArtifactCallback callback;
    private Repository repository;

    public ArtifactContext(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public ArtifactContext(String name, String version, String... suffixes) {
        this(name, version);
        this.suffixes = suffixes;
    }

    public ArtifactContext(String name, String version, Repository repository, String... suffixes) {
        this(name, version);
        this.suffixes = suffixes;
    }

    public ArtifactContext() {
    }

    public ArtifactContext getSha1Context() {
        String[] sha1Suffixes = new String[suffixes.length];
        for (int i = 0; i < sha1Suffixes.length; i++) {
            sha1Suffixes[i] = suffixes[i] + SHA1;
        }
        return new ArtifactContext(name, version, sha1Suffixes);
    }

    public ArtifactContext getSuffixContext(String... suffixes) {
        ArtifactContext ac = copy();
        ac.setSuffixes(suffixes);
        return ac;
    }

    public ArtifactContext getDocsContext() {
        return getSuffixContext(DOCS);
    }

    public ArtifactContext getResourcesContext() {
        return getSuffixContext(RESOURCES);
    }

    public ArtifactContext getModuleProperties() {
        return getSuffixContext(MODULE_PROPERTIES);
    }

    public ArtifactContext getModuleXml() {
        return getSuffixContext(MODULE_XML);
    }

    public void toNode(Node node) {
        if (node instanceof OpenNode) {
            final OpenNode on = (OpenNode) node;
            on.addNode(INFO, this);
        }
    }

    public static ArtifactContext fromNode(Node node) {
        final Node ac = (node instanceof OpenNode) ? ((OpenNode) node).peekChild(INFO) : node.getChild(INFO);
        return ac != null ? ac.getValue(ArtifactContext.class) : null;
    }

    public static void removeNode(Node node) {
        if (node instanceof OpenNode) {
            final OpenNode on = (OpenNode) node;
            if (on.peekChild(INFO) != null) {
                on.removeNode(INFO);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(String... suffixes) {
        this.suffixes = suffixes;
    }

    public String getSingleSuffix() {
        if (suffixes.length != 1) {
            throw new RepositoryException("ArtifactContext should have a single suffix");
        }
        return suffixes[0];
    }

    public static String getSuffixFromNode(Node node) {
        String fileName = node.getLabel();
        return getSuffixFromFilename(fileName);
    }

    /**
     * Returns the artifact "suffix" of the file name that gets passed.
     * The word "suffix" is in quotes because it can also return one
     * of the "full name" results like "module-docs" or "module.properties"
     */
    public static String getSuffixFromFilename(String fileName) {
        for (String suffix : fileNames) {
            if (fileName.equals(suffix)) {
                return suffix;
            }
        }
        for (String suffix : directoryNames) {
            if (fileName.equals(suffix)) {
                return suffix;
            }
        }
        for (String suffix : fileSuffixes) {
            if (fileName.endsWith(suffix)) {
                return suffix;
            }
        }
        throw new RepositoryException("Unknown suffix in " + fileName);
    }

    public static String getArtifactName(String name, String version, String suffix) {
        if (isDirectoryName(suffix) || isFullName(suffix))
            return suffix;
        else if (RepositoryManager.DEFAULT_MODULE.equals(name))
            return name + suffix;
        else
            return name + "-" + version + suffix;
    }

    /**
     * Returns true if the filename that was passed is one of the
     * directory artifact names. Eg. "module-docs"
     */
    public static boolean isDirectoryName(String fileName) {
        for (String suffix : directoryNames) {
            if (fileName.equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the filename that was passed is one of the
     * non-suffix artifact names. Eg. "module.properties"
     */
    private static boolean isFullName(String fileName) {
        for (String suffix : fileNames) {
            if (fileName.equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }

    public boolean isIgnoreSHA() {
        return ignoreSHA;
    }

    public void setIgnoreSHA(boolean ignoreSHA) {
        this.ignoreSHA = ignoreSHA;
    }

    public boolean isIgnoreCache() {
        return ignoreCache;
    }

    public void setIgnoreCache(boolean ignoreCache) {
        this.ignoreCache = ignoreCache;
    }

    public boolean isThrowErrorIfMissing() {
        return throwErrorIfMissing;
    }

    public void setThrowErrorIfMissing(boolean throwErrorIfMissing) {
        this.throwErrorIfMissing = throwErrorIfMissing;
    }

    public boolean isForceOperation() {
        return forceOperation;
    }

    public void setForceOperation(boolean forceOperation) {
        this.forceOperation = forceOperation;
    }

    public boolean isForceDescriptorCheck() {
        return forceDescriptorCheck;
    }

    public void setForceDescriptorCheck(boolean forceDescriptorCheck) {
        this.forceDescriptorCheck = forceDescriptorCheck;
    }

    public boolean isFetchSingleArtifact() {
        return fetchSingleArtifact;
    }

    public void setFetchSingleArtifact(boolean fetchSingleArtifact) {
        this.fetchSingleArtifact = fetchSingleArtifact;
    }

    public ArtifactCallback getCallback() {
        return callback;
    }

    public void setCallback(ArtifactCallback callback) {
        this.callback = callback;
    }

    public Repository getSearchRepository() {
        return repository;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getName()).append("-").append(getVersion());
        if (suffixes.length == 1) {
            str.append(suffixes[0]);
        } else {
            str.append("(");
            boolean first = true;
            for (String s : suffixes) {
                if (!first) {
                    str.append("|");
                }
                str.append(s);
                first = false;
            }
            str.append(")");
        }
        return str.toString();
    }

    public boolean forceOperation() {
        return isForceOperation();
    }

    public boolean forceDescriptorCheck() {
        return isForceDescriptorCheck();
    }
    
    public ArtifactContext copy() {
        ArtifactContext ac = new ArtifactContext(name, version, suffixes);
        ac.copySettingsFrom(this);
        ac.repository = repository;
        return ac;
    }
    
    // TODO can't we do this any better?
    public ArtifactContext copySettingsFrom(ArtifactContext ac) {
        ac.localOnly = localOnly;
        ac.ignoreSHA = ignoreSHA;
        ac.ignoreCache = ignoreCache;
        ac.throwErrorIfMissing = throwErrorIfMissing;
        ac.forceOperation = forceOperation;
        ac.forceDescriptorCheck = forceDescriptorCheck;
        ac.fetchSingleArtifact = fetchSingleArtifact;
        ac.callback = callback;
        return ac;
    }
}
