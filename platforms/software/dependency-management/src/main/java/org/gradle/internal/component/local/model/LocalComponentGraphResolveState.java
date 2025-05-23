/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.component.local.model;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.internal.component.model.ComponentGraphResolveState;
import org.gradle.internal.component.model.GraphSelectionCandidates;
import org.gradle.internal.component.model.VariantGraphResolveState;
import org.jspecify.annotations.Nullable;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A specialized {@link ComponentGraphResolveState} for local components (ie project dependencies).
 *
 * <p>Instances of this type are cached and reused for multiple graph resolutions, possibly in parallel. This means that the implementation must be thread-safe.
 */
@ThreadSafe
public interface LocalComponentGraphResolveState extends ComponentGraphResolveState {

    ModuleVersionIdentifier getModuleVersionId();

    @Override
    LocalComponentGraphResolveMetadata getMetadata();

    /**
     * We currently allow a configuration that has been partially observed for resolution to be modified
     * in a beforeResolve callback.
     *
     * To reduce the number of instances of root component metadata we create, we mark all configurations
     * as dirty and in need of re-evaluation when we see certain types of modifications to a configuration.
     *
     * In the future, we could narrow the number of configurations that need to be re-evaluated, but it would
     * be better to get rid of the behavior that allows configurations to be modified once they've been observed.
     *
     * @see org.gradle.api.internal.artifacts.ivyservice.moduleconverter.DefaultRootComponentMetadataBuilder.MetadataHolder#tryCached(ComponentIdentifier)
     */
    void reevaluate();

    @Override
    LocalComponentGraphSelectionCandidates getCandidatesForGraphVariantSelection();

    interface LocalComponentGraphSelectionCandidates extends GraphSelectionCandidates {

        /**
         * Get all variants that can be selected for this component. This includes both:
         * <ul>
         *     <li>Variant with attributes: those which can be selected through attribute matching</li>
         *     <li>Variant without attributes: those which can be selected by configuration name</li>
         * </ul>
         */
        List<LocalVariantGraphResolveState> getAllSelectableVariants();

        /**
         * Returns the variant that is identified by the given configuration name.
         */
        @Nullable
        VariantGraphResolveState getVariantByConfigurationName(String name);

    }

}
