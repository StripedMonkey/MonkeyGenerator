/*
 * Copyright 2018 MovingBlocks
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

import org.terasology.entitySystem.Component;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
@Updates(@Facet(SurfaceHeightFacet.class))
public class LakesProvider implements FacetProviderPlugin {

    private Noise lakes;
    //private LakesConfiguration configuration = new LakesConfiguration();

    @Override
    public void setSeed(long seed) {
        lakes = new PerlinNoise(seed * 3/4);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet facet = region.getRegionFacet(SurfaceHeightFacet.class);
        float depth = 5; //configuration.Depth;

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            float additiveLakeDepth = lakes.noise(position.x(), position.y()) * depth;
            additiveLakeDepth = TeraMath.clamp(additiveLakeDepth, -depth, 0);
            facet.setWorld(position, facet.getWorld(position) - additiveLakeDepth);

        }
    }

    /*@Override
    public String getConfigurationName() {return "Lakes";}

    @Override
    public Component getConfiguration() {return configuration;}

    @Override
    public void setConfiguration(Component configuration) {this.configuration = (LakesConfiguration) configuration;}

    private static class LakesConfiguration implements Component {
        @Range(min = 0.0f, max = 100.0f, label = "Depth", description = "How deep lakes are")
        private float Depth = 10f;
    }*/
}
