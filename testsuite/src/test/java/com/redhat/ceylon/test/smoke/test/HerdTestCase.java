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

package com.redhat.ceylon.test.smoke.test;

import java.net.URISyntaxException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import com.redhat.ceylon.cmr.api.ModuleQuery.Type;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.cmr.api.Repository;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.cmr.impl.DefaultRepository;
import com.redhat.ceylon.cmr.webdav.WebDAVContentStore;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * @author Stef Epardaud
 */
public class HerdTestCase extends AbstractTest {

    @Test
    public void testDummy() {
    }

    protected RepositoryManager getRepositoryManager() throws URISyntaxException {
        RepositoryManagerBuilder builder = new RepositoryManagerBuilder(getRepositoryRoot(), log);
        WebDAVContentStore rcs = new WebDAVContentStore("http://localhost:9000/test", log);
        Repository repo = new DefaultRepository(rcs.createRoot());
        return builder.appendRepository(repo).buildRepository();
    }
    
    @Test
    //@Ignore("Required Herd running locally")
    public void testHerdCompleteVersions() throws Exception{
        ModuleVersionDetails[] expected = new ModuleVersionDetails[]{
                new ModuleVersionDetails("0.3.0", "A module for collections \"foo\" `hehe` < 3\n\n    some code `with` \"stuff\" < 𐒅 &lt; &#32; &#x32; 2\n\nboo", "Apache Software License", "Stéphane Épardaud"),
        };
        testListVersions("ceylon.collection", null, expected);
    }

    @Test
    //@Ignore("Required Herd running locally")
    public void testHerdCompleteVersionsFiltered() throws Exception{
        ModuleVersionDetails[] expected = new ModuleVersionDetails[]{
        };
        testListVersions("ceylon.collection", "1.0", expected);
    }

    @Test
    //@Ignore("Required Herd running locally")
    public void testHerdSearch() throws Exception{
        ModuleDetails[] expected = new ModuleDetails[]{
                new ModuleDetails("ceylon.collection", "A module for collections \"foo\" `hehe` < 3\n\n    some code `with` \"stuff\" < 𐒅 &lt; &#32; &#x32; 2\n\nboo", "Apache Software License", set("Stéphane Épardaud"), set("0.3.0")),
                new ModuleDetails("ceylon.language", null, null, set(), set("0.1")),
                new ModuleDetails("com.acme.helloworld", null, null, set(), set("1.0.0", "1.0.2")),
                new ModuleDetails("fr.epardaud.collections", null, null, set(), set("0.1", "0.2")),
                new ModuleDetails("fr.epardaud.iop", null, null, set(), set("0.1")),
                new ModuleDetails("fr.epardaud.json", null, null, set(), set("0.1")),
                new ModuleDetails("fr.epardaud.net", null, null, set(), set("0.2")),
                new ModuleDetails("fr.epardaud.test", null, null, set(), set("0.1")),
                new ModuleDetails("org.apache.commons.httpclient", null, null, set(), set("3.1")),
        };
        testSearchResults("", Type.JVM, expected);
    }

    @Test
    public void testHerdSearchFiltered() throws Exception{
        ModuleDetails[] expected = new ModuleDetails[]{
                new ModuleDetails("ceylon.collection", "A module for collections \"foo\" `hehe` < 3\n\n    some code `with` \"stuff\" < 𐒅 &lt; &#32; &#x32; 2\n\nboo", "Apache Software License", set("Stéphane Épardaud"), set("0.3.0")),
                new ModuleDetails("ceylon.language", null, null, set(), set("0.1")),
        };
        testSearchResults("cey", Type.JVM, expected);
    }

    @Test
    public void testHerdSearchPaged() throws Exception{
        ModuleDetails[] expected = new ModuleDetails[]{
                new ModuleDetails("ceylon.language", null, null, set(), set("0.1")),
                new ModuleDetails("com.acme.helloworld", null, null, set(), set("1.0.0", "1.0.2")),
        };
        testSearchResults("", Type.JVM, expected, 1l, 2l);
    }
}
