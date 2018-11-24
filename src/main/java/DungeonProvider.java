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
import org.terasology.math.geom.Rect2i;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Produces(DungeonFacet.class)
@Requires(@Facet(value = SurfaceHeightFacet.class, border = @FacetBorder(sides = 4)))
public class DungeonProvider implements ConfigurableFacetProvider {

    private Noise noise;
    private DungeonsConfiguration configuration = new DungeonsConfiguration();

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(DungeonFacet.class).extendBy(0, 8, 4);
        DungeonFacet facet = new DungeonFacet(region.getRegion(), border);
        SurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(SurfaceHeightFacet.class);

        Rect2i worldRegion = surfaceHeightFacet.getWorldRegion();

        for (int wz = worldRegion.minY(); wz <= worldRegion.maxY(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(wx, wz));

                if (surfaceHeight >= facet.getWorldRegion().minY() && surfaceHeight <= facet.getWorldRegion().maxY()) {
                    if (noise.noise(wx, wz) > 1 - (configuration.DungeonRarity/1000)) {
                        facet.setWorld(wx, surfaceHeight, wz, new Dungeon());
                    }
                }
            }
        }
        region.setRegionFacet(DungeonFacet.class, facet);
    }


    @Override
    public String getConfigurationName() {
        return "Dungeons";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (DungeonsConfiguration) configuration;
    }

    /*Allows the user to set mountainHeight in the UI*/
    private static class DungeonsConfiguration implements Component {
        @Range(min = 0f, max = 1000.0f, increment = 1.0f, description = "Dungeon Rarity")
        private float DungeonRarity = 1.0f;
    }

}
