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

package com.redhat.ceylon.cmr.maven;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.NonTransitiveFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SingleScopedStrategy implements MavenResolutionStrategy {
    private static final MavenResolutionFilter[] EMPTY_CHAIN = new MavenResolutionFilter[]{};

    private MavenResolutionFilter[] filters;
    private TransitiveExclusionPolicy tep;

    SingleScopedStrategy(final ScopeType... scopes) {
        final AcceptScopesStrategy scopesStrategy = new AcceptScopesStrategy(scopes);
        final MavenResolutionFilter[] scopesFilters = scopesStrategy.getResolutionFilters();
        filters = new MavenResolutionFilter[scopesFilters.length];
        for (int i = 0; i < filters.length; i++) {
            final int index = i;
            filters[i] = new MavenResolutionFilter() {
                @Override
                public boolean accepts(MavenDependency dependency,
                        List<MavenDependency> dependenciesForResolution, List<MavenDependency> dependencyAncestors) {
                    return scopesFilters[index].accepts(dependency, dependenciesForResolution, dependencyAncestors) && NonTransitiveFilter.INSTANCE.accepts(dependency, dependenciesForResolution, dependencyAncestors);
                }
            };
        }
        tep = new SingleTransitiveExclusionPolicy(scopes);
    }

    public MavenResolutionFilter[] getPreResolutionFilters() {
        return EMPTY_CHAIN;
    }

    public MavenResolutionFilter[] getResolutionFilters() {
        return filters;
    }

    public TransitiveExclusionPolicy getTransitiveExclusionPolicy() {
        return tep;
    }

    private class SingleTransitiveExclusionPolicy implements TransitiveExclusionPolicy {
        private ScopeType[] scopes;

        private SingleTransitiveExclusionPolicy(ScopeType[] scopes) {
            EnumSet<ScopeType> used = EnumSet.copyOf(Arrays.asList(scopes));
            Set<ScopeType> set = EnumSet.complementOf(used);
            this.scopes = set.toArray(new ScopeType[set.size()]);
        }

        public boolean allowOptional() {
            return false;
        }

        public ScopeType[] getFilteredScopes() {
            return scopes;
        }
    }
}
