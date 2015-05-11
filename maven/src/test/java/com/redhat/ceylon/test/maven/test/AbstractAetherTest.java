/*
 * Copyright 2014 Red Hat inc. and third party contributors as noted
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

package com.redhat.ceylon.test.maven.test;

import java.io.File;
import java.net.URL;

import com.redhat.ceylon.cmr.api.CmrRepository;
import com.redhat.ceylon.cmr.impl.CMRJULLogger;
import com.redhat.ceylon.cmr.maven.AetherRepository;
import com.redhat.ceylon.common.log.Logger;

/**
 * Abstract Aether tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractAetherTest {
    protected static final Logger log = new CMRJULLogger();

    protected CmrRepository createAetherRepository() throws Exception {
        URL settingsURL = getClass().getClassLoader().getResource("maven-settings/settings.xml");
        String settingsXml = new File(settingsURL.toURI()).getPath();
        return AetherRepository.createRepository(log, settingsXml, false, 60000);
    }
}
