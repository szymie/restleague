package org.tiwpr.szymie.resources;

import org.tiwpr.szymie.models.Fixture;
import org.tiwpr.szymie.models.TablePosition;
import java.util.Map;

@FunctionalInterface
public interface UpdatePositions {
    public void update(Fixture fixture, Map<Integer, TablePosition> positions);
}
